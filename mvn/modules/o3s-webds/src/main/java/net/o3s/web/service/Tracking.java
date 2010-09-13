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
package net.o3s.web.service;

import java.util.Date;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.o3s.apis.IEJBEventMessageProducerRemote;
import net.o3s.apis.TrackingMessage;
import net.o3s.apis.TrackingMessageException;


public class Tracking {

	//@EJB
	private IEJBEventMessageProducerRemote tracking;

	private void setTrackingEJB() {

		InitialContext context=null;

		if (tracking == null) {
			try {
				context = new InitialContext();
				tracking = (IEJBEventMessageProducerRemote) context.lookup("net.o3s.beans.tracking.TrackingMessageProducerBean_net.o3s.apis.IEJBEventMessageProducerRemote@Remote");

			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public Date createArrivalEvent(String origin, String labelValue) {

		setTrackingEJB();

		Date dateCreation = null;

		try {
			dateCreation = tracking.createEvent(origin, labelValue, TrackingMessage.EVENT_INT_TYPE_ARRIVAL);
		} catch (TrackingMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dateCreation;

	}

	public Date createArrivalEvent(String origin, String labelValue, Date date) {

		setTrackingEJB();

		Date dateCreation = null;

		try {
			dateCreation = tracking.createEvent(origin, labelValue, TrackingMessage.EVENT_INT_TYPE_ARRIVAL, date);
		} catch (TrackingMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dateCreation;

	}
}
