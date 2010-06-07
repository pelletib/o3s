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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.o3s.apis.IEntityCategory;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityEvent;

/**
 * Entity implementation class for Entity: Category
 *
 */
@Entity
@NamedQueries( {
	@NamedQuery(name = "CATEGORY_FROM_NAME_AND_EVENT", query = "SELECT c FROM Category c WHERE c.name = :NAME AND c.event.id = :EVENTID"),
	@NamedQuery(name = "CATEGORY_FROM_NAME_AND_SEX_AND_EVENT", query = "SELECT c FROM Category c WHERE c.name = :NAME and c.sex = :SEX AND c.event.id = :EVENTID"),
	@NamedQuery(name = "CATEGORY_FROM_DATES_AND_SEX_AND_EVENT", query = "SELECT c FROM Category c WHERE c.sex = :SEX and :DATE between c.minDate and c.maxDate AND c.event.id = :EVENTID"),
	@NamedQuery(name = "NOCATEGORY", query = "SELECT c FROM Category c WHERE c.name LIKE 'Unknown%'"),
	@NamedQuery(name = "ALL_CATEGORIES_FROM_EVENT", query = "SELECT c FROM Category c WHERE c.event.id = :EVENTID"),
	@NamedQuery(name = "ALL_CATEGORIES", query = "SELECT c FROM Category c") })
public class Category implements IEntityCategory, Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	private char sex;

	@Temporal(TemporalType.DATE)
	private Date minDate;
	@Temporal(TemporalType.DATE)
	private Date maxDate;

	private char shortName;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, targetEntity=Competition.class)
	@OrderBy
	private Set<IEntityCompetition> competitions;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, targetEntity=Event.class)
    private IEntityEvent event;


	private static final long serialVersionUID = 1L;

	public Category() {
		super();
        this.competitions = new HashSet<IEntityCompetition>();
	}

	public int getId() {
		return this.id;
	}
	//public void setId(int id) {
	//	this.id=id;
	//}
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;

	}

	public void setCompetitions(Set<IEntityCompetition> competitions) {
		this.competitions = competitions;

	}
	public Set<IEntityCompetition> getCompetitions() {
		return this.competitions;
	}

	public Date getMaxDate() {
		return this.maxDate;
	}

	public Date getMinDate() {
		return this.minDate;
	}

	public char getSex() {
		return this.sex;
	}

	public void setMaxDate(Date date) {
		this.maxDate = date;
	}

	public void setMinDate(Date date) {
		this.minDate = date;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	public IEntityEvent getEvent() {
		return this.event;
	}

	public void setEvent(IEntityEvent event) {
		this.event = event;
	}

	public void setShortName(char shortName) {
		this.shortName = shortName;
	}

	public char getShortName() {
		return this.shortName;
	}

	public String toString() {
		return "Category [" +
		        this.getId() + ", " +
		        this.getName() + ", " +
		        this.getShortName() + ", " +
		        this.getMinDate() + ", " +
		        this.getMaxDate() + ", " +
		        this.getSex() + ", " +
		        this.getEvent() + ", " +
		        this.getCompetitions() + ", " +
		        "]";
	}


}
