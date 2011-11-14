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

public class RegisteredStatisticsVO {

	private String competition;

	public String getCompetition() {
		return this.competition;
	}

	public void setCompetition(final String competition) {
		this.competition = competition;
	}

	private String category;

	public String getCategory() {
		return this.category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	private int registeredNumber;
	public int getRegisteredNumber() {
		return this.registeredNumber;
	}
	public void setRegisteredNumber(int registeredNumber) {
		this.registeredNumber = registeredNumber;
	}

	private int arrivalNumber;
	public int getArrivalNumber() {
		return this.arrivalNumber;
	}
	public void setArrivalNumber(int arrivalNumber) {
		this.arrivalNumber = arrivalNumber;
	}

	public String toString() {
		return "RegisteredStatisticsVO [" + competition + "," + category + "," + registeredNumber + "," + arrivalNumber + "]";
	}

}
