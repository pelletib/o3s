/**
 * O3S: Open Source Sport Software
 * Copyright (C) 2010 Benoit Pelletier
 * Contact: btpelletier@gmail.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 *
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */
package net.o3s.beans.tracking;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.o3s.apis.IEJBNotificationProducerLocal;
import net.o3s.apis.IEJBRegisteringLocal;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.NotificationMessageException;
import net.o3s.apis.RegisteringException;
import net.o3s.apis.TrackingMessage;
import net.o3s.apis.TrackingMessageException;

/**
 * Message-Driven Bean implementation class for: NewOrderBean
 *
 */
@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") }, mappedName = "TrackingQueue")
public class TrackingMessageProcessingBean implements MessageListener {

    private static Logger logger = Logger.getLogger(TrackingMessageProcessingBean.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    private IEJBRegisteringLocal registering;

    @EJB
    private IEJBNotificationProducerLocal notification;


	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(final Message message) {

		if (message instanceof TextMessage) {
			TextMessage text = (TextMessage) message;
			try {
				TrackingMessage trackingMessage = new TrackingMessage();
				trackingMessage.parseXml(text.getText());
				logger.fine("Received Message: " + text.getText());

				if (trackingMessage.getType() == TrackingMessage.EVENT_INT_TYPE_ARRIVAL) {

					// Find the label in the database
					IEntityRegistered registered = registering.findRegisteredFromLabel(trackingMessage.getLabelValue());
					if (registered == null) {
						throw new TrackingMessageException ("Unable to retrieve a registered related to the event:" + trackingMessage);
					}

					// Set the arrival date
					registered.setArrivalDate(trackingMessage.getCreationTime());

					// Compute the duration
					if (registered.getCompetition().getStartingDate() == null) {
						throw new TrackingMessageException ("Competition pas encore demarree pour l'inscrit :" + trackingMessage);
					}
					registered.setElapsedTime(registered.getArrivalDate().getTime()-registered.getCompetition().getStartingDate().getTime());

					//registered = this.entityManager.merge(registered);

		            logger.fine("Update:" + registered);

		            try {
		            	notification.sendArrivalNotification(registered);
					} catch (NotificationMessageException e) {
						logger.severe("Unable to send a notification :" + e.getMessage());
					}

				}

			} catch (JMSException e) {
				logger.severe("Unable to process the Event: " + e.getMessage());
			} catch (TrackingMessageException e) {
				logger.severe("Unable to process the Event: " + e.getMessage());
			} catch (RegisteringException e) {
				logger.severe("Unable to process the Event: " + e.getMessage());
			}
		}

	}

}
