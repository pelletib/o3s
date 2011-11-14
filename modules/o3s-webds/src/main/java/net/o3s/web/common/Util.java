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
package net.o3s.web.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.o3s.apis.IEntityCategory;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityEvent;
import net.o3s.apis.IEntityLabel;
import net.o3s.apis.IEntityPerson;
import net.o3s.apis.IEntityRegistered;
import net.o3s.web.service.Registering;
import net.o3s.web.vo.CategoryVO;
import net.o3s.web.vo.CompetitionVO;
import net.o3s.web.vo.EventVO;
import net.o3s.web.vo.FlexException;
import net.o3s.web.vo.LabelVO;
import net.o3s.web.vo.PersonVO;
import net.o3s.web.vo.RegisteredVO;


public class Util {

	/**
	 * Logger
	 */
    private static Logger logger = Logger.getLogger(Util.class.getName());


	public static EventVO createEventVO(IEntityEvent event) {
		EventVO eventVO = new EventVO();

		if (event != null) {
			eventVO.setId(event.getId());
			eventVO.setName(event.getName());
			eventVO.setDate(event.getDate());
			eventVO.setImageFile(event.getImageFile());
			eventVO.setTheDefault(event.isTheDefault());
		}
		return eventVO;
	}

	public static List<EventVO> createEventListVO(List<IEntityEvent> events) {
		List<EventVO> eventsVO = new ArrayList<EventVO>();
		for(IEntityEvent event:events) {
			eventsVO.add(createEventVO(event));
		}
		return eventsVO;
	}

	public static CompetitionVO createCompetitionVO(IEntityCompetition competition) {

		CompetitionVO competitionVO = new CompetitionVO();
		if (competition != null) {
			competitionVO.setId(competition.getId());
			competitionVO.setName(competition.getName());
			competitionVO.setEvent(createEventVO(competition.getEvent()));
			competitionVO.setHigherLabelNumber(competition.getHigherLabelNumber());
			competitionVO.setLowerLabelNumber(competition.getLowerLabelNumber());
			competitionVO.setLastLabelNumber(competition.getLastLabelNumber());
			competitionVO.setStartingDate(competition.getStartingDate());
			competitionVO.setTeamed(competition.isTeamed());
		}
		return competitionVO;

	}

	public static List<CompetitionVO> createCompetitionListVO(List<IEntityCompetition> competitions) {
		List<CompetitionVO> competitionsVO = new ArrayList<CompetitionVO>();

		for(IEntityCompetition competition:competitions) {
			competitionsVO.add(createCompetitionVO(competition));
		}
		return competitionsVO;
	}

	public static List<CompetitionVO> createCompetitionListVO(Set<IEntityCompetition> competitions) {
		List<CompetitionVO> competitionsVO = new ArrayList<CompetitionVO>();

		for(IEntityCompetition competition:competitions) {
			competitionsVO.add(createCompetitionVO(competition));
		}
		return competitionsVO;
	}

	public static CategoryVO createCategoryVO(IEntityCategory category) {
		CategoryVO categoryVO = new CategoryVO();
		if (category != null) {
			categoryVO.setId(category.getId());
			categoryVO.setName(category.getName());
			categoryVO.setEvent(createEventVO(category.getEvent()));
			categoryVO.setMaxDate(category.getMaxDate());
			categoryVO.setMinDate(category.getMinDate());
			categoryVO.setSex(category.getSex());
			categoryVO.setShortName(category.getShortName());

			categoryVO.setCompetitions(createCompetitionListVO(category.getCompetitions()));
		}
		return categoryVO;

	}

	public static List<CategoryVO> createCategoryListVO(List<IEntityCategory> categories) {
		List<CategoryVO> categoriesVO = new ArrayList<CategoryVO>();

		for(IEntityCategory category:categories) {
			categoriesVO.add(createCategoryVO(category));
		}
		return categoriesVO;
	}

