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
package net.o3s.beans.registering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.o3s.apis.AlreadyExistException;
import net.o3s.apis.IEJBAdminLocal;
import net.o3s.apis.IEJBNotificationProducerLocal;
import net.o3s.apis.IEJBRegisteringLocal;
import net.o3s.apis.IEJBRegisteringRemote;
import net.o3s.apis.IEntityCategory;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityEvent;
import net.o3s.apis.IEntityLabel;
import net.o3s.apis.IEntityPerson;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.InvalidException;
import net.o3s.apis.NotificationMessageException;
import net.o3s.apis.RegisteringException;
import net.o3s.apis.TrackingMessageException;
import net.o3s.persistence.Label;
import net.o3s.persistence.Person;
import net.o3s.persistence.Registered;

/**
 * Session Bean implementation class RegisteringBean
 */
@Stateless
@Local(IEJBRegisteringLocal.class)
@Remote(IEJBRegisteringRemote.class)
public class RegisteringBean implements IEJBRegisteringLocal,IEJBRegisteringRemote {

	/**
	 * Logger
	 */
    private static Logger logger = Logger.getLogger(RegisteringBean.class.getName());

    /**
     * Persistent manager
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Admin EJB
     */
    @EJB
    private IEJBAdminLocal admin;

    /**
     * Notification EJB
     */
    @EJB
    private IEJBNotificationProducerLocal notification;

    /**
     * Check competition against category
     * @param competition
     * @param category
     * @throws RegisteringException
     */
    private void checkCompetitionCategory(IEntityCompetition competition, IEntityCategory category) throws RegisteringException {

    	boolean found = false;
    	Set<IEntityCompetition> competitionsInCategory = category.getCompetitions();
		logger.fine("Check Category <" + category.getName() + "> with Competition <" + competition.getName() + ">");

    	for (IEntityCompetition competitionInCategory : competitionsInCategory) {
			logger.fine("Check competitionInCategory <" + competitionInCategory.getId() + "> Competition <" + competition.getId() + ">");

    		if (competitionInCategory.getId() == competition.getId()) {
    			logger.fine("Category <" + category.getName() + "> is compatible with Competition <" + competition.getName() + ">");
    			found = true;
    			break;
    		}
    	}
    	if (!found) {
    		throw new RegisteringException("La categorie <" + category.getName() + "> n'est pas compatible avec la competition <" + competition.getName() + ">");
    	}
    	return;
    }

    /**
     * Get label from value
     * @param value
     * @return
     */
    private IEntityLabel findLabelFromValue(final String value) {
    	IEntityEvent event = admin.findDefaultEvent();
    	Query query = this.entityManager.createNamedQuery("LABEL_FROM_VALUE_AND_EVENT");
        query.setParameter("VALUE", value);
        query.setParameter("EVENTID", event.getId());

        IEntityLabel label = null;
        try {

        	label = (IEntityLabel) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        }

        return label;

    }

    /**
     * Get label from rfid
     * @param value
     * @return
     */
    private IEntityLabel findLabelFromRfid(final String rfid) {
    	IEntityEvent event = admin.findDefaultEvent();
    	Query query = this.entityManager.createNamedQuery("LABEL_FROM_RFID_AND_EVENT");
        query.setParameter("RFID", rfid);
        query.setParameter("EVENTID", event.getId());

        IEntityLabel label = null;
        try {

        	label = (IEntityLabel) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        }

        return label;

    }

    /**
     * Get label from number
     * @param number
     * @return
     */
    private IEntityLabel findLabelFromNumber(final int number) {
    	IEntityEvent event = admin.findDefaultEvent();
        Query query = this.entityManager.createNamedQuery("LABEL_FROM_NUMBER_AND_EVENT");
        query.setParameter("NUMBER", number);
        query.setParameter("EVENTID", event.getId());

        IEntityLabel label = null;
        try {

        	label = (IEntityLabel) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        }

        return label;

    }

    /**
     * Get all labels
     */
    @SuppressWarnings("unchecked")
    public List<IEntityLabel> findAllLabels() {
        Query query = this.entityManager.createNamedQuery("ALL_LABELS");

        List<IEntityLabel> labels = null;
        try {
        	labels = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	labels = new ArrayList<IEntityLabel>();
        }
        return labels;

    }

    /**
     * String padding at left
     * @param s
     * @param n
     * @return
     */
    private String padLeft(String s, int n) {
        return String.format("%1$#" + n + "s", s);
    }

    /**
     * Create new label
     * @param competition
     * @param category
     * @param name
     * @return
     * @throws RegisteringException
     */
    private IEntityLabel createLabel(IEntityCompetition competition, IEntityCategory category, String name) throws RegisteringException {
    	IEntityLabel label = null;

    	// to avoid conflict problem between a web and local instance, the label allocation can be done in ascending or descending mode
    	// default is ascending
    	// descending mode is set thru a java property (o3s.descLabelAlloc=true)
    	Boolean descMode = false;
    	String descLabelAlloc = System.getProperty("o3s.descLabelAlloc");
    	if (descLabelAlloc != null && ! descLabelAlloc.equalsIgnoreCase("")) {
    		descMode = Boolean.parseBoolean(descLabelAlloc);
    	}

    	int labelNumber;
    	int round = 0;

    	if (descMode == true) {
        	labelNumber = competition.getLastLabelNumber() - 1;
        	if (labelNumber < competition.getLowerLabelNumber()) {
        		round++;
        		labelNumber = competition.getHigherLabelNumber();
        	}

    	} else {
        	labelNumber = competition.getLastLabelNumber() + 1;
        	if (labelNumber > competition.getHigherLabelNumber()) {
        		round++;
        		labelNumber = competition.getLowerLabelNumber();
        	}
    	}


    	while ((label = findLabelFromNumber(labelNumber)) != null) {
    		logger.log(Level.WARNING, "Dossard deja existant [" + label + "], nouvel essai");

        	if (descMode == true) {
        		labelNumber--;
            	if (labelNumber < competition.getLowerLabelNumber()) {
            		round++;
            		if (round>1) {
            			throw new RegisteringException("Debordement de numero de dossard");
            		}

            		labelNumber = competition.getHigherLabelNumber();

            	}

        	} else {
        		labelNumber++;
            	if (labelNumber > competition.getHigherLabelNumber()) {
            		round++;
            		if (round>1) {
            			throw new RegisteringException("Debordement de numero de dossard");
            		}
            		labelNumber = competition.getLowerLabelNumber();
            	}
        	}
    	}

    	// Label=<number><cat_sh><name>
    	//         5c       1c      2c
    	String labelValue = padLeft(String.valueOf(labelNumber),5) + category.getShortName() + name.substring(0, 2);
    	labelValue = labelValue.trim();

     	competition.setLastLabelNumber(labelNumber);
     	label = new Label();
    	label.setNumber(labelNumber);
    	label.setValue(labelValue);
    	label.setRfid("");
    	IEntityEvent event = admin.findDefaultEvent();
    	label.setEvent(event);
    	this.entityManager.persist(label);

    	return label;
    }

