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
import net.o3s.web.common.Util;
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
		System.out.println("registereds=" + registereds);
		return Util.createRegisteredListVO(registereds, this);
	}

	// get all registered
	public List<RegisteredVO> getAllRegistereds() {
		setRegisteringEJB();
		List<IEntityRegistered> registereds = registering.findAllRegistered();
		System.out.println("registereds=" + registereds);
		return Util.createRegisteredListVO(registereds, this);
	}

	// get first name list
	public List<String> getFirstNameWithPrefix(String prefix) {
		setRegisteringEJB();
		List<String> firstNameList = registering.findAllFirstName(prefix);
		System.out.println("firstNameList=" + firstNameList);
		return firstNameList;
	}

	// get last name list
	public List<String> getLastNameWithPrefix(String prefix) {
		setRegisteringEJB();
		List<String> lastNameList = registering.findAllLastName(prefix);
		System.out.println("lastNameList=" + lastNameList);
		return lastNameList;
	}

	// get category associated with a person
	public IEntityCategory getCategory(IEntityPerson person) throws RegisteringException {
		setAdminEJB();
		System.out.println("getCategory:person " + person);
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

		System.out.println("person=" + pVO);
		return pVO;
	}

	// update person
	public PersonVO updatePerson(PersonVO personVO) {
		setRegisteringEJB();
		IEntityPerson person = null;
		System.out.println("update person" + personVO.getId());

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

		System.out.println("person=" + pVO);
		return pVO;
	}



	public int removePerson(int personId) {
		try {
			registering.removePerson(personId);
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		System.out.println("remove person=" + personId);
		return personId;

	}

	// get person if exist
	public PersonVO getPerson(String lastname, String firstname, Date birthday) {
		setRegisteringEJB();
		IEntityPerson person = null;
		System.out.println("getPerson <" + firstname + ", " + lastname + ", "  + birthday + ">");

		person = registering.findPersonFromLastnameFirstNameBirthDay(lastname, firstname, birthday);

		PersonVO pVO = null;

		if (person != null) {
			pVO = Util.createPersonVO(person, this);
		}
		System.out.println("person=" + pVO);
		return pVO;

	}

	// check if person if already registered
	public Boolean isAlreadyRegistered(int personId) {
		setRegisteringEJB();
		IEntityRegistered registered = null;
		System.out.println("isAlreadyRegistered <" + personId + ">");

		registered = registering.findRegisteredFromPerson(personId);

		System.out.println("registered=" + registered);

		return registered != null;

	}

	// create registered
	public List<RegisteredVO> createRegistered(RegisteredVO registeredVO) {

		setRegisteringEJB();
		List<IEntityPerson> persons = new ArrayList<IEntityPerson>();

		System.out.println("registeredVO=" + registeredVO);

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
					registeredVO.isPaid());
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		} catch (AlreadyExistException e) {
				e.printStackTrace();
				throw new FlexException(e.getMessage());
		}

		System.out.println("registereds=" + registereds);

		return Util.createRegisteredListVO(registereds, this);

	}

	// update registered
	public RegisteredVO updateRegistered(RegisteredVO registeredVO) {
		setRegisteringEJB();

		System.out.println("registeredVO=" + registeredVO);

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
					registeredVO.isPaid());
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}

		RegisteredVO rVO = Util.createRegisteredVO(registered, this);

		System.out.println("registered=" + rVO);
		return rVO;
	}

	public void removeRegistered(int id) {
		try {
			registering.removeRegistered(id);
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		System.out.println("remove registered=" + id);
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
    					registeredVO.isPaid());
    			logger.log(Level.FINE, "import Registered <" + registereds + ">");

    		} catch (Exception e) {
    			e.printStackTrace();
    			logger.log(Level.SEVERE, "Unable to import registered <" + registeredVO + ">", e);

    			//throw new FlexException(e.getMessage());
    		}
   		}
    }
}
