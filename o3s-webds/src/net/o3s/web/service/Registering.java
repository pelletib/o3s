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
package net.o3s.web.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.o3s.apis.AlreadyExistException;
import net.o3s.apis.IEJBAdminRemote;
import net.o3s.apis.IEJBRegisteringRemote;
import net.o3s.apis.IEntityCategory;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityPerson;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.RegisteringException;
import net.o3s.apis.ReportException;
import net.o3s.web.common.Util;
import net.o3s.web.vo.CategoryVO;
import net.o3s.web.vo.FlexException;
import net.o3s.web.vo.PersonVO;
import net.o3s.web.vo.RegisteredStatisticsVO;
import net.o3s.web.vo.RegisteredVO;

public class Registering {

	/**
	 * Logger
	 */
    private static Logger logger = Logger.getLogger(Registering.class.getName());

	//@EJB
	private IEJBRegisteringRemote registering;

	//@EJB
	private IEJBAdminRemote admin;

	private void setAdminEJB() {

		InitialContext context=null;

		if (admin == null) {
			try {
				context = new InitialContext();
				admin = (IEJBAdminRemote) context.lookup("net.o3s.beans.admin.AdminBean_net.o3s.apis.IEJBAdminRemote@Remote");

			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private void setRegisteringEJB() {

		InitialContext context=null;

		if (registering == null) {
			try {
				context = new InitialContext();
				registering = (IEJBRegisteringRemote) context.lookup("net.o3s.beans.registering.RegisteringBean_net.o3s.apis.IEJBRegisteringRemote@Remote");

			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}


	// get registered from default event
	public List<RegisteredVO> getRegistereds() {
		setRegisteringEJB();
		List<IEntityRegistered> registereds = registering.findAllRegisteredFromDefaultEvent();
		logger.fine("registereds=" + registereds);
		return Util.createRegisteredListVO(registereds, this);
	}

	// get registered from default event
	public List<PersonVO> getPersons() {
		setRegisteringEJB();
		List<IEntityPerson> persons = registering.findAllPersons();
		logger.fine("persons=" + persons);
		return Util.createPersonListVO(persons, this);
	}

	// get arrivals order by duration
	public List<RegisteredVO> getArrivalsFromCompetition(final int competitionId) throws RegisteringException {

		setRegisteringEJB();

		List<IEntityRegistered> registereds = null;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByDuration(competitionId);
		} catch (RegisteringException re) {
			re.printStackTrace();
    		throw new RegisteringException("Unable to find registereds for competition " + competitionId, re);
		}

		logger.fine("registereds=" + registereds);

		List<RegisteredVO> registeredsVO = Util.createRegisteredListVO(registereds, this);

		// set the rank
		int rank = 1;
		for (RegisteredVO registeredVO:registeredsVO) {
			registeredVO.setRank(rank);
			rank++;
		}

    	return registeredsVO;
	}

	// get arrivals order by category and duration
	public List<RegisteredVO> getArrivalsFromCompetitionOrderByCategory(final int competitionId, List<CategoryVO> categoriesVO) throws RegisteringException {

		setRegisteringEJB();

		List<Integer> categoriesId = new ArrayList<Integer>();
		if (categoriesVO != null) {
			for (CategoryVO categoryVO:categoriesVO) {
				categoriesId.add(categoryVO.getId());
			}
		}
		List<IEntityRegistered> registereds = null;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByCategoryAndDuration(competitionId, categoriesId);
		} catch (RegisteringException re) {
			re.printStackTrace();
    		throw new RegisteringException("Unable to find registereds for competition " + competitionId, re);
		} catch (Exception e) {
			e.printStackTrace();
    		throw new RegisteringException("Unable to find registereds for competition " + competitionId, e);
		}


		logger.fine("registereds=" + registereds);

		List<RegisteredVO> registeredsVO = Util.createRegisteredListVO(registereds, this);

		// set the rank
		int rank = 1;
		int catId = -1;
		for (RegisteredVO registeredVO:registeredsVO) {
			if (registeredVO.getCategory().getId() != catId) {
				rank = 1;
				catId = registeredVO.getCategory().getId();
			}
			registeredVO.setRank(rank);
			rank++;
		}

    	return registeredsVO;
	}


	// get all registered
	public List<RegisteredVO> getAllRegistereds() {
		setRegisteringEJB();
		List<IEntityRegistered> registereds = registering.findAllRegistered();
		logger.fine("registereds=" + registereds);
		return Util.createRegisteredListVO(registereds, this);
	}


	// get first name list
	public List<String> getFirstNameWithPrefix(String prefix) {
		setRegisteringEJB();
		List<String> firstNameList = registering.findAllFirstName(prefix);
		logger.fine("firstNameList=" + firstNameList);
		return firstNameList;
	}

	// get last name list
	public List<String> getLastNameWithPrefix(String prefix) {
		setRegisteringEJB();
		List<String> lastNameList = registering.findAllLastName(prefix);
		logger.fine("lastNameList=" + lastNameList);
		return lastNameList;
	}

	// get category associated with a person
	public IEntityCategory getCategory(IEntityPerson person) throws RegisteringException {
		setAdminEJB();
		logger.fine("getCategory:person " + person);
    	List<IEntityCategory> categories = admin.findCategoryFromDatesAndSex(person.getBirthday(), person.getSex());
    	if (categories == null || categories.isEmpty()){
    		throw new RegisteringException("Categorie non trouvee pour le couple (date de naissance/sexe):" + person.getBirthday() + "," + person.getSex());
    	}
    	// get the first one (almost equiv to random)
    	return categories.get(0);
	}

	// create person (if exist and force, update it)
	public PersonVO createPerson(PersonVO personVO, Boolean force) {
		setRegisteringEJB();
		IEntityPerson person = null;
		try {
			person = registering.createPerson(personVO.getLastname(), personVO.getFirstname(), personVO.getClub(), personVO.getLicense(), personVO.getEmail(), personVO.getSex(), personVO.getBirthday());
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		} catch (AlreadyExistException e) {
			if (force == true) {
				person = registering.findPersonFromLastnameFirstNameBirthDay(personVO.getLastname(),personVO.getFirstname(), personVO.getBirthday());
				personVO.setId(person.getId());
				return updatePerson(personVO);
			} else {
				e.printStackTrace();
				throw new FlexException(e.getMessage());
			}
		}

		PersonVO pVO = Util.createPersonVO(person, this);

		logger.fine("person=" + pVO);
		return pVO;
	}

	// update person
	public PersonVO updatePerson(PersonVO personVO) {
		setRegisteringEJB();
		IEntityPerson person = null;
		logger.fine("update person" + personVO.getId());

		try {
			person = registering.updatePerson(personVO.getId(), personVO.getLastname(), personVO.getFirstname(), personVO.getClub(), personVO.getLicense(), personVO.getEmail(), personVO.getSex(), personVO.getBirthday());
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}

		PersonVO pVO = Util.createPersonVO(person, this);

		// get the related category
		try {
			IEntityCategory category = getCategory(person);
			pVO.setComputedCategory(category.getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}

		logger.fine("person=" + pVO);
		return pVO;
	}



	public int removePerson(int personId) {

		// check if the person is not registered
		setRegisteringEJB();
		List<IEntityRegistered> registereds = null;
		registereds = registering.findRegisteredFromPersonForAllEvent(personId);
		if (registereds != null && !registereds.isEmpty()) {
			String msg = "Impossible de supprimer une personne inscrite (" +
			registereds.get(0).getEvent().getName() + "," +
            registereds.get(0).getCompetition().getName() + "," +
            registereds.get(0).getId() + ")";
			logger.severe(msg);
			throw new FlexException(msg);
		}

		try {
			registering.removePerson(personId);
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		logger.fine("remove person=" + personId);
		return personId;

	}

	// get person if exist
	public PersonVO getPerson(String lastname, String firstname, Date birthday) {
		setRegisteringEJB();
		IEntityPerson person = null;
		logger.fine("getPerson <" + firstname + ", " + lastname + ", "  + birthday + ">");

		person = registering.findPersonFromLastnameFirstNameBirthDay(lastname, firstname, birthday);

		PersonVO pVO = null;

		if (person != null) {
			pVO = Util.createPersonVO(person, this);
		}
		logger.fine("person=" + pVO);
		return pVO;

	}

	// check if person if already registered
	public Boolean isAlreadyRegisteredForDefaultEvent(int personId) {
		setRegisteringEJB();
		IEntityRegistered registered = null;
		logger.fine("isAlreadyRegistered <" + personId + ">");

		registered = registering.findRegisteredFromPersonForDefaultEvent(personId);

		logger.fine("registered=" + registered);

		return registered != null;

	}

	// check if person if already registered
	public Boolean isAlreadyRegisteredForAllEvent(int personId) {
		setRegisteringEJB();
		List<IEntityRegistered> registereds = null;
		logger.fine("isAlreadyRegistered <" + personId + ">");

		registereds = registering.findRegisteredFromPersonForAllEvent(personId);

		logger.fine("registereds=" + registereds);

		return registereds != null && !registereds.isEmpty();

	}

	// create registered
	public List<RegisteredVO> createRegistered(RegisteredVO registeredVO) {

		setRegisteringEJB();
		List<IEntityPerson> persons = new ArrayList<IEntityPerson>();

		logger.fine("registeredVO=" + registeredVO);

		List<PersonVO> personsVO = registeredVO.getPersons();
		for (PersonVO personVO:personsVO) {
			persons.add(registering.findPersonFromId(personVO.getId()));
		}

		if (registeredVO.getCompetition() == null) {
			return null;
		}

		List<IEntityRegistered> registereds = null;
		try {
			registereds = registering.createRegistered(
					persons,
					registeredVO.getCompetition().getId(),
					registeredVO.isTeamed(),
					registeredVO.getName(),
					registeredVO.isPaid(),
					registeredVO.isProvidedHealthForm());
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		} catch (AlreadyExistException e) {
				e.printStackTrace();
				throw new FlexException(e.getMessage());
		}

		logger.fine("registereds=" + registereds);

		return Util.createRegisteredListVO(registereds, this);

	}

	// update registered
	public RegisteredVO updateRegistered(RegisteredVO registeredVO) {
		setRegisteringEJB();

		logger.fine("registeredVO=" + registeredVO);

		List<IEntityPerson> persons = new ArrayList<IEntityPerson>();

		List<PersonVO> personsVO = registeredVO.getPersons();
		for (PersonVO personVO:personsVO) {
			persons.add(registering.findPersonFromId(personVO.getId()));
		}

		if (registeredVO.getCompetition() == null) {
			return null;
		}

		IEntityRegistered registered = null;

		try {
			registered = registering.updateRegistered(registeredVO.getId(),
					persons,
					registeredVO.getCompetition().getId(),
					registeredVO.isTeamed(),
					registeredVO.getName(),
					registeredVO.isPaid(),
					registeredVO.isProvidedHealthForm());
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}

		RegisteredVO rVO = Util.createRegisteredVO(registered, this);

		logger.fine("registered=" + rVO);
		return rVO;
	}

	public void removeRegistered(int id) {
		try {
			registering.removeRegistered(id);
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		logger.fine("remove registered=" + id);
	}

    public List<RegisteredStatisticsVO> getStatistics() throws RegisteringException {
		setAdminEJB();
    	List<IEntityCompetition> competitions = admin.findAllCompetitionsFromDefaultEvent();
    	List<IEntityCategory> categories = admin.findAllCategoriesFromDefaultEvent();

		setRegisteringEJB();

		int registeredNb = - 1;
		int arrivalNb = -1;
		int totalRegisteredNb = 0;
		int totalArrivalNb = 0;
		RegisteredStatisticsVO registeredStatisticsVO =  null;

		List<RegisteredStatisticsVO> statistics = new ArrayList<RegisteredStatisticsVO>();

		for (IEntityCompetition competition:competitions) {

			if (competition.getName().equals("Unknown")) {
				continue;
			}

			// statistics for scratch
			registeredStatisticsVO = new RegisteredStatisticsVO();
			registeredStatisticsVO.setCompetition(competition.getName());
			registeredStatisticsVO.setCategory("Total");

			registeredNb = registering.countRegisteredFromCompetition(competition.getId());
			totalRegisteredNb += registeredNb;
			registeredStatisticsVO.setRegisteredNumber(registeredNb);
			arrivalNb = registering.countArrivalFromCompetition(competition.getId());
			totalArrivalNb += arrivalNb;
			registeredStatisticsVO.setArrivalNumber(arrivalNb);
			statistics.add(registeredStatisticsVO);

			// statistics by categories
			for (IEntityCategory category:categories) {

				// check if the competition is compatible with the category
				for (IEntityCompetition competitionInCategory:category.getCompetitions()) {
					if (competitionInCategory.getId() == competition.getId()) {

						registeredNb = registering.countRegisteredFromCompetitionAndCategory(competition.getId(), category.getId());
						arrivalNb = registering.countArrivalFromCompetitionAndCategory(competition.getId(), category.getId());

						// don't add if equals 0
						if (registeredNb != 0 || arrivalNb != 0) {
							registeredStatisticsVO = new RegisteredStatisticsVO();
							registeredStatisticsVO.setCompetition(competition.getName());
							registeredStatisticsVO.setCategory(category.getName());
							registeredStatisticsVO.setRegisteredNumber(registeredNb);
							registeredStatisticsVO.setArrivalNumber(arrivalNb);
							statistics.add(registeredStatisticsVO);
							break;
						}
					}

				}
			}
		}

		// Add total counter
		registeredStatisticsVO = new RegisteredStatisticsVO();
		registeredStatisticsVO.setCompetition("Total");
		registeredStatisticsVO.setCategory("Total");
		registeredStatisticsVO.setRegisteredNumber(totalRegisteredNb);
		registeredStatisticsVO.setArrivalNumber(totalArrivalNb);
		statistics.add(registeredStatisticsVO);

		return statistics;
    }

    public void importRegistered(List<RegisteredVO> registeredsVO) throws RegisteringException {

   		setRegisteringEJB();
   		setAdminEJB();

		logger.log(Level.FINE, "input <" + registeredsVO + ">");

   		for (RegisteredVO registeredVO : registeredsVO) {

   			List<IEntityPerson> persons = new ArrayList<IEntityPerson>();

   			List<PersonVO> personsVO = registeredVO.getPersons();

   			// create person
   			for (PersonVO personVO:personsVO) {
   				try {
   					if (personVO.getLastname() == null || personVO.getLastname().equals("") ||
   							personVO.getFirstname() == null || personVO.getFirstname().equals("") ||
   							personVO.getBirthday() == null ||
   							!(personVO.getSex()=='F' || personVO.getSex()=='M')) {
   						throw new Exception("Les champs <Nom/Prenom/DdN/Sexe> sont obligatoires!");
   					}


   					IEntityPerson person = null;
   					person = registering.createPerson(personVO.getLastname(),
   							personVO.getFirstname(),
   							personVO.getClub(),
   							personVO.getLicense(),
   							personVO.getEmail(),
   							personVO.getSex(),
   							personVO.getBirthday());
   					persons.add(person);
   					logger.log(Level.FINE, "import Person <" + person + ">");

   				} catch (Exception e) {
   					e.printStackTrace();
   					logger.log(Level.SEVERE, "Unable to import person <" + personVO + ">", e);
   					continue;
   					//throw new FlexException(e.getMessage());
   				}
   			}

   			try {

   				// create registered
   				if (registeredVO.getCompetition() == null || registeredVO.getCompetition().equals("Unknown")) {

   					// Compute the category
   					List<IEntityCategory> categories = admin.findCategoryFromDatesAndSex(persons.get(0).getBirthday(), persons.get(0).getSex());
   					if (categories == null || categories.isEmpty()){
   						throw new RegisteringException("Categorie non trouvee pour le couple (date naissance/sexe):" + persons.get(0).getBirthday() + "," + persons.get(0).getSex());
   					}

   					// get the first one (almost equiv to random)
   					IEntityCategory category = categories.get(0);
   					for(IEntityCompetition competition:category.getCompetitions()) {
   	   					logger.log(Level.FINE, "Selected competition (1st) <" + competition + ">");
   	   					registeredVO.setCompetition(Util.createCompetitionVO(competition));
   	   					break;
   					}

   				}
   			} catch (Exception e) {
	    			e.printStackTrace();
	    			logger.log(Level.SEVERE, "Unable to get competition for registered <" + registeredVO + ">", e);
	    			continue;
	    			//throw new FlexException(e.getMessage());
			}

    		List<IEntityRegistered> registereds = null;
    		try {
    			registereds = registering.createRegistered(
    					persons,
    					registeredVO.getCompetition().getId(),
    					registeredVO.isTeamed(),
    					registeredVO.getName(),
    					registeredVO.isPaid(),
    					registeredVO.isProvidedHealthForm());
    			logger.log(Level.FINE, "import Registered <" + registereds + ">");

    		} catch (Exception e) {
    			e.printStackTrace();
    			logger.log(Level.SEVERE, "Unable to import registered <" + registeredVO + ">", e);

    			//throw new FlexException(e.getMessage());
    		}
   		}
    }
}