    public void setRfidToLabel(String labelData, String rfidOrg) throws RegisteringException {
    	// check if label already exists
    	if (labelData.length() > IEntityLabel.LABEL_VALUE_SIZE) {
    		throw new RegisteringException("Numero de dossard invalide <" + labelData + ">");
    	}
    	String rfid = rfidOrg;

    	if (!rfid.equals("EMPTY") && !isValidRfid(rfid)) {
    		// try to convert from us keyboard input to fr
    		//rfid=convertStringDigitFromUs2Fr(rfid);
    		//if (!isValidRfid(rfid)) {
    		throw new RegisteringException("Numero RFID invalide <" + rfid + ">");
    		//}
    	}
    	IEntityRegistered registered = null;
		try {
			registered = findRegisteredFromLabelData(labelData);
		} catch (InvalidException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (registered == null) {
			throw new RegisteringException("Numero de dossard invalide");
		}

    	IEntityLabel label = registered.getLabel();
		if (!label.getRfid().equals("") && !rfid.equals("EMPTY")) {
			throw new RegisteringException("Dossard deja associe avec le RFID '" + label.getRfid() + "'");
		}

    	if (rfid.equals("EMPTY")) {
    		label.setRfid("");
    	} else {

    		// check if the rfid is available
    		IEntityRegistered rl = null;
    		try {
    			rl = findRegisteredFromLabelData(rfid);
    		} catch (InvalidException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		if (rl!=null) {
    			throw new RegisteringException("Numero rfid '" + rfid + "' deja associe avec le dossard '" + rl.getLabel().getValue() + "'");
    		}

    		// rfid available, can be set
    		label.setRfid(rfid);
    	}

		logger.fine("Label '" + labelData + "' associated with rfid '" + rfid + "'");

		try {
			notification.sendRegisteringNotification(registered);
		} catch (NotificationMessageException e) {
			logger.log(Level.SEVERE, "Unable to send a notification :" + e.getMessage());
		}
    }

    /**
     * Create new label
     * @param labelValue
     * @throws RegisteringException
     */
    private IEntityLabel createLabel(IEntityCompetition competition, String labelValue, String rfid) throws AlreadyExistException, RegisteringException {

    	int labelNumber = 0;

    	// label format is DDDDCCC with d:digit
    	try {
    		labelNumber= Integer.parseInt(labelValue.substring(0, labelValue.length() - 3));
        } catch (NumberFormatException ne1) {
        		throw new RegisteringException(ne1.getMessage());
        }

    	IEntityLabel label = findLabelFromNumber(labelNumber);

    	if (label != null) {
    		throw new AlreadyExistException("Dossard deja existant [" + label + "]");
    	}

		label = new Label();
		label.setNumber(labelNumber);
		label.setValue(labelValue);
		label.setRfid(rfid);
    	IEntityEvent event = admin.findDefaultEvent();
    	label.setEvent(event);
		this.entityManager.persist(label);
    	return label;
    }

    /**
     * Get person from lastname/firstname/birthday
     */
    public IEntityPerson findPersonFromLastnameFirstNameBirthDay(final String lastname, final String firstname, final Date birthday) {
        Query query = this.entityManager.createNamedQuery("PERSON_FROM_LASTNAME_FIRSTNAME_BIRTHDAY");
        query.setParameter("LASTNAME", normalizeLastname(lastname));
        query.setParameter("FIRSTNAME", normalizeFirstname(firstname));
        query.setParameter("BIRTHDAY", birthday);

        IEntityPerson person = null;
        try {

        	person = (IEntityPerson) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        }

        return person;

    }

    /**
     * Find persons from Lastname and Firstname
     */
    @SuppressWarnings("unchecked")
    public  List<IEntityPerson> findPersonsFromLastnameFirstname(final String lastname, final String firstname) {
        Query query = this.entityManager.createNamedQuery("PERSON_FROM_LASTNAME_FIRSTNAME");
        query.setParameter("LASTNAME", normalizeLastname(lastname));
        query.setParameter("FIRSTNAME", normalizeFirstname(firstname));

        List<IEntityPerson> persons = null;
        try {
        	persons = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	persons = new ArrayList<IEntityPerson>();
        }
        return persons;
    }

    /**
     * Normalize lastname
     * @param lastname
     * @return
     */
    private String normalizeLastname(final String lastname) {
    	if (lastname != null) {
    		return lastname.toUpperCase();
    	} else {
    		return null;
    	}
    }

    /**
     * Normalize firstname
     * @param firstname
     * @return
     */
    private String normalizeFirstname(final String firstname) {
    	if (firstname != null) {
    		String capitalized = "";
    		if (firstname.length()>0) {
            	String firstLetter = firstname.substring(0,1);  // Get first letter
                String remainder   = firstname.substring(1);    // Get remainder of word.
                capitalized = firstLetter.toUpperCase() + remainder.toLowerCase();
    		}
        	return capitalized;
    	} else {
    		return null;
    	}
    }

    /**
     * Create new Person
     */
    public IEntityPerson createPerson(final String lastname,
    		                          final String firstname,
    		                          final String club,
    		                          final String license,
    		                          final String email,
    		                          final char sex,
    		                          final Date birthday) throws AlreadyExistException,RegisteringException {
    	IEntityPerson person = null;

    	person = findPersonFromLastnameFirstNameBirthDay(lastname, firstname, birthday);
        if (person == null) {
        	person = new Person();

        	person.setLastname(normalizeLastname(lastname));
        	person.setFirstname(normalizeFirstname(firstname));
        	person.setClub(club);
        	person.setLicense(license);
        	person.setEmail(email);
        	person.setSex(sex);

        	// check if birthday if before today (at least :)
        	Date today = new Date();
        	if (birthday.after(today)) {
            	throw new RegisteringException("Date d'anniversaire invalide");
        	}
        	person.setBirthday(birthday);

            this.entityManager.persist(person);
        } else {
        	throw new AlreadyExistException("La personne <" + lastname + ", " + firstname + ", " + birthday + "> existe deja !");
        }
        return person;
    }

    /**
     * Create new Person
     */
    public IEntityPerson createPerson(final IEntityPerson myPerson) throws AlreadyExistException,RegisteringException {

    	IEntityPerson person = null;
    	person = findPersonFromLastnameFirstNameBirthDay(myPerson.getLastname(), myPerson.getFirstname(), myPerson.getBirthday());

        if (person == null) {
        	// check if birthday if before today (at least :)
        	Date today = new Date();
        	if (myPerson.getBirthday().after(today)) {
            	throw new RegisteringException("Date d'anniversaire invalide");
        	}

            this.entityManager.persist(myPerson);
            person = myPerson;
        } else {
        	throw new AlreadyExistException("La personne <" + myPerson.getLastname() + ", " + myPerson.getFirstname() + ", " + myPerson.getBirthday() + "> existe deja !");
        }
        return person;
    }

    /**
     * Update a person
     */
    public IEntityPerson updatePerson(final int id, final String lastname, final String firstname, final String club, final String license, final String email, final char sex, final Date birthday) throws RegisteringException {
    	IEntityPerson person = null;

    	person = findPersonFromId(id);
        if (person != null) {
        	person.setLastname(normalizeLastname(lastname));
        	person.setFirstname(normalizeFirstname(firstname));
        	person.setClub(club);
        	person.setLicense(license);
        	person.setEmail(email);
        	person.setSex(sex);
        	person.setBirthday(birthday);
        } else {
        	throw new RegisteringException("La personne <" + id + "> n'existe pas !");
        }
        return person;
    }

    /**
     * Update person
     */
    public IEntityPerson updatePerson(IEntityPerson person) throws RegisteringException {

    	if (person == null) {
        	throw new RegisteringException("person is null");
    	}

    	IEntityPerson p = findPersonFromId(person.getId());
        if (p != null) {
        	p.setLastname(normalizeLastname(person.getLastname()));
        	p.setFirstname(normalizeFirstname(person.getFirstname()));
        	p.setClub(person.getClub());
        	person.setLicense(person.getLicense());
        	p.setEmail(person.getEmail());
        	p.setSex(person.getSex());
        	p.setBirthday(person.getBirthday());
        } else {
        	throw new RegisteringException("La personne <" + person + "> n'existe pas !");
        }
        return p;
    }

    /**
     * Get person from id
     */
    public IEntityPerson findPersonFromId(final int id) {
    	IEntityPerson person = null;
        try {
        	person = (IEntityPerson) this.entityManager.find(Person.class, id);
        } catch (javax.persistence.NoResultException e) {
        }
        return person;
    }

    /**
     * Find all lastnames
     */
    @SuppressWarnings("unchecked")
    public List<String> findAllLastName(final String prefix) {
    	List<String> lastNameList = null;
    	Query query = null;

    	if (prefix == null) {
    		query = this.entityManager.createNamedQuery("ALL_LASTNAME");
    	} else {
    		query = this.entityManager.createNamedQuery("LASTNAME_FROM_PATTERN");
            query.setParameter("PATTERN", normalizeLastname(prefix) + "%");
    	}
        try {
        	lastNameList = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	lastNameList = new ArrayList<String>();
        }
        return lastNameList;

    }

    /**
     * Find all firstnames
     */
    @SuppressWarnings("unchecked")
    public List<String> findAllFirstName(final String prefix) {

    	List<String> firstNameList = null;
    	Query query = null;

    	if (prefix == null) {
    		query = this.entityManager.createNamedQuery("ALL_FIRSTNAME");
    	} else {
    		query = this.entityManager.createNamedQuery("FIRSTNAME_FROM_PATTERN");
            query.setParameter("PATTERN", normalizeFirstname(prefix) + "%");
    	}

        try {
        	firstNameList = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	firstNameList = new ArrayList<String>();
        }
        return firstNameList;

    }

    /**
     * Remove a person
     */
    public void removePerson(final int id) throws RegisteringException {

    	IEntityPerson person = findPersonFromId(id);

    	if (person == null) {
    		throw new RegisteringException("person is null");
    	}
    	this.entityManager.remove(person);
    }

    /**
     * Get registered from name
     * @param name
     * @return
     */
    private IEntityRegistered findRegisteredFromName(final String name) {
    	IEntityEvent event = admin.findDefaultEvent();

        Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_NAME");
        query.setParameter("NAME", name);
        query.setParameter("EVENTID", event.getId());

        IEntityRegistered registered = null;
        try {

        	registered = (IEntityRegistered) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        }

        return registered;

    }

    /**
     * Get registered from person id
     */
    public IEntityRegistered findRegisteredFromPersonForDefaultEvent(final int personId) {

    	IEntityEvent event = admin.findDefaultEvent();

        Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_PERSONID");
        query.setParameter("PERSONID", personId);
        query.setParameter("EVENTID", event.getId());

        IEntityRegistered registered = null;
        try {
        	registered = (IEntityRegistered) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        }

        return registered;

    }

    /**
     * Get registered from person id for all event
     */
    public List<IEntityRegistered> findRegisteredFromPersonForAllEvent(final int personId) {

        Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_PERSONID_ALL_EVENTS");
        query.setParameter("PERSONID", personId);

        List<IEntityRegistered> registereds = null;
        try {
        	registereds = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	registereds = new ArrayList<IEntityRegistered>();
        } catch (Exception e){
        	e.printStackTrace();
        	registereds = new ArrayList<IEntityRegistered>();
        }

        return registereds;

    }

    /**
     * Get registered from label
     */
    public IEntityRegistered findRegisteredFromLabel(final String labelValue) throws InvalidException {
        Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_LABEL");
    	IEntityEvent event = admin.findDefaultEvent();
        query.setParameter("EVENTID", event.getId());
        query.setParameter("VALUE", labelValue);

        IEntityRegistered registered = null;
        try {

        	registered = (IEntityRegistered) query.getSingleResult();
        } catch (javax.persistence.NoResultException nre) {

        } catch (NonUniqueResultException nure) {
        	nure.printStackTrace();
        	throw new InvalidException("Impossible de trouver ce dossard [" + labelValue + "]", nure);
        } catch (Exception e){
        	e.printStackTrace();
        	throw new InvalidException("Impossible de trouver ce dossard [" + labelValue + "]", e);

        }

        return registered;

    }

    /**
     * Get registered from rfid tag
     */
    public IEntityRegistered findRegisteredFromRfid(final String rfid) throws InvalidException {
        Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_RFID");
        query.setParameter("RFID", rfid);
    	IEntityEvent event = admin.findDefaultEvent();
        query.setParameter("EVENTID", event.getId());


        IEntityRegistered registered = null;
        try {

        	registered = (IEntityRegistered) query.getSingleResult();
        } catch (javax.persistence.NoResultException nre) {

        } catch (NonUniqueResultException nure) {
        	nure.printStackTrace();
        	throw new InvalidException("Impossible de trouver ce dossard avec pour tag rfid [" + rfid + "]", nure);
        } catch (Exception e){
        	e.printStackTrace();
        	throw new InvalidException("Impossible de trouver ce dossard avec pour tag rfid  [" + rfid + "]", e);

        }

        return registered;

    }

	/**
	 * Check if a the string is a valid rfid
	 * @param s String to check
	 * @return true if the string is numeric
	 */
	public boolean isValidRfid(String s) {
		if (s == null)
			return false;

		if (s.length() == 0) {
			return false;
		}

		if (s.length() <= IEntityLabel.LABEL_VALUE_SIZE) {
			return false;
		}

		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i))) {
					return false;
			}
		}

		return true;

	}

	/**
	 * convert us digits serie to fr one
	 * @param s String to convert
	 * @return new string
	 */
	public String convertStringDigitFromUs2Fr(String s) {
		if (s == null)
			return null;

		if (s.length() == 0) {
			return "";
		}

		if (s.length() <= IEntityLabel.LABEL_VALUE_SIZE) {
			return "";
		}

		String newString = "";
		for (int i = 0; i < s.length(); i++) {
			newString += convertDigitFromUs2Fr( s.charAt(i));
		}

		return newString;

	}

	private char convertDigitFromUs2Fr(char c) {

			switch (c) {
			case '&':
				return '1';
			case 'é':
				return '2';
			case '\"':
				return '3';
			case '\'':
				return '4';
			case '(':
				return '5';
			case '-':
				return '6';
			case 'è':
				return '7';
			case '_':
				return '8';
			case 'ç':
				return '9';
			case 'à':
				return '0';
			default:
					return c;
			}
	}

    /**
     * Get registered from rfid tag
     */
    public IEntityRegistered findRegisteredFromLabelData(final String labelData) throws InvalidException {

		IEntityRegistered registered = null;

		// first check if size if greater than 8c and is composed of only digits -> rfid
		//String rfid=convertStringDigitFromUs2Fr(labelData);
		String rfid=labelData;

		if (isValidRfid(rfid)) {
			logger.fine("RFID detected: " + rfid);
			registered = findRegisteredFromRfid(rfid);

		} else  {
			// first check if num -> only the label number is provided
			// else it's a full label value

			try {
				int labelNumber = Integer.parseInt(labelData);
				logger.fine("Label number detected: " + labelNumber);
				registered = findRegisteredFromLabelNumber(labelNumber);
			} catch (NumberFormatException ne1) {
				logger.fine("Label (full) detected: " + labelData);
				registered = findRegisteredFromLabel(labelData);
			}
		}

		return registered;
    }

    /**
     * Get registered from labelNumber
     */
    public IEntityRegistered findRegisteredFromLabelNumber(final int labelNumber) throws InvalidException {
        Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_LABELNUMBER");
    	IEntityEvent event = admin.findDefaultEvent();
        query.setParameter("EVENTID", event.getId());
        query.setParameter("VALUE", labelNumber);

        IEntityRegistered registered = null;
        try {

        	registered = (IEntityRegistered) query.getSingleResult();
        } catch (javax.persistence.NoResultException nre) {

        } catch (NonUniqueResultException nure) {
        	nure.printStackTrace();
        	throw new InvalidException("Impossible de trouver ce dossard [" + labelNumber + "]", nure);
        } catch (Exception e){
        	e.printStackTrace();
        	throw new InvalidException("Impossible de trouver ce dossard [" + labelNumber + "]", e);

        }

        return registered;

    }

    /**
     * Get registered from id
     */
    public IEntityRegistered findRegisteredFromId(final int id) {
    	IEntityRegistered registered = null;
        try {
        	registered = (IEntityRegistered) this.entityManager.find(Registered.class, id);
        } catch (javax.persistence.NoResultException e) {
        }
        return registered;
    }

    /**
     * Remove a registered
     */
    public void removeRegistered(final int id) throws RegisteringException {

    	IEntityRegistered registered = findRegisteredFromId(id);

    	if (registered == null) {
    		throw new RegisteringException("registered <" + id + "> is null");
    	}
    	this.entityManager.remove(registered.getLabel());
    	this.entityManager.remove(registered);
    }

    /**
     * Reset All
     */
    public void resetAll() throws RegisteringException {

    	// remove all registereds
    	List<IEntityRegistered> registereds = findAllRegisteredFromDefaultEvent();
    	for (IEntityRegistered registered:registereds) {
    		removeRegistered(registered.getId());
    	}

    	// remove all persons who are not registered
       	List<IEntityPerson> persons = findAllPersons();
    	for (IEntityPerson person:persons) {
    		registereds = findRegisteredFromPersonForAllEvent(person.getId());
    		if (registereds == null || registereds.isEmpty()) {
        		removePerson(person.getId());
    		}
    	}

    	// reinit competition
    	Boolean descMode = false;
    	String descLabelAlloc = System.getProperty("o3s.descLabelAlloc");
    	if (descLabelAlloc != null && ! descLabelAlloc.equalsIgnoreCase("")) {
    		descMode = Boolean.parseBoolean(descLabelAlloc);
    	}

       	List<IEntityCompetition> competitions= admin.findAllCompetitionsFromDefaultEvent();
    	for (IEntityCompetition competition:competitions) {
    		if (descMode) {
    			competition.setLastLabelNumber(competition.getHigherLabelNumber());
    		} else {
    			competition.setLastLabelNumber(competition.getLowerLabelNumber());
    		}
    		competition.setStartingDate(null);
    	}
    }

    /**
     * Get registered on a competition order by duration
     */
    @SuppressWarnings("unchecked")
    public List<IEntityRegistered> findRegisteredFromCompetitionOrderByDuration(final int competitionId) throws RegisteringException {

        Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_COMPETITION_ORDERBY_ETIME");
        query.setParameter("COMPETITION", competitionId);

        List<IEntityRegistered> registereds = null;
        try {

        	registereds = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	registereds = new ArrayList<IEntityRegistered>();
        } catch (Exception e){
        	e.printStackTrace();
        	throw new RegisteringException("Unable to find competitionId [" + competitionId + "]", e);
        }
        return registereds;
    }

    /**
     * Get registered on competition order by category and duration
     */
    @SuppressWarnings("unchecked")
    public List<IEntityRegistered> findRegisteredFromCompetitionOrderByCategoryAndDuration(final int competitionId) throws RegisteringException {

    	Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_COMPETITION_ORDERBY_CATEGORY_ETIME");
        query.setParameter("COMPETITION", competitionId);

        List<IEntityRegistered> registereds = null;
        try {

        	registereds = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	registereds = new ArrayList<IEntityRegistered>();
        } catch (Exception e){
        	e.printStackTrace();
        	throw new RegisteringException("Unable to find competitionId [" + competitionId + "]", e);
        }
        return registereds;
    }

    /**
     * Get registered on competition order by club and duration
     */
    @SuppressWarnings("unchecked")
    public List<IEntityRegistered> findRegisteredFromCompetitionOrderByClubAndDuration(final int competitionId) throws RegisteringException {

    	Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_COMPETITION_ORDERBY_CLUB_ETIME");
        query.setParameter("COMPETITION", competitionId);

        List<IEntityRegistered> registereds = null;
        try {

        	registereds = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	registereds = new ArrayList<IEntityRegistered>();
        } catch (Exception e){
        	e.printStackTrace();
        	throw new RegisteringException("Unable to find competitionId [" + competitionId + "]", e);
        }
        return registereds;
    }

    /**
     * Get registered on competition order by category and duration for a list of categories
     */
    @SuppressWarnings("unchecked")
    public List<IEntityRegistered> findRegisteredFromCompetitionOrderByCategoryAndDuration(final int competitionId,  List<Integer> categoriesId) throws RegisteringException {

    	Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_COMPETITION_ORDERBY_CATEGORY_ETIME");
        query.setParameter("COMPETITION", competitionId);

        List<IEntityRegistered> registereds = null;
        try {

        	registereds = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	registereds = new ArrayList<IEntityRegistered>();
        } catch (Exception e){
        	e.printStackTrace();
        	throw new RegisteringException("Unable to find competitionId [" + competitionId + "]", e);
        }


       	// filter the registered according to the categories list
    	List<IEntityRegistered> filteredRegistereds;

    	if (categoriesId != null) {
    		filteredRegistereds = new ArrayList<IEntityRegistered>();
			for (IEntityRegistered registered : registereds) {
				for (Integer categoryId : categoriesId) {
					if (registered.getCategory().getId() == categoryId
							.intValue()) {
						filteredRegistereds.add(registered);
						break;
					}
				}
			}
    	} else {
    		filteredRegistereds = registereds;
    	}

        return filteredRegistereds;
    }

    /**
     * Count number of registered for a given competition
     */
    @SuppressWarnings("unchecked")
    public int countRegisteredFromCompetition(final int competitionId) throws RegisteringException {
    	IEntityEvent event = admin.findDefaultEvent();

    	Query query = this.entityManager.createNamedQuery("COUNT_REGISTERED_FROM_COMPETITION");
        query.setParameter("COMPETITION", competitionId);
        query.setParameter("EVENTID", event.getId());

        int count = -1;
        try {
        	count = ((Long) query.getSingleResult()).intValue();
        } catch (javax.persistence.NoResultException e) {
        	count = 0;
        } catch (Exception e){
        	e.printStackTrace();
        	throw new RegisteringException("Unable to find registered for competitionId [" + competitionId + "]", e);
        }
        return count;
    }

    /**
     * Count number of arrivals for a given competition
     */
    @SuppressWarnings("unchecked")
    public int countArrivalFromCompetition(final int competitionId) throws RegisteringException {
    	IEntityEvent event = admin.findDefaultEvent();

    	Query query = this.entityManager.createNamedQuery("COUNT_ARRIVAL_FROM_COMPETITION");
        query.setParameter("COMPETITION", competitionId);
        query.setParameter("EVENTID", event.getId());

        int count = -1;
        try {
        	count = ((Long) query.getSingleResult()).intValue();
        } catch (javax.persistence.NoResultException e) {
        	count = 0;
        } catch (Exception e){
        	e.printStackTrace();
        	throw new RegisteringException("Unable to find arrival for competitionId [" + competitionId + "]", e);
        }
        return count;
    }

    /**
     * Count number of registered for a given competition and category
     */
    @SuppressWarnings("unchecked")
    public int countRegisteredFromCompetitionAndCategory(final int competitionId, final int categoryId) throws RegisteringException {
    	IEntityEvent event = admin.findDefaultEvent();

    	Query query = this.entityManager.createNamedQuery("COUNT_REGISTERED_FROM_COMPETITION_AND_CATEGORY");
        query.setParameter("COMPETITION", competitionId);
        query.setParameter("CATEGORY", categoryId);
        query.setParameter("EVENTID", event.getId());

        int count = -1;
        try {
        	count = ((Long) query.getSingleResult()).intValue();
        } catch (javax.persistence.NoResultException e) {
        	count = 0;
        } catch (Exception e){
        	e.printStackTrace();
        	throw new RegisteringException("Unable to find registered for competitionId [" + competitionId + "] and categoryId [" + categoryId + "]", e);
        }
        return count;

    }

    /**
     * Count number of arrivals for a given competition and category
     */
    @SuppressWarnings("unchecked")
    public int countArrivalFromCompetitionAndCategory(final int competitionId, final int categoryId) throws RegisteringException {
    	IEntityEvent event = admin.findDefaultEvent();

    	Query query = this.entityManager.createNamedQuery("COUNT_ARRIVAL_FROM_COMPETITION_AND_CATEGORY");
        query.setParameter("COMPETITION", competitionId);
        query.setParameter("CATEGORY", categoryId);
        query.setParameter("EVENTID", event.getId());

        int count = -1;
        try {
        	count = ((Long) query.getSingleResult()).intValue();
        } catch (javax.persistence.NoResultException e) {
        	count = 0;
        } catch (Exception e){
        	e.printStackTrace();
        	throw new RegisteringException("Unable to find arrival for competitionId [" + competitionId + "] and categoryId [" + categoryId + "]", e);
        }
        return count;

    }

    /**
     * Get all registered
     */
    @SuppressWarnings("unchecked")
    public List<IEntityRegistered> findAllRegistered() {
        Query query = this.entityManager.createNamedQuery("ALL_REGISTERED");

        List<IEntityRegistered> registereds = null;
        try {
        	registereds = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	registereds = new ArrayList<IEntityRegistered>();
        }

        return registereds;

    }

    /**
     * Get all registered on default event
     */
    @SuppressWarnings("unchecked")
    public  List<IEntityRegistered> findAllRegisteredFromDefaultEvent() {
        Query query = this.entityManager.createNamedQuery("ALL_REGISTERED_FROM_EVENT");
        IEntityEvent event = admin.findDefaultEvent();
        query.setParameter("EVENTID", event.getId());

        List<IEntityRegistered> registereds = null;
        try {
        	registereds = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	registereds = new ArrayList<IEntityRegistered>();
        }
        return registereds;
    }

    /**
     * Get all registered on the specified event
     */
    @SuppressWarnings("unchecked")
    public  List<IEntityRegistered> findAllRegisteredFromEvent(int eventId) {
        Query query = this.entityManager.createNamedQuery("ALL_REGISTERED_FROM_EVENT");
        query.setParameter("EVENTID", eventId);

        List<IEntityRegistered> registereds = null;
        try {
        	registereds = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	registereds = new ArrayList<IEntityRegistered>();
        }
        return registereds;
    }

    /**
     * Get all registered on default event where registration date is greater than mindate
     */
    @SuppressWarnings("unchecked")
    public  List<IEntityRegistered> findAllRegisteredFromDefaultEventWhereRegisteringDateIsGreaterThan(Date minDate) {
        Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_EVENT_AFTER_REGISTRATION_DATE");
        IEntityEvent event = admin.findDefaultEvent();
        query.setParameter("EVENTID", event.getId());
        query.setParameter("MINDATE", minDate);

        List<IEntityRegistered> registereds = null;
        try {
        	registereds = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	registereds = new ArrayList<IEntityRegistered>();
        }
        return registereds;
    }



    /**
     * Get all persons
     */
    @SuppressWarnings("unchecked")
    public  List<IEntityPerson> findAllPersons() {
        Query query = this.entityManager.createNamedQuery("ALL_PERSONS");

        List<IEntityPerson> persons = null;
        try {
        	persons = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	persons = new ArrayList<IEntityPerson>();
        }
        return persons;
    }



    /**
     * Create a new registered
     */
    // TODO : deal with homonym
    @SuppressWarnings("unchecked")
	public List<IEntityRegistered> createRegistered(
			final List<IEntityPerson> persons,
			final int competitionId,
			boolean isTeamed,
			final String name,
			final boolean isPaid,
			final boolean providedHealthForm,
			final String source) throws AlreadyExistException, RegisteringException {
    	IEntityRegistered registered = null;
		List<IEntityRegistered> registeredsReturn = new ArrayList<IEntityRegistered>();
		String registeredName = null;

    	IEntityCompetition competition = admin.findCompetitionFromId(competitionId);

    	for (IEntityPerson person : persons) {

           	if (!isTeamed) {
           		registeredName = person.getLastname() + " " + person.getFirstname();
           	} else {
           		if (name == null || name.equals("") == true ) {
                	throw new RegisteringException("Ce nom n'est pas valide !");
           		}
           		registeredName = name;
           	}

    		//if (findRegisteredFromName(registeredName) != null) {
            //	throw new AlreadyExistException("L'inscrit <" + registeredName + "> existe deja !");
    		//}

    		IEntityRegistered clone = findRegisteredFromPersonForDefaultEvent(person.getId());
    		if ( clone != null) {
            	throw new AlreadyExistException("L'inscrit <" + registeredName + "> existe deja !" + clone);
    		}

        	registered = new Registered();
        	registered.setName(registeredName);

        	IEntityCategory category = null;

        	if (isTeamed) {
             	registered.setPersons(new HashSet(persons));
            	category = admin.findNoCategory();
            	registered.setCategory(category);
            	registered.setClub("N/A");

         	} else {
        		List<IEntityPerson> onlyOnePerson = new ArrayList<IEntityPerson>();
        		onlyOnePerson.add(person);
            	registered.setPersons(new HashSet(onlyOnePerson));

            	// retrieve the category
            	List<IEntityCategory> categories = admin.findCategoryFromDatesAndSex(onlyOnePerson.get(0).getBirthday(), onlyOnePerson.get(0).getSex());
            	if (categories == null || categories.isEmpty()){
            		throw new RegisteringException("Categorie non trouvee pour le couple (date naissance/sexe):" + persons.get(0).getBirthday() + "," + persons.get(0).getSex());
            	}
            	// get the first one (almost equiv to random)
            	category = categories.get(0);

            	// check the competition regarding the category
            	checkCompetitionCategory(competition, category);
            	registered.setCategory(category);
            	registered.setClub(onlyOnePerson.get(0).getClub());

        	}

          	// set registering date with the current date
        	Date currentDate = new Date();
        	registered.setRegisteringDate(currentDate);

    		registered.setIsTeamed(isTeamed);
        	registered.setIsPaid(isPaid);
        	registered.setProvidedHealthForm(providedHealthForm);
        	registered.setSource(source);

           	// set event related to the competition
        	registered.setEvent(competition.getEvent());
           	// set the competition
        	registered.setCompetition(competition);

        	// Allocate a new Label
        	IEntityLabel label = null;

        	label = createLabel(competition, category,  registeredName);

        	if (label == null) {
        		throw new RegisteringException("Impossible de creer un dossard pour le triplet (competition/categorie/nom)" + competition + ", " + category + ", " + registeredName);
        	}
        	registered.setLabel(label);
        	registered = this.entityManager.merge(registered);
        	registeredsReturn.add(registered);
        	if (isTeamed) {
               	// exit of the loop (only one iteration)
            	break;
        	}

    		try {
    			notification.sendRegisteringNotification(registered);
    		} catch (NotificationMessageException e) {
    			logger.log(Level.SEVERE, "Unable to send a notification :" + e.getMessage());
    		}

    	}

        return registeredsReturn;
    }

    /**
     * Update arrival date for a registered
     */
    @SuppressWarnings("unchecked")
	public void updateArrivalDateRegistered(
			final int id,
			final Date arrivalDate) throws RegisteringException,InvalidException {

		IEntityRegistered registered = findRegisteredFromId(id);
		if (registered == null) {
			throw new RegisteringException ("Impossible de recuperer l'inscrit <" + id + ">");
		}

		// Set the arrival date
		registered.setArrivalDate(arrivalDate);

		// Compute the duration
		if (registered.getCompetition().getStartingDate() == null) {
			throw new InvalidException ("Competition pas encore demarree pour l'inscrit :" + registered.getName());
		}
		registered.setElapsedTime(registered.getArrivalDate().getTime()-registered.getCompetition().getStartingDate().getTime());

    }

    /**
     * Re-Compute elapsed time for a registered
     */
    @SuppressWarnings("unchecked")
	public void recomputeElapsedTimeRegistereds(
			final int competitionId) throws RegisteringException {

    	IEntityCompetition competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
			throw new RegisteringException ("Competition inconnue :" + competitionId);
    	}

    	List<IEntityRegistered> registereds = findRegisteredFromCompetitionOrderByDuration(competitionId);

    	// Compute the duration
		if (competition.getStartingDate() == null) {
			// reinit elapsed time
			for (IEntityRegistered registered:registereds) {
	    		registered.setElapsedTime(0);
	    		//registered.setArrivalDate(null);
				logger.fine("Competition pas encore demarree pour l'inscrit :" + registered);
	    	}
		} else {

			for (IEntityRegistered registered:registereds) {
				registered.setElapsedTime(registered.getArrivalDate().getTime()-registered.getCompetition().getStartingDate().getTime());
				logger.fine("Date d'arrivee recalculee pour l'inscrit :" + registered);
			}
		}

    }

    /**
     * Update a registered
     */
    @SuppressWarnings("unchecked")
	public IEntityRegistered updateRegistered(
			final int id,
			final List<IEntityPerson> persons,
			final int competitionId,
			boolean isTeamed,
			final String name,
			final boolean isPaid,
			final boolean providedHealthForm,
			final String source) throws RegisteringException {
    	IEntityRegistered registered = null;
		String registeredName = null;

    	IEntityCompetition competition = admin.findCompetitionFromId(competitionId);

    	registered = findRegisteredFromId(id);
        if (registered != null) {

        	IEntityCompetition oldCompetition = registered.getCompetition();

         	for (IEntityPerson person : persons) {
               	if (!isTeamed) {
               		registeredName = person.getLastname() + " " + person.getFirstname();

               	} else {
               		if (name == null || name.equals("") == true ) {
                    	throw new RegisteringException("Nom invalide !");
               		}
               		registeredName = name;
               	}
               	// check the name uniqueness
               	IEntityRegistered r = findRegisteredFromName(name);

        		if (r != null && r.getId() != id) {
                	throw new RegisteringException("L'inscrit <" + name + "> existe deja !");
        		}

            	registered.setName(registeredName);

            	IEntityCategory category = null;

            	if (isTeamed) {
                 	registered.setPersons(new HashSet(persons));
                	category = admin.findNoCategory();
                	registered.setCategory(category);
                	registered.setClub("N/A");

             	} else {
            		List<IEntityPerson> onlyOnePerson = new ArrayList<IEntityPerson>();
            		onlyOnePerson.add(person);
                	registered.setPersons(new HashSet(onlyOnePerson));

                	// retrieve the category
                	List<IEntityCategory> categories = admin.findCategoryFromDatesAndSex(persons.get(0).getBirthday(), persons.get(0).getSex());
                	if (categories == null || categories.isEmpty()){
                		throw new RegisteringException("Categorie non trouvee pour le couple (date naissance/sexe):" + persons.get(0).getBirthday() + "," + persons.get(0).getSex());
                	}
                	// get the first one (almost equiv to random)
                	category = categories.get(0);

                	// check the competition regarding the category
                	checkCompetitionCategory(competition, category);
                	registered.setCategory(category);
                	registered.setClub(persons.get(0).getClub());

            	}

        		registered.setIsTeamed(isTeamed);
            	registered.setIsPaid(isPaid);
            	registered.setProvidedHealthForm(providedHealthForm);
            	registered.setSource(source);

            	// set the competition
            	registered.setCompetition(competition);

            	// Allocate a new Label if the competition has changed
            	if (oldCompetition.getId() != competition.getId()) {
            		IEntityLabel label = null;

            		label = createLabel(competition, category,  registeredName);

            		if (label == null) {
            			throw new RegisteringException("Impossible de creer un dossard pour le triplet (competition/categorie/nom)" + competition + ", " + category + ", " + registeredName);
            		}
            		registered.setLabel(label);

            	}
        	}

          } else {
        	throw new RegisteringException("L'inscrit <" + id + "> n'existe pas !");
        }
        return registered;

    }

    /*
     * Import a registered
     */
    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IEntityRegistered importRegistered(
			final IEntityEvent event,
			final IEntityCompetition competition,
			final IEntityCategory category,
			final String name,
			final Date registeringDate,
			final String labelValue,
			final String rfid,
			final boolean isTeamed,
			final boolean isPaid,
			final boolean providedHealthForm,
			final List<IEntityPerson> persons,
			final String club,
			final String source,
			final Date arrivalDate,
			final long elapsedTime) throws AlreadyExistException, RegisteringException {

    	// Check if the registered already exists
    	IEntityRegistered registered = null;
		try {
			registered = findRegisteredFromLabel(labelValue);
		} catch (InvalidException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    	if (registered != null) {
        	throw new AlreadyExistException("L'inscrit <" + name + "> avec le dossard <" + labelValue + "> existe deja !" + registered);
    	}

    	if (competition == null) {
        	throw new RegisteringException("Competition inconnue pour le dossard : "  + labelValue);
    	}

    	// At first check if the persons are not registered yet
    	List<IEntityPerson> persistedPersonsList = new ArrayList<IEntityPerson>();
    	for (IEntityPerson person : persons) {
    		// Attempt to create the person
    		IEntityPerson pClone = null;
    		try {
    			pClone = createPerson(person);
    			logger.log(Level.FINE, "creation <" + person + ">");

    		} catch (AlreadyExistException e) {

    			// if exists, update it
        		pClone = findPersonFromLastnameFirstNameBirthDay(person.getLastname(), person.getFirstname(), person.getBirthday());
    			IEntityRegistered rClone = findRegisteredFromPersonForDefaultEvent(pClone.getId());
    			if ( rClone != null) {
            			throw new AlreadyExistException("L'inscrit <" + name + "> existe deja !" + rClone);
    			}

    			logger.log(Level.WARNING, "La personne <" + person + "> existe deja -> update");

        		pClone.setLastname(person.getLastname());
        		pClone.setFirstname(person.getFirstname());
        		pClone.setBirthday(person.getBirthday());
        		pClone.setSex(person.getSex());
        		pClone.setEmail(person.getEmail());
        		pClone.setClub(person.getClub());
        		pClone.setLicense(person.getLicense());
        		updatePerson(pClone);
    		}
    		persistedPersonsList.add(pClone) ;

    	}

    	// reach this point means that none person is already registered !
    	registered = new Registered();
		registered.setEvent(event);
		registered.setCompetition(competition);
		registered.setCategory(category);
		registered.setName(name);
		registered.setRegisteringDate(registeringDate);
		registered.setPersons(new HashSet(persistedPersonsList));
		registered.setLabel(createLabel(competition, labelValue, rfid));
		registered.setIsPaid(isPaid);
		registered.setIsTeamed(isTeamed);
		registered.setProvidedHealthForm(providedHealthForm);
		if (club != null) {
			registered.setClub(club);
		}

		registered.setSource(source);

		if (arrivalDate != null) {
			registered.setArrivalDate(arrivalDate);
		}

		if (elapsedTime != 0) {
			registered.setElapsedTime(elapsedTime);
		}

		this.entityManager.merge(registered);
		logger.log(Level.FINE, "creation <" + registered + ">");

        return registered;

    }
}
