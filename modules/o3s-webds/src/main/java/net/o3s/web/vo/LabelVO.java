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

public class LabelVO {

	private int id;
	private int number;
	private String value;
	private String rfid;


	public LabelVO() {
		super();
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id=id;
	}

	public String toString() {
		return "LabelVO [" +
				this.getId() + ", " +
				this.getNumber() + ", " +
				this.getValue() + ", " +
		        this.getRfid() + ", " +
				"]";
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public String getRfid() {
		return rfid;
	}
}
