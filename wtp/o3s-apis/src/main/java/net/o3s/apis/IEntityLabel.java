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
package net.o3s.apis;

public interface IEntityLabel {

	public static int LABEL_VALUE_SIZE = 8;
	public static int RFID_SIZE = 10;

	int getId();
	//void setId(int id);

	String getValue();
	void setValue(final String value);
	String getRfid();
	void setRfid(final String rfid);
	int getNumber();
	void setNumber(final int number);
	IEntityEvent getEvent();
	void setEvent(IEntityEvent event);
	String toString();

}
