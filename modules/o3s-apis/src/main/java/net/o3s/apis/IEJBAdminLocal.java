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

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IEJBAdminLocal {

	IEntityEvent findEventFromName(final String name);
	IEntityEvent findEventFromId(final int id);
	IEntityEvent findLastEvent();
	List<IEntityEvent> findAllEvents();
	IEntityEvent findDefaultEvent();
	void setDefaultEvent(final int id);
	IEntityEvent createEvent(final String name, final Date date, final String fileName);
	void removeEvent(final int id) throws AdminException;
	IEntityEvent duplicateEvent(final int eventId);
    IEntityEvent updateEvent(final int id,final String name,final Date date) throws AdminException;

	IEntityCompetition findCompetitionFromId(final int id);
	List<IEntityCompetition> findAllCompetitions();
	List<IEntityCompetition> findAllCompetitionsFromDefaultEvent();
	List<IEntityCompetition> findAllCompetitionsFromEvent(final int eventId);

	IEntityCompetition findCompetitionFromName(final String name, IEntityEvent event);
	Date setStartDateInCompetition(final int id) throws AdminException;
	Date setStartDateInCompetition(final int id, final Date date) throws AdminException;
	IEntityCompetition createCompetition(final String name, final int lowerLabelNumber, final int higherLabelNumber, final int lastLabelNumber, final int eventId, final boolean isTeamed);
	IEntityCompetition updateCompetition(
    		final int id,
    		final String name,
    		final int firstLabelNumber,
    		final int lastLabelNumber,
    		final int lowerLabelNumber,
    		final Date startingDate,
    		final boolean teamed) throws AdminException;
	void removeCompetition(final int id) throws AdminException;

	IEntityCategory findCategoryFromNameAndSex(final String name, final char sex);
	List<IEntityCategory> findCategoryFromDatesAndSex(final Date date, final char sex);
	IEntityCategory findNoCategory();
	List<IEntityCategory> findAllCategories();
	List<IEntityCategory> findAllCategoriesFromDefaultEvent();
	List<IEntityCategory> findAllCategoriesFromEvent(final int eventId);
	IEntityCategory createCategory(final String name, final Date minDate, final Date maxDate, final char sex, final char shortName, final int eventId, final IEntityCompetition... competitions);
	IEntityCategory updateCategory(
    		final int id,
    		final String name,
    		final char sex,
    		final char shortName,
    		final Date minDate,
    		final Date maxDate,
    		final List<IEntityCompetition> competitions) throws AdminException;
	void removeCategory(final int id) throws AdminException;

	String exportRegisteredAsFileName(final Date from);
	int importRegistered(String fileName);
	int importAdminFromZip(String zipFileName) throws IOException, AdminException;
	String exportAdminAsZip() throws IOException;
	void resetAdminAll() throws AdminException;
}
