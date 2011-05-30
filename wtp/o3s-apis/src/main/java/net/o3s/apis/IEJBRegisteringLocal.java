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

import java.util.Date;
import java.util.List;

public interface IEJBRegisteringLocal {

	IEntityPerson createPerson(final String lastname, final String firstname, final String club, final String license, final String email, final char sex, final Date birthday) throws AlreadyExistException,RegisteringException;
	IEntityPerson updatePerson(final int id, final String lastname, final String firstname, final String club, final String license, final String email, final char sex, final Date birthday) throws RegisteringException;
	IEntityPerson updatePerson(IEntityPerson person) throws RegisteringException;
	IEntityPerson findPersonFromId(final int id);
	IEntityPerson findPersonFromLastnameFirstNameBirthDay(final String lastname, final String firstname, final Date birthday);
	List<IEntityPerson> findPersonsFromLastnameFirstname(final String lastname, final String firstname);
	void removePerson(final int id) throws RegisteringException;
	List<String> findAllLastName(final String prefix);
	List<String> findAllFirstName(final String prefix);
	List<IEntityPerson> findAllPersons();

	List<IEntityRegistered> findAllRegistered();
	List<IEntityRegistered> findAllRegisteredFromDefaultEvent();
	List<IEntityRegistered> findAllRegisteredFromEvent(int eventId);
	List<IEntityRegistered> findAllRegisteredFromDefaultEventWhereRegisteringDateIsGreaterThan(Date minDate);
	IEntityRegistered findRegisteredFromLabel(final String labelValue)  throws RegisteringException;
	IEntityRegistered findRegisteredFromLabelNumber(final int labelNumber) throws RegisteringException;
	IEntityRegistered findRegisteredFromRfid(final String rfid) throws RegisteringException;
	IEntityRegistered findRegisteredFromLabelData(final String labelData) throws RegisteringException;
	void setRfidToLabel(String labelData, String rfid) throws RegisteringException;
	List<IEntityRegistered> findRegisteredFromCompetitionOrderByDuration(final int competitionId) throws RegisteringException;
	List<IEntityRegistered> findRegisteredFromCompetitionOrderByCategoryAndDuration(final int competitionId) throws RegisteringException;
	List<IEntityRegistered> findRegisteredFromCompetitionOrderByCategoryAndDuration(final int competitionId,  List<Integer> categoriesId) throws RegisteringException;
	List<IEntityRegistered> findRegisteredFromCompetitionOrderByClubAndDuration(final int competitionId) throws RegisteringException;

	List<IEntityRegistered> createRegistered(final List<IEntityPerson> persons, final int competitionId, boolean isTeamed, final String name, final boolean isPaid, final boolean providedHealthForm, final String source)  throws AlreadyExistException,RegisteringException;
	IEntityRegistered findRegisteredFromId(final int id);
	IEntityRegistered findRegisteredFromPersonForDefaultEvent(final int personId);
	List<IEntityRegistered> findRegisteredFromPersonForAllEvent(final int personId);
	void removeRegistered(final int id) throws RegisteringException;
	IEntityRegistered updateRegistered(final int id, final List<IEntityPerson> persons, final int competitionId, boolean isTeamed, final String name, final boolean isPaid, final boolean providedHealthForm, final String source) throws RegisteringException;
	List<IEntityLabel> findAllLabels();
	IEntityRegistered importRegistered(
			final IEntityEvent event,
			final IEntityCompetition competition,
			final IEntityCategory category,
			final String name,
			final Date registeringDate,
			final String labelValue,
			final boolean isTeamed,
			final boolean isPaid,
			final boolean providedHealthForm,
			final List<IEntityPerson> persons,
			final String club,
			final String source,
			final Date arrivalDate,
			final long elapsedTime) throws AlreadyExistException, RegisteringException;

	void updateArrivalDateRegistered(
			final int id,
			final Date arrivalDate) throws RegisteringException;

	void recomputeElapsedTimeRegistereds(
			final int id) throws RegisteringException;

	int countRegisteredFromCompetition(final int competitionId) throws RegisteringException;
	int countArrivalFromCompetition(final int competitionId) throws RegisteringException;
	int countRegisteredFromCompetitionAndCategory(final int competitionId, final int categoryId) throws RegisteringException;
	int countArrivalFromCompetitionAndCategory(final int competitionId, final int categoryId) throws RegisteringException;

	void resetAll() throws RegisteringException;
}