	public static LabelVO createLabelVO(IEntityLabel label) {

		LabelVO labelVO = new LabelVO();
		if (label != null) {
			labelVO.setId(label.getId());
			labelVO.setNumber(label.getNumber());
			labelVO.setValue(label.getValue());
			labelVO.setRfid(label.getRfid());
		}
		return labelVO;
	}

	public static PersonVO createPersonVO(IEntityPerson person, String rfid, Registering service) {
		PersonVO personVO = new PersonVO();
		if (person != null) {
			personVO.setId(person.getId());
			personVO.setBirthday(person.getBirthday());
			personVO.setClub(person.getClub());
			personVO.setLicense(person.getLicense());
			personVO.setEmail(person.getEmail());
			personVO.setFirstname(person.getFirstname());
			personVO.setLastname(person.getLastname());
			personVO.setSex(person.getSex());

			// get the related category
			try {
				IEntityCategory category = service.getCategory(person);
				personVO.setComputedCategory(category.getName());
			} catch (Exception e) {
				e.printStackTrace();
				//throw new FlexException(e.getMessage());
				personVO.setComputedCategory("Error");
			}

			// check if the person is already registered (in all events)
			try {
				Boolean isRegistered = service.isAlreadyRegisteredForAllEvent(person.getId());
				personVO.setRegistered(isRegistered);
			} catch (Exception e) {
				e.printStackTrace();
				throw new FlexException(e.getMessage());
			}

			// set rfid
			personVO.setRfid(rfid);
		}
		return personVO;
	}



	public static List<PersonVO> createPersonListVO(Set<IEntityPerson> persons, String rfid, Registering service) {
		List<PersonVO> personsVO = new ArrayList<PersonVO>();

		for(IEntityPerson person:persons) {
			personsVO.add(createPersonVO(person, rfid, service));
		}
		return personsVO;
	}

	public static List<PersonVO> createPersonListVO(List<IEntityPerson> persons, String rfid, Registering service) {
		List<PersonVO> personsVO = new ArrayList<PersonVO>();

		for(IEntityPerson person:persons) {
			personsVO.add(createPersonVO(person, rfid, service));
		}
		logger.fine("personsVO=" + personsVO);

		return personsVO;
	}





	public static RegisteredVO createRegisteredVO(IEntityRegistered registered, Registering service) {
		RegisteredVO registeredVO = new RegisteredVO();
		if (registered != null) {
			registeredVO.setId(registered.getId());
			registeredVO.setName(registered.getName());
			registeredVO.setArrivalDate(registered.getArrivalDate());
			registeredVO.setElapsedTime(registered.getElapsedTime());
			registeredVO.setCategory(createCategoryVO(registered.getCategory()));
			registeredVO.setCompetition(createCompetitionVO(registered.getCompetition()));
			registeredVO.setEvent(createEventVO(registered.getEvent()));
			registeredVO.setLabel(createLabelVO(registered.getLabel()));
			registeredVO.setPaid(registered.isPaid());
			registeredVO.setPersons(createPersonListVO(registered.getPersons(), registered.getLabel().getRfid(), service));
			registeredVO.setRegisteringDate(registered.getRegisteringDate());
			registeredVO.setTeamed(registered.isTeamed());
			registeredVO.setRank(-1);
			registeredVO.setProvidedHealthForm(registered.isProvidedHealthForm());
			registeredVO.setSource(registered.getSource());
			registeredVO.setClub(registered.getClub());
		}
		return registeredVO;

	}

	public static List<RegisteredVO> createRegisteredListVO(List<IEntityRegistered> registereds, Registering service) {
		List<RegisteredVO> registeredsVO = new ArrayList<RegisteredVO>();

		for(IEntityRegistered registered:registereds) {
			registeredsVO.add(createRegisteredVO(registered, service));
		}
		logger.fine("registeredsVO=" + registeredsVO);

		return registeredsVO;
	}

}
