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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import net.o3s.apis.IEntityLabel;

/**
 * Entity implementation class for Entity: Label
 *
 */
@Entity
@NamedQueries( {
	@NamedQuery(name = "LABEL_FROM_NUMBER", query = "SELECT l FROM Label l WHERE l.number = :NUMBER"),
	@NamedQuery(name = "LABEL_FROM_VALUE", query = "SELECT l FROM Label l WHERE l.value LIKE :VALUE"),
	@NamedQuery(name = "ALL_LABELS", query = "SELECT l FROM Label l") })
public class Label implements IEntityLabel, Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private int number;
	private String value;
	private static final long serialVersionUID = 1L;

	public Label() {
		super();
	}

	public int getId() {
		return this.id;
	}
//	public void setId(int id) {
//		this.id=id;
//	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String toString() {
		return "Label [" +
		        this.getId() + ", " +
		        this.getNumber() + ", " +
		        this.getValue() + ", " +
		        "]";
	}
}
