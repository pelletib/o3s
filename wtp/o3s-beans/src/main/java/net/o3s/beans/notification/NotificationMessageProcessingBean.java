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
package net.o3s.beans.notification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

import net.o3s.apis.IEJBAdminLocal;
import net.o3s.apis.IEJBRegisteringLocal;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.NotificationMessage;
import net.o3s.apis.NotificationMessageException;


@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic") }, mappedName = "NotificationTopic")
public class NotificationMessageProcessingBean implements MessageListener {

    private static Logger logger = Logger.getLogger(NotificationMessageProcessingBean.class.getName());

    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    private IEJBRegisteringLocal registering;

    @EJB
    private IEJBAdminLocal admin;

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(final Message message) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");

		if (message instanceof TextMessage) {
			TextMessage text = (TextMessage) message;
			try {
				NotificationMessage notificationMessage = new NotificationMessage();
				notificationMessage.parseXml(text.getText());
				logger.fine("Received Message: " + text.getText());

				if (notificationMessage.getType() == NotificationMessage.NOTIFICATION_INT_TYPE_ARRIVAL) {

					// Find the registered in the database
					IEntityRegistered registered = (IEntityRegistered) registering.findRegisteredFromId(notificationMessage.getRegisteredId());

					if (registered == null) {
						throw new NotificationMessageException ("Unable to retrieve a registered related to the event:" + notificationMessage);
					}

					int rank = 0;
					try {
						rank = registering.getRanking(registered.getId());
					} catch (Exception e) {
						e.printStackTrace();
						logger.fine("Unable to process the ranking: " + e.getMessage());
					}


		            logger.info("Notification - arrival: <" +
		            		registered.getLabel().getValue() + "," +
		            		registered.getLabel().getRfid() + "," +
		            		registered.getName() + "," +
		            		registered.getCompetition().getName() + "," +
		            		registered.getCategory().getName() + "," +
		            		df.format(registered.getArrivalDate()) + "," +
		            		registered.getElapsedTime() + "," +
                                        rank);
				}

				if (notificationMessage.getType() == NotificationMessage.NOTIFICATION_INT_TYPE_DEPARTURE) {

					// Find the competition in the database
					IEntityCompetition competition = (IEntityCompetition) admin.findCompetitionFromId(notificationMessage.getCompetitionId());

					if (competition == null) {
						throw new NotificationMessageException ("Unable to retrieve a competition related to the event:" + notificationMessage);
					}

		            logger.info("Notification - departure:" +
		            		competition.getName() + "," +
		            		df.format(competition.getStartingDate()));
				}

				if (notificationMessage.getType() == NotificationMessage.NOTIFICATION_INT_TYPE_REGISTERING) {

					// Find the registered in the database
					IEntityRegistered registered = (IEntityRegistered) registering.findRegisteredFromId(notificationMessage.getRegisteredId());

					if (registered == null) {
						throw new NotificationMessageException ("Unable to retrieve a registered related to the event:" + notificationMessage);
					}

		            logger.info("Notification - registering: <" +
		            		registered.getLabel().getValue() + "," +
		            		registered.getLabel().getRfid() + "," +
		            		registered.getName() + "," +
		            		registered.getCompetition().getName() + "," +
		            		registered.getCategory().getName());
				}

				if (notificationMessage.getType() == NotificationMessage.NOTIFICATION_INT_TYPE_ERROR) {

		            logger.info("Notification - error:" + notificationMessage.getMessage());
				}



			} catch (JMSException e) {
				logger.severe("Unable to process the Notification: " + e.getMessage());
			} catch (NotificationMessageException e) {
				logger.severe("Unable to process the Notification: " + e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();

				logger.severe("Unable to process the Notification: " + e.getMessage());
			}
		}

	}

}
