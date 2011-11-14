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
package net.o3s.persistence;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

import net.o3s.apis.IEntityEvent;
import net.o3s.apis.IEntityLabel;

/**
 * Entity implementation class for Entity: Label
 *
 */
@Entity
@NamedQueries( {
	@NamedQuery(name = "LABEL_FROM_NUMBER_AND_EVENT", query = "SELECT l FROM Label l WHERE l.number = :NUMBER AND l.event.id = :EVENTID"),
	@NamedQuery(name = "LABEL_FROM_VALUE_AND_EVENT", query = "SELECT l FROM Label l WHERE l.event.id = :EVENTID AND l.value LIKE :VALUE"),
	@NamedQuery(name = "LABEL_FROM_RFID_AND_EVENT", query = "SELECT l FROM Label l WHERE l.event.id = :EVENTID AND l.rfid LIKE :RFID"),
	@NamedQuery(name = "ALL_LABELS", query = "SELECT l FROM Label l") })
public class Label implements IEntityLabel, Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private int number;
	private String value;
	private String rfid;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, targetEntity=Event.class)
    private IEntityEvent event;

	private static final long serialVersionUID = 1L;

	public Label() {
		super();
	}

	public int getId() {
		return this.id;
	}
//	public void setId(int id) {
//		this.id=id;
//	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public IEntityEvent getEvent() {
		return this.event;
	}

	public void setEvent(IEntityEvent event) {
		this.event = event;
	}

	public String toString() {
		return "Label [" +
		        this.getId() + ", " +
		        this.getNumber() + ", " +
		        this.getValue() + ", " +
		        this.getRfid() + ", " +
		        this.getEvent() + ", " +
		        "]";
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public String getRfid() {
		return rfid;
	}
}
