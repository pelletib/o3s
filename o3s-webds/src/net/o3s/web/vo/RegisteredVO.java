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
package net.o3s.web.vo;

import java.util.Date;
import java.util.List;

public class RegisteredVO {

	private int id;
	private String name;
	private boolean teamed=false;
	private boolean paid=false;
	private Date registeringDate;
	private Date arrivalDate;
	private long elapsedTime;
    private EventVO event;
    private CompetitionVO competition;
    private LabelVO label;
    private CategoryVO category;
	private List<PersonVO> persons;


	public RegisteredVO() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id=id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String toString() {
		return "RegisteredVO [" +
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
				"]";
	}

	public boolean isTeamed() {
		return teamed;
	}

	public void setTeamed(boolean teamed) {
		this.teamed = teamed;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public Date getRegisteringDate() {
		return registeringDate;
	}

	public void setRegisteringDate(Date registeringDate) {
		this.registeringDate = registeringDate;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public EventVO getEvent() {
		return event;
	}

	public void setEvent(EventVO event) {
		this.event = event;
	}

	public CompetitionVO getCompetition() {
		return competition;
	}

	public void setCompetition(CompetitionVO competition) {
		this.competition = competition;
	}

	public LabelVO getLabel() {
		return label;
	}

	public void setLabel(LabelVO label) {
		this.label = label;
	}

	public CategoryVO getCategory() {
		return category;
	}

	public void setCategory(CategoryVO category) {
		this.category = category;
	}

	public List<PersonVO> getPersons() {
		return persons;
	}

	public void setPersons(List<PersonVO> persons) {
		this.persons = persons;
	}
}
