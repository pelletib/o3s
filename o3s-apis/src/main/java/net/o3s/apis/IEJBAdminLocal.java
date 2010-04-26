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
import java.util.List;

public interface IEJBAdminLocal {

	IEntityEvent findEventFromName(final String name);
	IEntityEvent findLastEvent();
	List<IEntityEvent> findAllEvents();
	IEntityEvent findDefaultEvent();
	void setDefaultEvent(final int id);
	IEntityEvent createEvent(final String name, final Date date);

	IEntityCompetition findCompetitionFromId(final int id);
	List<IEntityCompetition> findAllCompetitions();
	List<IEntityCompetition> findAllCompetitionsFromDefaultEvent();
	Date setStartDateInCompetition(final int id) throws AdminException;
	IEntityCompetition createCompetition(final String name, final int lowerLabelNumber, final int higherLabelNumber, final int lastLabelNumber, final IEntityEvent event, final boolean isTeamed);

	IEntityCategory findCategoryFromNameAndSex(final String name, final char sex);
	List<IEntityCategory> findCategoryFromDatesAndSex(final Date date, final char sex);
	IEntityCategory findNoCategory();
	List<IEntityCategory> findAllCategories();
	List<IEntityCategory> findAllCategoriesFromDefaultEvent();
	IEntityCategory createCategory(final String name, final Date minDate, final Date maxDate, final char sex, final char shortName, final IEntityEvent event, final IEntityCompetition... competitions);

}
