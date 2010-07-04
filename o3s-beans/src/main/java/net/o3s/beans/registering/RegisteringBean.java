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
package net.o3s.beans.registering;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.o3s.apis.AlreadyExistException;
import net.o3s.apis.IEJBAdminLocal;
import net.o3s.apis.IEJBRegisteringLocal;
import net.o3s.apis.IEJBRegisteringRemote;
import net.o3s.apis.IEntityCategory;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityEvent;
import net.o3s.apis.IEntityLabel;
import net.o3s.apis.IEntityPerson;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.RegisteringException;
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
     * Check competition against category
     * @param competition
     * @param category
     * @throws RegisteringException
     */
    private void checkCompetitionCategory(IEntityCompetition competition, IEntityCategory category) throws RegisteringException {

    	boolean found = false;
    	Set<IEntityCompetition> competitionsInCategory = category.getCompetitions();
    	for (IEntityCompetition competitionInCategory : competitionsInCategory) {
    		if (competitionInCategory.equals(competition)) {
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

    	int labelNumber = competition.getLastLabelNumber() + 1;
    	if (labelNumber >= competition.getHigherLabelNumber()) {
    		throw new RegisteringException("Debordement de numero de dossard");
    	}
    	competition.setLastLabelNumber(labelNumber);

    	// Label=<number><cat_sh><name>
    	//         5c       1c      2c
    	String labelValue = padLeft(String.valueOf(labelNumber),5) + category.getShortName() + name.substring(0, 2);
    	labelValue = labelValue.trim();

    	label = findLabelFromNumber(labelNumber);
        if (label == null) {
        	label = new Label();
        	label.setNumber(labelNumber);
        	label.setValue(labelValue);
        	this.entityManager.persist(label);
    	} else {
    		throw new RegisteringException("Le dossard existe deja pour cette etiquette [" + labelNumber + "]:" + label);
    	}

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
        return person;
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
    public IEntityRegistered findRegisteredFromLabel(final String labelValue) throws RegisteringException {
        Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_LABEL");
        query.setParameter("VALUE", labelValue);

        IEntityRegistered registered = null;
        try {

        	registered = (IEntityRegistered) query.getSingleResult();
        } catch (javax.persistence.NoResultException nre) {

        } catch (NonUniqueResultException nure) {
        	nure.printStackTrace();
        	throw new RegisteringException("Impossible de trouver ce dossard [" + labelValue + "]", nure);
        } catch (Exception e){
        	e.printStackTrace();
        	throw new RegisteringException("Impossible de trouver ce dossard [" + labelValue + "]", e);

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
    	this.entityManager.remove(registered);
    }

    /**
     * Get registered on a competition order by duration
     */
    @SuppressWarnings("unchecked")
    public List<IEntityRegistered> findRegisteredFromCompetitionOrderByDuration(final int competitionId) throws RegisteringException {

    	IEntityEvent event = admin.findDefaultEvent();

        Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_COMPETITION_ORDERBY_ETIME");
        query.setParameter("COMPETITION", competitionId);
        query.setParameter("EVENTID", event.getId());

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
    	IEntityEvent event = admin.findDefaultEvent();

    	Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_COMPETITION_ORDERBY_CATEGORY_ETIME");
        query.setParameter("COMPETITION", competitionId);
        query.setParameter("EVENTID", event.getId());

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
    	IEntityEvent event = admin.findDefaultEvent();

    	Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_COMPETITION_ORDERBY_CLUB_ETIME");
        query.setParameter("COMPETITION", competitionId);
        query.setParameter("EVENTID", event.getId());

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
    	IEntityEvent event = admin.findDefaultEvent();

    	Query query = this.entityManager.createNamedQuery("REGISTERED_FROM_COMPETITION_ORDERBY_CATEGORY_ETIME");
        query.setParameter("COMPETITION", competitionId);
        query.setParameter("EVENTID", event.getId());

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
           		registeredName = person.getFirstname() + " " + person.getLastname();
           	} else {
           		if (name == null || name.equals("") == true ) {
                	throw new RegisteringException("Ce nom n'est pas valide !");
           		}
           		registeredName = name;
           	}

    		if (findRegisteredFromName(registeredName) != null) {
            	throw new AlreadyExistException("L'inscrit <" + registeredName + "> existe deja !");
    		}

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
    	}

        return registeredsReturn;
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
               		registeredName = person.getFirstname() + " " + person.getLastname();

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
}
