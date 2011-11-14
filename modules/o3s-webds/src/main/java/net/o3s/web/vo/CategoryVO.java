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
package net.o3s.web.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CategoryVO {

	private int id;
	private String name;
	private char sex;
	private Date minDate;
	private Date maxDate;
	private char shortName;
	private List<CompetitionVO> competitions;
    private EventVO event;


	public CategoryVO() {
		super();
	}

	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id=id;
	}
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCompetitions(List<CompetitionVO> competitions) {
		this.competitions = competitions;
	}
	public List<CompetitionVO> getCompetitions() {
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

	public EventVO getEvent() {
		return this.event;
	}

	public void setEvent(EventVO event) {
		this.event = event;
	}

	public void setShortName(char shortName) {
		this.shortName = shortName;
	}

	public char getShortName() {
		return this.shortName;
	}

	public String toString() {
		return "CategoryVO [" +
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
