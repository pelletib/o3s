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

import java.util.Date;

public class PersonVO {

	private int id;
	private String lastname;
	private String firstname;
	private String club;
	private String license;


	private String email;
	private Date birthday;
	private char sex;
	private String computedCategory;

	private boolean registered;
	private String rfid;

	public PersonVO() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id=id;
	}


	public String toString() {
		return "PersonVO [" +
        		this.getId() + ", " +
        		this.getFirstname() + ", " +
        		this.getLastname() + ", " +
        		this.getBirthday() + ", " +
        		this.getClub() + ", " +
        		this.getLicense() + ", " +
        		this.getEmail() + ", " +
        		this.getSex() + ", " +
        		this.getComputedCategory() + ", " +
        		this.isRegistered() + ", " +
        		this.getRfid() + ", " +
      		"]";
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getClub() {
		return club;
	}

	public void setClub(String club) {
		this.club = club;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

	public String getComputedCategory() {
		return computedCategory;
	}

	public void setComputedCategory(String computedCategory) {
		this.computedCategory = computedCategory;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public boolean isRegistered() {
		return registered;
	}

	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}
}
