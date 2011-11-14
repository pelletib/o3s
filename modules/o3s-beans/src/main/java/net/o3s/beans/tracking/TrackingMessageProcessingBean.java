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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.o3s.apis.IEJBNotificationProducerLocal;
import net.o3s.apis.IEJBRegisteringLocal;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.InvalidException;
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

    @EJB
    private IEJBRegisteringLocal registering;

    @EJB
    private IEJBNotificationProducerLocal notification;

	private void setRegisteringEJB() {

		InitialContext context=null;

		if (registering == null) {
			try {
				context = new InitialContext();
				registering = (IEJBRegisteringLocal) context.lookup("net.o3s.beans.registering.RegisteringBean_net.o3s.apis.IEJBRegisteringLocal@Local");

			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private void setNotificationEJB() {

		InitialContext context=null;

		if (notification == null) {
			try {
				context = new InitialContext();
				notification = (IEJBNotificationProducerLocal) context.lookup("net.o3s.beans.notification.NotificationProducerBean_net.o3s.apis.IEJBNotificationProducerLocal@Local");

			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	/**
	 * Find a registered according to a label value which can have 3 differents form (number, label string, rfid)
	 * @param labelValue to find
	 * @return registered
	 * @throws TrackingMessageException if not found
	 */
	private IEntityRegistered findRegistered(TrackingMessage trackingMessage) throws TrackingMessageException{

		String labelData = trackingMessage.getLabelValue();
		IEntityRegistered registered = null;

		try {
			registered = registering.findRegisteredFromLabelData(labelData);

		} catch (InvalidException e) {
			throw new TrackingMessageException ("Unable to retrieve a registered related to the event:" + trackingMessage);
		}

		if (registered == null) {
			throw new TrackingMessageException ("Unable to retrieve a registered related to the event:" + trackingMessage);
		}

		return registered;

	}

	private void updateArrivalDateRegistered(int id, Date date) throws TrackingMessageException {

		try {
			registering.updateArrivalDateRegistered(id, date);
		} catch (InvalidException e) {
			throw new TrackingMessageException("Impossible de mettre a jour la date d'arrivee ' id=" + id + ", date=" + date, e);
		} catch (RegisteringException e) {
			throw new TrackingMessageException("Impossible de mettre a jour la date d'arrivee ' id=" + id + ", date=" + date, e);
		}

	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(final Message message) {

		if (notification == null) {
			setNotificationEJB();
		}

		if (message instanceof TextMessage) {
			TextMessage text = (TextMessage) message;
			try {
				TrackingMessage trackingMessage = new TrackingMessage();
				trackingMessage.parseXml(text.getText());

				logger.info("Received Message: " + text.getText());

				if (trackingMessage.getType() == TrackingMessage.EVENT_INT_TYPE_ARRIVAL_CTIME) {

					if (registering == null) {
						setRegisteringEJB();
					}

					IEntityRegistered registered = null;
					try {
						registered = findRegistered(trackingMessage);
					} catch (Exception e) {
						throw new TrackingMessageException("Impossible de trouver une personne associee a la valeur '" + trackingMessage.getLabelValue() + "'", e);
					}

					updateArrivalDateRegistered(registered.getId(), trackingMessage.getCreationTime());

		            logger.fine("Update:" + registered);


		            try {
		            	notification.sendArrivalNotification(registered);
					} catch (NotificationMessageException e) {
						logger.severe("Unable to send a notification :" + e.getMessage());
					}

				}

				if (trackingMessage.getType() == TrackingMessage.EVENT_INT_TYPE_ARRIVAL_ETIME) {

					if (registering == null) {
						setRegisteringEJB();
					}

					IEntityRegistered registered = null;
					try {
						registered = findRegistered(trackingMessage);
					} catch (Exception e) {
						throw new TrackingMessageException("Impossible de trouver une personne associee a la valeur '" + trackingMessage.getLabelValue() + "'", e);
					}

					// compute arrival date
					if (registered.getCompetition().getStartingDate() == null) {
						throw new TrackingMessageException ("Competition not started:" + registered.getCompetition().getName());
					}
					Date arrivalDate = new Date(registered.getCompetition().getStartingDate().getTime() + trackingMessage.getElapsedTime());

					updateArrivalDateRegistered(registered.getId(), arrivalDate);

		            logger.fine("Update:" + registered);


		            try {
		            	notification.sendArrivalNotification(registered);
					} catch (NotificationMessageException e) {
						logger.severe("Unable to send a notification :" + e.getMessage());
					}

				}

			} catch (Exception e) {

				String cause = "";
				if (e.getCause() != null) {
					cause = ", " + e.getCause().getMessage();
				}

				logger.log(Level.SEVERE, "Unable to process the Event: " + e.getMessage() + cause, e);

				try {
					notification.sendErrorNotification(e.getMessage() + cause);
				} catch (Exception e1) {
					logger.severe("Unable to send a notification :"
							+ e1.getMessage());
				}
			}
		}

	}

}
