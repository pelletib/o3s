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
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityEvent;

/**
 * Entity implementation class for Entity: Competition
 *
 */
@Entity
@NamedQueries( {
	@NamedQuery(name = "COMPETITION_FROM_NAME_AND_EVENT", query = "SELECT c FROM Competition c WHERE c.name = :NAME AND c.event.id = :EVENTID"),
	@NamedQuery(name = "ALL_COMPETITIONS_FROM_EVENT", query = "SELECT c FROM Competition c WHERE c.event.id = :EVENTID"),
	@NamedQuery(name = "ALL_COMPETITIONS", query = "SELECT c FROM Competition c") })
public class Competition implements IEntityCompetition, Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	private int lowerLabelNumber;
	private int higherLabelNumber;
	private int lastLabelNumber;
	private boolean isTeamed;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startingDate;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, targetEntity=Event.class)
    private IEntityEvent event;


	private static final long serialVersionUID = 1L;

	public Competition() {
		super();
	}

	public int getId() {
		return this.id;
	}
//	public void setId(int id) {
//		this.id=id;
//	}
	public int getLowerLabelNumber() {
		return this.lowerLabelNumber;
	}

	public void setLowerLabelNumber(int lowerLabelNumber) {
		this.lowerLabelNumber = lowerLabelNumber;

	}
	public int getHigherLabelNumber() {
		return this.higherLabelNumber;
	}

	public void setHigherLabelNumber(int higherLabelNumber) {
		this.higherLabelNumber = higherLabelNumber;

	}
	public int getLastLabelNumber() {
		return this.lastLabelNumber;
	}

	public void setLastLabelNumber(int lastLabelNumber) {
		this.lastLabelNumber = lastLabelNumber;

	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public IEntityEvent getEvent() {
		return this.event;
	}

	public void setEvent(IEntityEvent event) {
		this.event = event;
	}

	public Date getStartingDate() {
		return startingDate;
	}

	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}

	public Boolean isTeamed() {
		return isTeamed;
	}

	public void setTeamed(boolean isTeamed) {
		this.isTeamed = isTeamed;
	}
	public String toString() {
		return "Competition [" +
		        this.getId() + ", " +
		        this.getName() + ", " +
		        this.getLowerLabelNumber() + ", " +
		        this.getHigherLabelNumber() + ", " +
		        this.getLastLabelNumber() + ", " +
		        this.getEvent() + ", " +
		        this.getStartingDate() + ", " +
		        this.isTeamed() + ", " +
		        "]";
	}

}
