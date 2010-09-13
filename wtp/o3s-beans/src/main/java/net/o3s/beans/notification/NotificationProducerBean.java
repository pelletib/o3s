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

import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;


import net.o3s.apis.IEJBNotificationProducerLocal;
import net.o3s.apis.IEJBNotificationProducerRemote;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.NotificationMessage;
import net.o3s.apis.NotificationMessageException;


/**
 * Session Bean implementation class RegisteringBean
 */
@Stateless
@Local(IEJBNotificationProducerLocal.class)
@Remote(IEJBNotificationProducerRemote.class)
public class NotificationProducerBean implements IEJBNotificationProducerLocal,IEJBNotificationProducerRemote {

    private static Logger logger = Logger.getLogger(NotificationProducerBean.class.getName());

    @Resource(mappedName="NotificationTopic")
    private Topic topic;

    @Resource(mappedName="CF")
    private ConnectionFactory connectionFactory;

    public void sendDepartureNotification(IEntityCompetition competition) throws NotificationMessageException {
    	try {
            Connection connection = this.connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(this.topic);
            TextMessage message = session.createTextMessage();

            NotificationMessage notification = new NotificationMessage();
            notification.setCompetitionId(competition.getId());
            notification.setType(NotificationMessage.NOTIFICATION_INT_TYPE_DEPARTURE);
            message.setText(notification.toXML());

            producer.send(message);
            producer.close();
            session.close();
            connection.close();

        	} catch (Exception e) {
        		throw new NotificationMessageException("Unable to create an notification [" + competition.getId() + ", " + NotificationMessage.NOTIFICATION_INT_TYPE_DEPARTURE + "]", e);
        	}

    }

    public void sendArrivalNotification(IEntityRegistered registered) throws NotificationMessageException {
    	try {
            Connection connection = this.connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(this.topic);
            TextMessage message = session.createTextMessage();

            NotificationMessage notification = new NotificationMessage();
            notification.setRegisteredId(registered.getId());
            notification.setType(NotificationMessage.NOTIFICATION_INT_TYPE_ARRIVAL);
            message.setText(notification.toXML());

            producer.send(message);
            producer.close();
            session.close();
            connection.close();

    	} catch (Exception e) {
        		throw new NotificationMessageException("Unable to create an notification [" + registered.getId() + ", " + NotificationMessage.NOTIFICATION_INT_TYPE_ARRIVAL + "]", e);
    	}

    }

}
