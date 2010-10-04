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

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Queue;


import net.o3s.apis.IEJBEventMessageProducerLocal;
import net.o3s.apis.IEJBEventMessageProducerRemote;
import net.o3s.apis.TrackingMessage;
import net.o3s.apis.TrackingMessageException;



/**
 * Session Bean implementation class RegisteringBean
 */
@Stateless
@Local(IEJBEventMessageProducerLocal.class)
@Remote(IEJBEventMessageProducerRemote.class)
public class TrackingMessageProducerBean implements IEJBEventMessageProducerLocal,IEJBEventMessageProducerRemote {

    private static Logger logger = Logger.getLogger(TrackingMessageProducerBean.class.getName());

    @Resource(mappedName="TrackingQueue")
    private Queue queue;

    @Resource(mappedName="CF")
    private ConnectionFactory connectionFactory;

    public Date createEvent(String origin, String labelValue, int type, Date date) throws TrackingMessageException {

    	try {
        Connection connection = this.connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(this.queue);
        TextMessage message = session.createTextMessage();

        TrackingMessage event = new TrackingMessage();
        event.setOrigin(origin);
        event.setType(type);
        event.setLabelValue(labelValue);
        event.setCreationTime(date);

        message.setText(event.toXML());

        logger.log(Level.FINE,"send event :"+message.getText());

        producer.send(message);
        producer.close();
        session.close();
        connection.close();
    	} catch (Exception e) {
    		throw new TrackingMessageException("Unable to create an event [" + origin + ", " + labelValue + ", " + type + "]", e);
    	}

    	return date;

    }

    public Date createEvent(String origin, String labelValue, int type) throws TrackingMessageException {
    	return createEvent(origin, labelValue, type, new Date());
    }
}
