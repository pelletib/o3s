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

import net.o3s.apis.IEntityPerson;

/**
 * Entity implementation class for Entity: Person
 *
 */
@Entity
@NamedQueries( {
	@NamedQuery(name = "ALL_FIRSTNAME", query = "SELECT DISTINCT p.firstname FROM Person p ORDER BY p.firstname"),
	@NamedQuery(name = "ALL_LASTNAME", query = "SELECT DISTINCT p.lastname FROM Person p ORDER BY p.lastname"),
	@NamedQuery(name = "FIRSTNAME_FROM_PATTERN", query = "SELECT DISTINCT p.firstname FROM Person p WHERE p.firstname LIKE :PATTERN ORDER BY p.firstname"),
	@NamedQuery(name = "LASTNAME_FROM_PATTERN", query = "SELECT DISTINCT p.lastname FROM Person p WHERE p.lastname LIKE :PATTERN ORDER BY p.lastname"),
	@NamedQuery(name = "PERSON_FROM_LASTNAME", query = "SELECT p FROM Person p WHERE p.lastname = :LASTNAME"),
	@NamedQuery(name = "PERSON_FROM_LASTNAME_FIRSTNAME", query = "SELECT p FROM Person p WHERE p.lastname = :LASTNAME and p.firstname = :FIRSTNAME"),
	@NamedQuery(name = "PERSON_FROM_LASTNAME_FIRSTNAME_BIRTHDAY", query = "SELECT p FROM Person p WHERE p.lastname = :LASTNAME and p.firstname = :FIRSTNAME and p.birthday = :BIRTHDAY"),
	@NamedQuery(name = "ALL_PERSONS", query = "SELECT p FROM Person p") })
public class Person implements IEntityPerson, Serializable {

	/**
	 * Serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	/**
	 * Lastname
	 */
	private String lastname;

	/**
	 * Firstname
	 */
	private String firstname;

	/**
	 * Club
	 */
	private String club;

	/**
	 * License
	 */
	private String license;

	/**
	 * Email
	 */
	private String email;

	/**
	 * Birthday
	 */
	@Temporal(TemporalType.DATE)
	private Date birthday;

	/**
	 * Sex
	 */
	private char sex;

	/**
	 * Constructor
	 */
	public Person() {
		super();
	}

	/**
	 * Getters/Setters
	 */
	public int getId() {
		return this.id;
	}
//	public void setId(int id) {
//		this.id=id;
//	}
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

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String toString() {
		return "Person [" +
		        this.getId() + ", " +
		        this.getFirstname() + ", " +
		        this.getLastname() + ", " +
		        this.getBirthday() + ", " +
		        this.getClub() + ", " +
		        this.getLicense() + ", " +
		        this.getEmail() + ", " +
		        this.getSex() + ", " +
		        "]";
	}
}
