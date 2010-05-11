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



public class EventVO {

	/**
	 * Id
	 */
	private int id;

	/**
	 * Name
	 */
	private String name;

	/**
	 * Date
	 */
	private Date date;

	/**
	 * Default event ?
	 */
	private boolean theDefault = false;

	/**
	 * Logo
	 */
	private  byte[]  imageFile;

	/**
	 * Constructor
	 */
	public EventVO() {
		super();
	}

	/**
	 * Getters/Setters
	 */

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id=id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isTheDefault() {
		return theDefault;
	}

	public void setTheDefault(boolean theDefault) {
		this.theDefault = theDefault;
	}

	public byte[] getImageFile() {
		return imageFile;
	}

	public void setImageFile(byte[] imageFile) {
		this.imageFile = imageFile;
	}
	public String toString() {
		return "EventVO [" +
		        this.getId() + ", " +
		        this.getName() + ", " +
		        this.getImageFile() + ", " +
		        this.isTheDefault() + ", " +
		        "]";
	}

}
