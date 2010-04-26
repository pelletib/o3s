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

import javax.naming.InitialContext;
import javax.naming.NamingException;


import net.o3s.apis.IEJBAdminRemote;
import net.o3s.apis.IEJBRegisteringRemote;
import net.o3s.apis.IEntityCategory;
import net.o3s.apis.IEntityPerson;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.RegisteringException;
import net.o3s.web.common.Util;
import net.o3s.web.vo.CategoryVO;
import net.o3s.web.vo.FlexException;
import net.o3s.web.vo.PersonVO;
import net.o3s.web.vo.RegisteredVO;



public class Registering {

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
		return Util.createRegisteredListVO(registereds);
	}

	// get all registered
	public List<RegisteredVO> getAllRegistereds() {
		setRegisteringEJB();
		List<IEntityRegistered> registereds = registering.findAllRegistered();
		System.out.println("registereds=" + registereds);
		return Util.createRegisteredListVO(registereds);
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
	private IEntityCategory getCategory(IEntityPerson person) throws RegisteringException {
		setAdminEJB();
		System.out.println("getCategory:person " + person);
    	List<IEntityCategory> categories = admin.findCategoryFromDatesAndSex(person.getBirthday(), person.getSex());
    	if (categories == null || categories.isEmpty()){
    		throw new RegisteringException("Category not found for (birthday/sex):" + person.getBirthday() + "," + person.getSex());
    	}
    	// get the first one (almost equiv to random)
    	return categories.get(0);
	}

	// create person
	public PersonVO createPerson(PersonVO personVO) {
		setRegisteringEJB();
		IEntityPerson person = null;
		try {
			person = registering.createPerson(personVO.getLastname(), personVO.getFirstname(), personVO.getClub(), personVO.getEmail(), personVO.getSex(), personVO.getBirthday());
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}

		PersonVO pVO = Util.createPersonVO(person);

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

	// update person
	public PersonVO updatePerson(PersonVO personVO) {
		setRegisteringEJB();
		IEntityPerson person = null;
		System.out.println("update person" + personVO.getId());

		try {
			person = registering.updatePerson(personVO.getId(), personVO.getLastname(), personVO.getFirstname(), personVO.getClub(), personVO.getEmail(), personVO.getSex(), personVO.getBirthday());
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}

		PersonVO pVO = Util.createPersonVO(person);

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



	public PersonVO removePerson(PersonVO personVO) {
		try {
			registering.removePerson(personVO.getId());
		} catch (RegisteringException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		System.out.println("remove person=" + personVO);
		return personVO;

	}

	// get person if exist
	public PersonVO getPerson(String lastname, String firstname, Date birthday) {
		setRegisteringEJB();
		IEntityPerson person = null;
		System.out.println("getPerson <" + firstname + ", " + lastname + ", "  + birthday + ">");

		person = registering.findPersonFromLastnameFirstNameBirthDay(lastname, firstname, birthday);

		PersonVO pVO = null;

		if (person != null) {
			pVO = Util.createPersonVO(person);
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
		}

		System.out.println("registereds=" + registereds);

		return Util.createRegisteredListVO(registereds);

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

		RegisteredVO rVO = Util.createRegisteredVO(registered);

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



}
