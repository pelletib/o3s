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
 * $Id: pelletib $
 * --------------------------------------------------------------------------
 */
package net.o3s.web.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.o3s.apis.IEJBAdminRemote;
import net.o3s.apis.IEJBRegisteringRemote;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.NotificationMessage;
import net.o3s.apis.NotificationMessageException;
import net.o3s.web.vo.NotificationVO;


import flex.messaging.MessageBroker;
import flex.messaging.messages.AsyncMessage;

public class Notification implements javax.jms.MessageListener {

    private Session session;
    private Connection connection;
    private static Logger logger = Logger.getLogger(Notification.class.getName());

    //@EJB
    private IEJBRegisteringRemote registering;

    //@EJB
    private IEJBAdminRemote admin;

    //@Resource(mappedName="NotificationTopic")
    //private Topic topic;

    //@Resource(mappedName="CF")
    //private ConnectionFactory connectionFactory;

	private void setRegisteringEJB() {

		InitialContext context=null;

		if (registering == null) {
			try {
				context = new InitialContext();
				registering = (IEJBRegisteringRemote) context.lookup("net.o3s.beans.registering.RegisteringBean_net.o3s.apis.IEJBRegisteringRemote@Remote");

			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	private void setAdminEJB() {

		InitialContext context=null;

		if (admin == null) {
			try {
				context = new InitialContext();
				admin = (IEJBAdminRemote) context.lookup("net.o3s.beans.admin.AdminBean_net.o3s.apis.IEJBAdminRemote@Remote");

			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}


    /* Constructor. Establish JMS publisher and subscriber */
    public Notification( ) throws Exception {

        InitialContext context = new InitialContext();

        // Look up a JMS connection factory
        // setMessageListener() is forbidden on CF
        ConnectionFactory conFactory = (ConnectionFactory) context.lookup("JCF");

        // Create a JMS connection
        connection = conFactory.createConnection();

        session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);

        // Look up a JMS topic
        Topic topic = (Topic) context.lookup("NotificationTopic");

        // Create a JMS subscriber
        MessageConsumer mc = session.createConsumer(topic);

        // Set a JMS message listener
        mc.setMessageListener(this);

        setRegisteringEJB();

        setAdminEJB();

        // Start the JMS connection; allows messages to be delivered
        connection.start( );

        logger.fine("Message listener ok");

    }

    public void sendMessageToClients(NotificationVO notificationVO) {

    	AsyncMessage msg = new AsyncMessage();

    	msg.setClientId("Java-Based-Producer-For-Messaging");

    	msg.setTimestamp(new Date().getTime());

    	//you can create a unique id
    	msg.setMessageId("Java-Based-Producer-For-Messaging-ID");

    	//destination to which the message is to be sent
    	msg.setDestination("notificationMessage");

    	//set message body
    	msg.setBody(notificationVO != null?notificationVO:null);
//    	String body;
//    	if (registeredVO.toString() != null) {
//    		body = registeredVO.toString();
//
//    	} else {
//    		body = "null";
//    	}
    	//msg.setBody(body);

    	//set message header
    	msg.setHeader("sender", "From the server");

    	//send message to destination
    	MessageBroker.getMessageBroker(null).routeMessageToService(msg, null);

    }

    private NotificationVO formatArrivalNotification(IEntityRegistered registered) {
    	NotificationVO notification = new NotificationVO();

    	notification.setCreationTime(new Date());
    	notification.setType(NotificationMessage.NOTIFICATION_STR_TYPE_ARRIVAL);
    	String message = "";
    	message += registered.getCompetition().getName() + "-";
    	message += registered.getCategory().getName() + "-";
    	message += registered.getName() + " in ";


        long timet= registered.getElapsedTime() / 1000;

        long hours = timet/3600;
        long minutes = (timet/60)-(hours*60);
        long seconds = timet-(minutes*60)-(hours*3600);
        long mseconds = registered.getElapsedTime() - (seconds*1000) - (minutes*60*1000) - (hours*3600*1000);

        message += hours + ":" + minutes + ":" + seconds + ":" + mseconds;

    	//Date dateElapsedTime = new Date(registered.getElapsedTime());
    	//DateFormat df = new SimpleDateFormat("HH:mm:ss:S");
    	//message +=  df.format(dateElapsedTime);

    	notification.setMessage(message);

    	return notification;
    }

    private NotificationVO formatDepartureNotification(IEntityCompetition competition) {
    	NotificationVO notification = new NotificationVO();
    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    	notification.setCreationTime(new Date());
    	notification.setType(NotificationMessage.NOTIFICATION_STR_TYPE_DEPARTURE);
    	String message = "";
    	message += competition.getName() + " at ";
    	message += df.format(competition.getStartingDate());

    	notification.setMessage(message);

    	return notification;
    }

    /* Receive message from topic subscriber */
    public synchronized void  onMessage(Message message) {

		if (message instanceof TextMessage) {
			TextMessage text = (TextMessage) message;

			if (text != null) {
				try {
					NotificationMessage notificationMessage = new NotificationMessage();
					notificationMessage.parseXml(text.getText());
					logger.fine("Received Message: " + text.getText());

					if (notificationMessage.getType() == NotificationMessage.NOTIFICATION_INT_TYPE_ARRIVAL) {

						// Find the label in the database
						IEntityRegistered registered = registering.findRegisteredFromId(notificationMessage.getRegisteredId());
						if (registered == null) {
							throw new NotificationMessageException ("Unable to retrieve a registered related to the event:" + notificationMessage);
						}

						logger.fine("Notification - registered arrival:" + registered.getId());
						sendMessageToClients(formatArrivalNotification(registered));
					}

					if (notificationMessage.getType() == NotificationMessage.NOTIFICATION_INT_TYPE_DEPARTURE) {

						// Find the competition in the database
						IEntityCompetition competition = (IEntityCompetition) admin.findCompetitionFromId(notificationMessage.getCompetitionId());

						if (competition == null) {
							throw new NotificationMessageException ("Unable to retrieve a competition related to the event:" + notificationMessage);
						}

			            logger.fine("Notification - departure:" + competition.getId());
						sendMessageToClients(formatDepartureNotification(competition));

					}

				} catch (JMSException e) {
					logger.fine("Unable to process the Notification: " + e.getMessage());
				} catch (NotificationMessageException e) {
					logger.fine("Unable to process the Notification: " + e.getMessage());
				}
				catch (IllegalStateException e) {
					//logger.fine("Unable to process the Notification: " + e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
					logger.fine("Unable to process the Notification: " + e.getMessage());
				}
			}
		}
    }

    /* Close the JMS connection */
    public void close( ) throws JMSException {
        connection.close( );
    }
}
