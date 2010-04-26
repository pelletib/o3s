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

public class CompetitionVO {

	private int id;
	private String name;
	private int lowerLabelNumber;
	private int higherLabelNumber;
	private int lastLabelNumber;
	private Date startingDate;
    private EventVO event;
	private boolean teamed=false;

	public CompetitionVO() {
		super();
	}

	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id=id;
	}
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

	public EventVO getEvent() {
		return this.event;
	}

	public void setEvent(EventVO event) {
		this.event = event;
	}

	public Date getStartingDate() {
		return startingDate;
	}

	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}

	public String toString() {
		return "CompetitionVO [" +
		        this.getId() + ", " +
		        this.getName() + ", " +
		        this.getLowerLabelNumber() + ", " +
		        this.getHigherLabelNumber() + ", " +
		        this.getLastLabelNumber() + ", " +
		        this.getEvent() + ", " +
				this.isTeamed() + ", " +
		        this.getStartingDate() + ", " +
		        "]";
	}
	public boolean isTeamed() {
		return teamed;
	}

	public void setTeamed(boolean teamed) {
		this.teamed = teamed;
	}

}
