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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.o3s.apis.IEntityCategory;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityEvent;
import net.o3s.apis.IEntityLabel;
import net.o3s.apis.IEntityPerson;
import net.o3s.apis.IEntityRegistered;

/**
 * Entity implementation class for Entity: Participant
 *
 */
@Entity
@NamedQueries( {
	@NamedQuery(name = "COUNT_REGISTERED_FROM_COMPETITION",
		    query = "SELECT count(r) FROM Registered r WHERE r.event.id = :EVENTID AND r.competition.id = :COMPETITION"),
	@NamedQuery(name = "COUNT_ARRIVAL_FROM_COMPETITION",
			query = "SELECT count(r) FROM Registered r WHERE r.event.id = :EVENTID AND r.competition.id = :COMPETITION AND r.arrivalDate IS NOT NULL"),
	@NamedQuery(name = "COUNT_REGISTERED_FROM_COMPETITION_AND_CATEGORY",
		    query = "SELECT count(r) FROM Registered r WHERE r.event.id = :EVENTID AND r.competition.id = :COMPETITION AND r.category.id = :CATEGORY"),
	@NamedQuery(name = "COUNT_ARRIVAL_FROM_COMPETITION_AND_CATEGORY",
			query = "SELECT count(r) FROM Registered r WHERE r.event.id = :EVENTID AND r.competition.id = :COMPETITION AND r.category.id = :CATEGORY AND r.arrivalDate IS NOT NULL"),
	@NamedQuery(name = "REGISTERED_FROM_NAME", query = "SELECT r FROM Registered r WHERE r.name = :NAME AND r.event.id = :EVENTID"),
	@NamedQuery(name = "REGISTERED_FROM_PERSONID_ALL_EVENTS", query = "SELECT r FROM Registered r INNER JOIN r.persons p WHERE p.id = :PERSONID"),
	@NamedQuery(name = "REGISTERED_FROM_PERSONID", query = "SELECT r FROM Registered r INNER JOIN r.persons p WHERE p.id = :PERSONID AND r.event.id = :EVENTID"),
    @NamedQuery(name = "REGISTERED_FROM_LABEL", query = "SELECT r FROM Registered r WHERE r.label.value LIKE :VALUE"),
    @NamedQuery(name = "REGISTERED_FROM_LABELNUMBER", query = "SELECT r FROM Registered r WHERE r.label.number = :VALUE"),
    @NamedQuery(name = "REGISTERED_FROM_RFID", query = "SELECT r FROM Registered r WHERE r.label.rfid LIKE :RFID"),
    @NamedQuery(name = "REGISTERED_FROM_COMPETITION_ORDERBY_ETIME",
		    query = "SELECT r FROM Registered r WHERE r.competition.id = :COMPETITION AND r.arrivalDate IS NOT NULL ORDER BY r.elapsedTime"),
	@NamedQuery(name = "REGISTERED_FROM_COMPETITION_ORDERBY_CATEGORY_ETIME",
			    query = "SELECT r FROM Registered r WHERE r.competition.id = :COMPETITION AND r.arrivalDate IS NOT NULL ORDER BY r.category.id,r.elapsedTime"),
	@NamedQuery(name = "REGISTERED_FROM_COMPETITION_ORDERBY_CLUB_ETIME",
			    query = "SELECT r FROM Registered r WHERE r.competition.id = :COMPETITION AND r.arrivalDate IS NOT NULL AND r.teamed = FALSE AND r.club IS NOT NULL AND r.club <> '' AND r.club <> 'N/A' ORDER BY r.club,r.elapsedTime"),
	@NamedQuery(name = "REGISTERED_FROM_EVENT_AFTER_REGISTRATION_DATE",
					    query = "SELECT r FROM Registered r WHERE r.event.id = :EVENTID AND r.registeringDate >= :MINDATE"),
	@NamedQuery(name = "ALL_REGISTERED_FROM_EVENT", query = "SELECT r FROM Registered r WHERE r.event.id = :EVENTID"),
    @NamedQuery(name = "ALL_REGISTERED", query = "SELECT r FROM Registered r") })
public class Registered implements IEntityRegistered, Serializable {

    /**
	 *
	 */
	private static final long serialVersionUID = 2931598744846653686L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    boolean teamed=false;
    boolean paid=false;

	@Temporal(TemporalType.TIMESTAMP)
    Date registeringDate;

	@Temporal(TemporalType.TIMESTAMP)
    Date arrivalDate;

    long elapsedTime;

    String name;
    boolean providedHealthForm=false;

    /**
     * web/import/internal
     */
    private String source;

    private String club;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, targetEntity=Event.class)
    private IEntityEvent event;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, targetEntity=Competition.class)
    private IEntityCompetition competition;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, targetEntity=Label.class)
    private IEntityLabel label;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, targetEntity=Category.class)
    private IEntityCategory category;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, targetEntity=Person.class)
	@OrderBy
	private Set<IEntityPerson> persons;

    public Registered() {
        super();
        this.persons = new HashSet<IEntityPerson>();
    }

	public int getId() {
        return this.id;
    }
	//public void setId(int id) {
	//	this.id=id;
	//}
	public void setName(String name) {
		this.name = name;

	}

	public String getName() {
		return this.name;
	}

	public void setPersons(Set<IEntityPerson> persons) {
		this.persons = persons;

	}
	public Set<IEntityPerson> getPersons() {
		return this.persons;
	}

	public Date getRegisteringDate() {
		return this.registeringDate;
	}
	public void setRegisteringDate(Date date) {
		this.registeringDate = date;
	}

	public Date getArrivalDate() {
		return this.arrivalDate;
	}
	public void setArrivalDate(Date date) {
		this.arrivalDate = date;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public Boolean isPaid() {
		return this.paid;
	}

	public void setIsPaid(boolean paid) {
		this.paid = paid;
	}

	public Boolean isTeamed() {
		return this.teamed;
	}

	public void setIsTeamed(boolean teamed) {
		this.teamed = teamed;
	}

	public IEntityCompetition getCompetition() {
		return this.competition;
	}

	public void setCompetition(IEntityCompetition competition) {
		this.competition = competition;
	}

	public IEntityLabel getLabel() {
		return this.label;
	}

	public void setLabel(IEntityLabel label) {
		this.label = label;
	}

	public IEntityEvent getEvent() {
		return this.event;
	}

	public void setEvent(IEntityEvent event) {
		this.event = event;
	}

	public IEntityCategory getCategory() {
		return this.category;
	}

	public void setCategory(IEntityCategory category) {
		this.category = category;
	}

	public Boolean isProvidedHealthForm() {
		return providedHealthForm;
	}

	public void setProvidedHealthForm(boolean providedHealthForm) {
		this.providedHealthForm = providedHealthForm;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getClub() {
		return club;
	}

	public void setClub(String club) {
		this.club = club;
	}

	public String toString() {
		return "Registered [" +
		        this.getId() + ", " +
		        this.getName() + ", " +
		        this.getRegisteringDate() + ", " +
		        this.getArrivalDate() + ", " +
		        this.getElapsedTime() + ", " +
		        this.getPersons() + ", " +
		        this.isPaid() + ", " +
		        this.isTeamed() + ", " +
		        this.getLabel() + ", " +
		        this.getEvent() + ", " +
		        this.getCompetition() + ", " +
		        this.getCategory() + ", " +
		        this.isProvidedHealthForm() + "," +
		        this.getSource() + "," +
		        this.getClub() + "," +
		        "]";
	}
}
