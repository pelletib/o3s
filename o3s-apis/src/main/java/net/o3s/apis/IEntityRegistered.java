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
package net.o3s.apis;

import java.util.Date;
import java.util.Set;

public interface IEntityRegistered {

	int getId();
	//void setId(int id);

	Date getRegisteringDate();
	void setRegisteringDate(Date date);

	Date getArrivalDate();
	void setArrivalDate(Date date);

	long getElapsedTime();
	void setElapsedTime(long elapsedTime);

	IEntityEvent getEvent();
	void setEvent(IEntityEvent event);

	Boolean isTeamed();
	void setIsTeamed(boolean teamed);

	Boolean isPaid();
	void setIsPaid(boolean paid);

	Boolean isProvidedHealthForm();
	void setProvidedHealthForm(boolean providedHealthForm);

	String getName();
	void setName(String name);

	Set<IEntityPerson> getPersons();
	void setPersons(final Set<IEntityPerson> persons);

	IEntityCompetition getCompetition();
	void setCompetition(IEntityCompetition competition);

	IEntityLabel getLabel();
	void setLabel(IEntityLabel label);

	IEntityCategory getCategory();
	void setCategory(IEntityCategory category);

	String getSource();
	void setSource(String source) ;

	String toString();
}
