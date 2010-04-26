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
package net.o3s.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.o3s.apis.IEntityEvent;

/**
 * Entity implementation class for Entity: Event
 *
 */
@Entity
@NamedQueries( {
	@NamedQuery(name = "EVENT_FROM_NAME", query = "SELECT e FROM Event e WHERE e.name = :NAME"),
	@NamedQuery(name = "DEFAULT_EVENT", query = "SELECT e FROM Event e WHERE e.theDefault = 'true'"),
	@NamedQuery(name = "ALL_EVENTS", query = "SELECT e FROM Event e") })
public class Event implements IEntityEvent, Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	@Temporal(TemporalType.DATE)
	private Date date;
	private boolean theDefault = false;

	private static final long serialVersionUID = 1L;

	public Event() {
		super();
	}

	public int getId() {
		return this.id;
	}

//	public void setId(int id) {
//		this.id=id;
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public boolean isTheDefault() {
		return theDefault;
	}

	public void setTheDefault(boolean theDefault) {
		this.theDefault = theDefault;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}


	public String toString() {
		return "Event [" +
		        this.getId() + ", " +
		        this.getName() + ", " +
		        this.isTheDefault() + ", " +
		        "]";
	}
}
