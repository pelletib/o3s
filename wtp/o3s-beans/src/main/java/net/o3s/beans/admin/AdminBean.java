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
package net.o3s.beans.admin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.o3s.apis.AdminException;
import net.o3s.apis.AlreadyExistException;
import net.o3s.apis.IEJBAdminLocal;
import net.o3s.apis.IEJBAdminRemote;
import net.o3s.apis.IEJBNotificationProducerLocal;
import net.o3s.apis.IEJBRegisteringLocal;
import net.o3s.apis.IEntityCategory;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityEvent;
import net.o3s.apis.IEntityLabel;
import net.o3s.apis.IEntityPerson;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.NotificationMessageException;
import net.o3s.apis.RegisteringException;
import net.o3s.apis.TestData;
import net.o3s.persistence.Category;
import net.o3s.persistence.Competition;
import net.o3s.persistence.Event;
import net.o3s.persistence.Person;
import net.o3s.persistence.Registered;



/**
 * Session Bean implementation class AdminBean
 */
@Stateless
@Local(IEJBAdminLocal.class)
@Remote(IEJBAdminRemote.class)
public class AdminBean implements IEJBAdminLocal,IEJBAdminRemote {

	private static final String PREFIX_EXPORT_FILENAME = "o3s_export_registered";

	private static final String PREFIX_EXPORT_ADMIN_FILENAME = "o3s_export_admin";
	private static final String PREFIX_EXPORT_EVENT_FILENAME = "event";
	private static final String PREFIX_EXPORT_CATEGORY_FILENAME = "category";
	private static final String PREFIX_EXPORT_COMPETITION_FILENAME = "competition";

	private static final String EVENT_LOGO = "logo_cross_crossey.png";

	/**
	 * Logger
	 */
    private static Logger logger = Logger.getLogger(AdminBean.class.getName());

    /**
     * Persistent manager
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Notification EJB
     */
    @EJB
    private IEJBNotificationProducerLocal notification;

    /**
     * Registering EJB
     */
    @EJB
    private IEJBRegisteringLocal registering;


    /**
     * Read an image file and returns it as a byte array
     * @param file
     * @return
     * @throws IOException
     */
    private byte[] readImage(String fileName) throws IOException {
      logger.fine("[Open File] " + fileName);
      File file = null;
      InputStream is = null;
      int length = 0;
      // first try with the absolute path
      try {
    	  file = new File(fileName);
    	  if (! file.exists()) {
    		  // second try through the classloader
    		  ClassLoader cl = Thread.currentThread().getContextClassLoader();
    		  is = cl.getResourceAsStream(fileName);
    		  length = (int) 100 * 1024;

    	  } else {
    		  length = (int) file.length();
        	  is = new FileInputStream(file);
    	  }

      } catch (Exception e) {
    	  throw new IOException(e.getMessage());
      }

      // Create the byte array to hold the data
      byte[] bytes = new byte[length];

      // Read in the bytes
      int offset = 0;
      int numRead = 0;
      while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
        offset += numRead;
      }
      // Ensure all the bytes have been read in
      if (offset >= bytes.length) {
        throw new IOException("Could not completely read file " + fileName);
      }
      // Close the input stream and return bytes
      is.close();
      return bytes;
    }

    /**
     * get event from id
     * @param id event id
     * @return IEntityEvent
     */
    public IEntityEvent findEventFromId(final int id) {
        IEntityEvent event = null;
        try {
        	event = (IEntityEvent) this.entityManager.find(Event.class, id);
        } catch (javax.persistence.NoResultException e) {
        }
        return event;
    }

    public IEntityEvent findEventFromName(final String name) {
        Query query = this.entityManager.createNamedQuery("EVENT_FROM_NAME");
        query.setParameter("NAME", name);
        IEntityEvent event = null;
        try {

        	event = (IEntityEvent) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        }

        return event;
    }

    @SuppressWarnings("unchecked")
    public IEntityEvent findLastEvent() {
        Query query = this.entityManager.createNamedQuery("ALL_EVENTS");
        List<Event> results = null;
        try {
        	results = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	return null;
        }
        if (results.size() == 0) {
        	return null;
        }
        return (IEntityEvent) results.get(results.size()-1);


    }

    @SuppressWarnings("unchecked")
    public IEntityEvent findDefaultEvent() {
        Query query = this.entityManager.createNamedQuery("DEFAULT_EVENT");
        IEntityEvent event = null;
        try {
        	event = (IEntityEvent) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        	// if not event are defined as default, return the last one
        	return findLastEvent();
        }

        return event;
    }

    public IEntityEvent createEvent(
    		final String name,
    		final Date date,
    		final String fileName) {
    	return createEvent(name, date, fileName, false);
    }

    public IEntityEvent updateEvent(
    		final int id,
    		final String name,
    		final Date date) throws AdminException {
    	IEntityEvent event = null;

    	event = findEventFromId(id);
        if (event != null) {
        	event.setName(name);
        	event.setDate(date);
        } else {
        	throw new AdminException("L'evenement <" + id + "> n'existe pas !");
        }
        return event;
    }

    private IEntityEvent createEvent(
    		final String name,
    		final Date date,
    		final String fileName,
    		boolean force) {

            if (fileName != null) {
        		byte[] image = null;
            	try {
            		image = readImage(fileName);
				} catch (IOException e) {
					logger.log(Level.SEVERE, null, e);
				}
				return createEventWithBinImage(name,date,image, force);
            } else {
            	return createEventWithBinImage(name,date,null, force);
            }
    }

    private IEntityEvent createEventWithBinImage(
    		final String name,
    		final Date date,
    		final byte[] imageFile,
    		boolean force) {
    	IEntityEvent event = null;

    	event = findEventFromName(name);
    	if (event == null || force) {
        	logger.fine("Create new event : " + name);

        	event = new Event();
            event.setName(name);
            event.setDate(date);

            List <IEntityEvent> eventsList = findAllEvents();

            // if there's only one event, set it as default
            if (eventsList.size() == 0) {
            	event.setTheDefault(true);
            } else {
            	event.setTheDefault(false);
            }

            if (imageFile != null) {
            	// Lets open an image file
            	event.setImageFile(imageFile);
            }

            this.entityManager.persist(event);
        }
        return event;
    }

    /**
     * Remove an event
     */
    public void removeEvent(final int id) throws AdminException {
    	doRemoveEvent(id, false);
    }

    /**
     * Remove an event
     * force will remove even the default one
     */
    private void doRemoveEvent(final int id, final boolean force) throws AdminException {

    	IEntityEvent event = findEventFromId(id);
    	List<IEntityRegistered> registereds = null;

    	if (event == null) {
    		throw new AdminException("event is null");
    	}
    	// no delete if it's the default event
    	if (event.isTheDefault() && !force) {
    		throw new AdminException("Unable to remove the default event");

    	}
    	// no delete if there's still some registered
    	registereds = this.registering.findAllRegisteredFromEvent(id);
       	if (registereds.size() > 0) {
    		throw new AdminException("Unable to remove event with existing registered (" + registereds.size() + ")");
    	}

       	// remove categories related to the event
    	List<IEntityCategory> categories = findAllCategoriesFromEvent(id);
    	for (IEntityCategory category:categories) {
        	this.entityManager.remove(category);
    	}

      	// remove competitions related to the event
    	List<IEntityCompetition> competitions = findAllCompetitionsFromEvent(id);
    	for (IEntityCompetition competition:competitions) {
        	this.entityManager.remove(competition);
    	}

    	// finally remove the event
    	this.entityManager.remove(event);
    }

    /**
     * Remove a competition
     */
    public void removeCompetition(final int id) throws AdminException {

    	IEntityCompetition competition = findCompetitionFromId(id);
    	IEntityEvent event = findEventFromId(competition.getEvent().getId());

    	if (competition == null) {
    		throw new AdminException("competition is null");
    	}

    	// no delete if there's still some registered
    	List<IEntityRegistered> registereds = null;
    	registereds = this.registering.findAllRegisteredFromEvent(event.getId());
    	boolean found = false;
    	for (IEntityRegistered registered:registereds) {
    		if (registered.getCompetition().getId() == id) {
    			found = true;
    			break;
    		}
    	}
       	if (found) {
    		throw new AdminException("Unable to remove competition with existing registered");
    	}

    	// no delete if there's still some categories
    	List<IEntityCategory> categories = null;
    	categories = findAllCategoriesFromEvent(event.getId());
    	found = false;
    	for (IEntityCategory category:categories) {
    		for (IEntityCompetition c:category.getCompetitions())
    			if (c.getId() == id) {
    			found = true;
    			break;
    		}
    	}
       	if (found) {
    		throw new AdminException("Unable to remove competition with existing categories");
    	}

    	this.entityManager.remove(competition);

    	//TODO : remove categories, competitions
    }

    /**
     * Remove a category
     */
    public void removeCategory(final int id) throws AdminException {

    	IEntityCategory category = findCategoryFromId(id);
    	IEntityEvent event = findEventFromId(category.getEvent().getId());

    	if (category == null) {
    		throw new AdminException("category is null");
    	}

    	// no delete if there's still some registered
    	List<IEntityRegistered> registereds = null;
    	registereds = this.registering.findAllRegisteredFromEvent(event.getId());
    	boolean found = false;
    	for (IEntityRegistered registered:registereds) {
    		if (registered.getCategory() != null) {
        		if (registered.getCategory().getId() == id) {
        			found = true;
        			break;
        		}
    		}
    	}
       	if (found) {
    		throw new AdminException("Unable to remove category with existing registered");
    	}


    	this.entityManager.remove(category);

    	//TODO : remove categories, competitions
    }

    public void setDefaultEvent(final int id) {

    	IEntityEvent oldDefaultEvent = findDefaultEvent();
    	IEntityEvent newDefaultEvent = findEventFromId(id);

    	if (oldDefaultEvent.getId() == newDefaultEvent.getId()) {
        	logger.log(Level.WARNING, "Event <" + newDefaultEvent + "> already set as default. Do nothing");
    		newDefaultEvent.setTheDefault(true);
        	return;
    	}
    	if (newDefaultEvent != null) {
    		newDefaultEvent.setTheDefault(true);
        	oldDefaultEvent.setTheDefault(false);
    	}
    }

    private IEntityCompetition findCompetitionFromName(final String name) {
    	IEntityEvent event = findDefaultEvent();
    	Query query = this.entityManager.createNamedQuery("COMPETITION_FROM_NAME_AND_EVENT");
        query.setParameter("NAME", name);
        query.setParameter("EVENTID", event.getId());
        IEntityCompetition competition = null;
        try {
        	competition = (IEntityCompetition) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        }
        return competition;
    }

    public IEntityCompetition findCompetitionFromName(final String name, IEntityEvent event) {
    	Query query = this.entityManager.createNamedQuery("COMPETITION_FROM_NAME_AND_EVENT");
        query.setParameter("NAME", name);
        query.setParameter("EVENTID", event.getId());
        IEntityCompetition competition = null;
        try {
        	competition = (IEntityCompetition) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        }
        return competition;
    }

    @SuppressWarnings("unchecked")
    public  List<IEntityCompetition> findAllCompetitions() {
        Query query = this.entityManager.createNamedQuery("ALL_COMPETITIONS");

        List<IEntityCompetition> competitions = null;
        try {
        	competitions = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	competitions = new ArrayList<IEntityCompetition>();
        }
        return competitions;
    }

    @SuppressWarnings("unchecked")
    public  List<IEntityCompetition> findAllCompetitionsFromDefaultEvent() {
        IEntityEvent event = findDefaultEvent();
        if (event != null) {
        	return findAllCompetitionsFromEvent(event.getId());
        } else {
        	return new ArrayList<IEntityCompetition>();
        }
    }

    @SuppressWarnings("unchecked")
    public  List<IEntityCompetition> findAllCompetitionsFromEvent(final int eventId) {
        Query query = this.entityManager.createNamedQuery("ALL_COMPETITIONS_FROM_EVENT");

        List<IEntityCompetition> competitions = null;
        try {
            query.setParameter("EVENTID", eventId);
            competitions = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	competitions = new ArrayList<IEntityCompetition>();
        } catch (Exception e1) {
        	logger.log(Level.WARNING, "error", e1);
        	competitions = new ArrayList<IEntityCompetition>();
        }
        return competitions;
    }

    @SuppressWarnings("unchecked")
    public  List<IEntityEvent> findAllEvents() {
        Query query = this.entityManager.createNamedQuery("ALL_EVENTS");

        List<IEntityEvent> events = null;
        try {
        	events = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	events = new ArrayList<IEntityEvent>();
        }
        return events;
    }

    public IEntityCompetition findCompetitionFromId(final int id) {
        IEntityCompetition competition = null;
        try {
        	competition = (IEntityCompetition) this.entityManager.find(Competition.class, id);
        } catch (javax.persistence.NoResultException e) {
        }
        return competition;
    }


    public IEntityCompetition createCompetition(
    		final String name,
    		final int lowerLabelNumber,
    		final int higherLabelNumber,
    		final int lastLabelNumber,
    		final int eventId,
    		final boolean isTeamed) {
    	return createCompetition(name, lowerLabelNumber, higherLabelNumber, lastLabelNumber, eventId, isTeamed, false);
    }

    private IEntityCompetition createCompetition(
    		final String name,
    		final int lowerLabelNumber,
    		final int higherLabelNumber,
    		final int lastLabelNumber,
    		final int eventId,
    		final boolean isTeamed,
    		final boolean force) {
    	IEntityCompetition competition = null;
    	competition = findCompetitionFromName(name);
    	IEntityEvent event = findEventFromId(eventId);
    	if (event == null) {
        	logger.log(Level.SEVERE, "Unknown event, set default one");
    		event = findDefaultEvent();
    	}
        if (competition == null || force) {
        	logger.fine("Create new competition : " + name);
            competition = new Competition();
            competition.setName(name);
            competition.setLowerLabelNumber(lowerLabelNumber);
            competition.setHigherLabelNumber(higherLabelNumber);
            competition.setLastLabelNumber(lastLabelNumber);
            competition.setEvent(event);
            competition.setTeamed(isTeamed);
            this.entityManager.persist(competition);
        }
        return competition;
    }

    public IEntityCompetition updateCompetition(
    		final int id,
    		final String name,
    		final int firstLabelNumber,
    		final int lastLabelNumber,
    		final int lowerLabelNumber,
    		final Date startingDate,
    		final boolean teamed) throws AdminException {
    	IEntityCompetition competition = null;

    	competition = findCompetitionFromId(id);
        if (competition != null) {
        	competition.setName(name);
        	competition.setHigherLabelNumber(firstLabelNumber);
        	competition.setLastLabelNumber(lastLabelNumber);
        	competition.setLowerLabelNumber(lowerLabelNumber);
        	competition.setStartingDate(startingDate);
        	competition.setTeamed(teamed);
        } else {
        	throw new AdminException("La competition <" + id + "> n'existe pas !");
        }
        return competition;
    }


    public Date setStartDateInCompetition(final int id) throws AdminException {
    	return setStartDateInCompetition(id, new Date());
    }

    public Date setStartDateInCompetition(final int id, final Date date) throws AdminException {
    	IEntityCompetition competition = findCompetitionFromId(id);
    	if (competition == null) {
    		throw new AdminException("Unable to find Competition (id=" + id + ")");

    	} else {
    		competition.setStartingDate(date);

    		try {
    			notification.sendDepartureNotification(competition);
    		} catch (NotificationMessageException e) {
    			logger.log(Level.SEVERE, "Unable to send a notification :" + e.getMessage());
    		}

    		// recompute elapsed time for related registereds
    		try {
				registering.recomputeElapsedTimeRegistereds(id);
			} catch (RegisteringException e) {
    			logger.log(Level.SEVERE, "Unable to recompute elapsed time :" + e.getMessage());
			}
    	}
    	return date;
    }

    @SuppressWarnings("unchecked")
    public List<IEntityCategory> findCategoryFromDatesAndSex(final Date date, final char sex) {
    	IEntityEvent event = findDefaultEvent();
    	Query query = this.entityManager.createNamedQuery("CATEGORY_FROM_DATES_AND_SEX_AND_EVENT");
    	query.setParameter("DATE", date);
    	query.setParameter("SEX", sex);
        query.setParameter("EVENTID", event.getId());

    	 List<IEntityCategory> categories = null;
    	 try {
    		 categories = query.getResultList();
         } catch (javax.persistence.NoResultException e) {
         	categories = new ArrayList<IEntityCategory>();

         }
         return categories;

    }

    @SuppressWarnings("unchecked")
    public IEntityCategory findCategoryFromName(final String name, final IEntityEvent event) {
    	Query query = this.entityManager.createNamedQuery("CATEGORY_FROM_NAME_AND_EVENT");
        query.setParameter("NAME", name);
        query.setParameter("EVENTID", event.getId());
        IEntityCategory category = null;
        try {
        	category = (IEntityCategory) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        }
        return category;
    }

    public IEntityCategory findCategoryFromId(final int id) {
        IEntityCategory category = null;
        try {
        	category = (IEntityCategory) this.entityManager.find(Category.class, id);
        } catch (javax.persistence.NoResultException e) {
        }
        return category;
    }


    public IEntityCategory findNoCategory() {
    	IEntityCategory category = null;
    	IEntityEvent event = findDefaultEvent();

        try {
    		Query query = this.entityManager.createNamedQuery("NOCATEGORY");
            query.setParameter("EVENTID", event.getId());
            category = (IEntityCategory) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        }
        return category;

    }

    public IEntityCategory findCategoryFromNameAndSex(final String name, final char sex) {
    	IEntityCategory category = null;
    	IEntityEvent event = findDefaultEvent();

        try {
    		Query query = this.entityManager.createNamedQuery("CATEGORY_FROM_NAME_AND_EVENT");
    		query.setParameter("NAME", name + " (" + sex + ")");
            query.setParameter("EVENTID", event.getId());
            category = (IEntityCategory) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        	Query query = this.entityManager.createNamedQuery("CATEGORY_FROM_NAME_AND_SEX_AND_EVENT");
    		query.setParameter("NAME", name);
    		query.setParameter("SEX", sex);
            query.setParameter("EVENTID", event.getId());
    		try {
    		category =  (IEntityCategory) query.getSingleResult();
    		} catch (javax.persistence.NoResultException re) {
    		}
        }
        return category;


    }

    @SuppressWarnings("unchecked")
    public List<IEntityCategory> findAllCategories() {
        Query query = this.entityManager.createNamedQuery("ALL_CATEGORIES");

        List<IEntityCategory> categories = null;
        try {
        	categories = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	categories = new ArrayList<IEntityCategory>();
        }

        return categories;

    }

    @SuppressWarnings("unchecked")
    public  List<IEntityCategory> findAllCategoriesFromDefaultEvent() {
        IEntityEvent event = findDefaultEvent();
        if (event != null) {
        	return findAllCategoriesFromEvent(event.getId());
        } else {
        	return new ArrayList<IEntityCategory>();
        }
    }

    @SuppressWarnings("unchecked")
    public  List<IEntityCategory> findAllCategoriesFromEvent(final int eventId) {
        Query query = this.entityManager.createNamedQuery("ALL_CATEGORIES_FROM_EVENT");
        query.setParameter("EVENTID", eventId);

        List<IEntityCategory> categories = null;
        try {
        	categories = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	categories = new ArrayList<IEntityCategory>();
        }
        return categories;
    }

    @SuppressWarnings("unchecked")
	public IEntityCategory createCategory(final String name, final Date minDate, final Date maxDate, final char sex, final char shortName, final int eventId, final IEntityCompetition... competitions) {
    	return createCategory( false, name, minDate, maxDate, sex, shortName, eventId, competitions);
    }

    @SuppressWarnings("unchecked")
	private IEntityCategory createCategory(
			final boolean force,
			final String name,
			final Date minDate,
			final Date maxDate,
			final char sex,
			final char shortName,
			final int eventId,
			final IEntityCompetition... competitions) {
    	IEntityCategory category = null;
    	category = findCategoryFromNameAndSex(name, sex);

    	IEntityEvent event = findEventFromId(eventId);
    	if (event == null) {
        	logger.log(Level.SEVERE, "Unknown event, set default one");
    		event = findDefaultEvent();
    	}

        if (category == null || force) {
        	logger.fine("Create new category : " + name);

        	category = new Category();

        	// if force, the same name exist, so don't add the sex attribute
        	if (force) {
        		category.setName(name);
        	} else {
        		category.setName(name + " (" + sex + ")");
        	}
        	if (minDate == null) {
        		category.setMinDate(new Date());
        	} else {
        		category.setMinDate(minDate);
        	}
        	if (maxDate == null) {
        		category.setMaxDate(new Date());
        	} else {
            	category.setMaxDate(maxDate);
        	}
        	category.setSex(sex);
        	if (shortName == 0x0) {
        		category.setShortName('?');
        	} else {
        		category.setShortName(shortName);
        	}
        	category.setEvent(event);

        	if (competitions == null) {
        		// choose a competition randomly
        		IEntityCompetition comp = null;
        		int i = 0;
        		while (comp == null & i < 99999) {
            		comp = findCompetitionFromId(i);
        			i++;
        		}
        		List<IEntityCompetition> comps = new ArrayList<IEntityCompetition>();
        		comps.add(comp);
        		category.setCompetitions (new HashSet(Arrays.asList(comps)));
        	} else {
        		category.setCompetitions (new HashSet(Arrays.asList(competitions)));
        	}
            this.entityManager.persist(category);
        }

        return category;
    }

    @SuppressWarnings("unchecked")
	private IEntityCategory doCreateCategory(
			final String name,
			final Date minDate,
			final Date maxDate,
			final char sex,
			final char shortName,
			final int eventId,
			final List<IEntityCompetition> competitions) {
    	IEntityCategory category = null;
    	category = findCategoryFromNameAndSex(name, sex);

    	IEntityEvent event = findEventFromId(eventId);
    	if (event == null) {
        	logger.log(Level.SEVERE, "Unknown event, set default one");
    		event = findDefaultEvent();
    	}

        if (category == null) {
        	logger.fine("Create new category : " + name);

        	category = new Category();

        	// if force, the same name exist, so don't add the sex attribute
        	category.setName(name);

        	if (minDate == null) {
        		category.setMinDate(new Date());
        	} else {
        		category.setMinDate(minDate);
        	}
        	if (maxDate == null) {
        		category.setMaxDate(new Date());
        	} else {
            	category.setMaxDate(maxDate);
        	}
        	category.setSex(sex);
        	if (shortName == 0x0) {
        		category.setShortName('?');
        	} else {
        		category.setShortName(shortName);
        	}
        	category.setEvent(event);
        	if (competitions == null) {
        		// choose a competition randomly
        		IEntityCompetition comp = null;
        		int i = 0;
        		while (comp == null & i < 99999) {
            		comp = findCompetitionFromId(i);
        			i++;
        		}
        		List<IEntityCompetition> comps = new ArrayList<IEntityCompetition>();
        		comps.add(comp);
        		category.setCompetitions (new HashSet(Arrays.asList(comps)));
        	} else {
        		category.setCompetitions (new HashSet((competitions)));
        	}
            this.entityManager.persist(category);
        }

        return category;
    }

    @SuppressWarnings("unchecked")
	public IEntityCategory updateCategory(
    		final int id,
    		final String name,
    		final char sex,
    		final char shortName,
    		final Date minDate,
    		final Date maxDate,
    		final List<IEntityCompetition> competitions) throws AdminException {
    	IEntityCategory category = null;

    	category = findCategoryFromId(id);
        if (category != null) {
        	category.setName(name);
        	category.setSex(sex);
        	category.setShortName(shortName);

        	if (minDate == null) {
            	category.setMinDate(new Date());
        	} else {
            	category.setMinDate(minDate);
        	}

           	if (maxDate == null) {
            	category.setMaxDate(new Date());
        	} else {
            	category.setMaxDate(maxDate);
        	}

           	if (competitions == null) {
        		// choose a competition randomly
        		IEntityCompetition comp = null;
        		int i = 0;
        		while (comp == null & i < 99999) {
            		comp = findCompetitionFromId(i);
        			i++;
        		}
        		List<IEntityCompetition> comps = new ArrayList<IEntityCompetition>();
        		comps.add(comp);
        		category.setCompetitions (new HashSet(comps));
        	} else {
        		List<IEntityCompetition> comps = new ArrayList<IEntityCompetition>();
        		for (IEntityCompetition comp:competitions) {
        			comps.add(findCompetitionFromId(comp.getId()));
        		}
        		category.setCompetitions (new HashSet(comps));
        	}

        } else {
        	throw new AdminException("La categorie <" + id + "> n'existe pas !");
        }
        return category;
    }

	public String exportRegisteredAsFileName(final Date from) {

		// get default event
    	IEntityEvent defaultEvent = findDefaultEvent();

    	List<IEntityRegistered> registereds = null;

    	if (from == null) {
			registereds = this.registering.findAllRegisteredFromDefaultEvent();
		} else {
			registereds = this.registering.findAllRegisteredFromDefaultEventWhereRegisteringDateIsGreaterThan(from);
		}


		// build file name
    	DateFormat dfyyyyMMdd = new SimpleDateFormat("yyyyMMddHHmmss");
    	String today = dfyyyyMMdd.format(new Date());
    	String fileName = PREFIX_EXPORT_FILENAME + "-" + defaultEvent.getName() + "-" + today + ".csv";
    	fileName = fileName.replace(' ', '_');

    	// Open the file
    	FileWriter writer = null;

		try {
			writer = new FileWriter(fileName);

			// Header
			writer.append("Event");
			writer.append(',');
			writer.append("Competition");
			writer.append(',');
			writer.append("Category");
			writer.append(',');
			writer.append("Name");
			writer.append(',');
			writer.append("RegisteringDate");
			writer.append(',');
			writer.append("Label");
			writer.append(',');
			writer.append("Rfid");
			writer.append(',');
			writer.append("Teamed");
			writer.append(',');
			writer.append("Paid");
			writer.append(',');
			writer.append("ProvidedHealthForm");
			writer.append(',');
			writer.append("Persons");
			writer.append(',');
			writer.append("Club");
			writer.append(',');
			writer.append("Source");
			writer.append(',');
			writer.append("ArrivalDate");
			writer.append(',');
			writer.append("ElapsedTime");
			writer.append('\n');

			// Browse data and export
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");

			for (IEntityRegistered registered : registereds) {

				// Name
				writer.append(registered.getEvent().getName());
				writer.append(',');

				// Competition
				writer.append(registered.getCompetition().getName());
				writer.append(',');

				// Category
				writer.append(registered.getCategory().getName());
				writer.append(',');

				// Name
				writer.append(registered.getName());
				writer.append(',');

				// RegisteringDate
				writer.append(df.format(registered.getRegisteringDate()));
				writer.append(',');

				// Label
				writer.append(registered.getLabel().getValue());
				writer.append(',');

				// Rfid
				writer.append(registered.getLabel().getRfid());
				writer.append(',');

				// isTeamed ?
				writer.append(registered.isTeamed().toString());
				writer.append(',');

				// isPaid ?
				writer.append(registered.isPaid().toString());
				writer.append(',');

				// isProvidedHealth ?
				writer.append(registered.isProvidedHealthForm().toString());
				writer.append(',');

				// Persons, format is
				// line separator : %
				// field separor : #
				// line : LASTNAME#FIRSTNAME#SEX#BIRTHDAY#CLUB#LICENSE#EMAIL
				for (IEntityPerson person : registered.getPersons()) {

					// Lastname
					writer.append(person.getLastname());
					writer.append('#');

					// Firstname
					writer.append(person.getFirstname());
					writer.append('#');

					// Sex
					writer.append(person.getSex());
					writer.append('#');

					// Birthday
					writer.append(df.format(person.getBirthday()));
					writer.append('#');

					// Club
					if (person.getClub() != null
							&& !person.getClub().equalsIgnoreCase("")) {
						writer.append(person.getClub());
					}
					writer.append('#');

					// License
					if (person.getLicense() != null
							&& !person.getLicense().equalsIgnoreCase("")) {
						writer.append(person.getLicense());
					}
					writer.append('#');

					// Email
					if (person.getEmail() != null
							&& !person.getEmail().equalsIgnoreCase("")) {
						writer.append(person.getEmail());
					}

					writer.append('%');

				}

				writer.append(',');

				// Club
				if (registered.getClub() != null
						&& !registered.getClub().equalsIgnoreCase("")) {
					writer.append(registered.getClub());
				}
				writer.append(',');

				// Source
				writer.append(registered.getSource());
				writer.append(',');

				// ArrivalDate
				if (registered.getArrivalDate() != null) {
					writer.append(df.format(registered.getArrivalDate()));
				}
				writer.append(',');

				// ElapsedTime
				if (registered.getElapsedTime() > 0) {
					writer.append(Long.toString(registered.getElapsedTime()));
				}
				writer.append('\n');

			}



		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			// Flush & Close the file
			try {
				writer.flush();
			    writer.close();
			} catch (Exception e) {

			}

		}

		return fileName;

	}

	/**
	 * Export the admin data in a zip file containing 3 CSV files (event, competition, category)
	 * @return the file name
	 */
	public String exportAdminAsZip() throws IOException {

		// creates a temp dir
    	DateFormat dfyyyyMMdd = new SimpleDateFormat("yyyyMMddHHmmss");
    	String today = dfyyyyMMdd.format(new Date());

    	// directory to build the file
    	final File sysTempDir = new File(System.getProperty("java.io.tmpdir"));
        File newTempDir = new File(sysTempDir, today);

        if(!newTempDir.mkdirs()) {

            throw new IOException(
                    "Failed to create temp dir named " +
                    newTempDir.getAbsolutePath());
        }


		// event
    	String eventFileName = newTempDir.getAbsolutePath() + File.separator +  PREFIX_EXPORT_EVENT_FILENAME + ".csv";
    	eventFileName = eventFileName.replace(' ', '_');
    	exportEventAsFileName(eventFileName);

		// competition
    	String competitionFileName = newTempDir.getAbsolutePath() + File.separator +  PREFIX_EXPORT_COMPETITION_FILENAME + ".csv";
    	competitionFileName = competitionFileName.replace(' ', '_');
    	exportCompetitionAsFileName(competitionFileName);

		// event
    	String categoryFileName = newTempDir.getAbsolutePath() + File.separator +  PREFIX_EXPORT_CATEGORY_FILENAME + ".csv";
    	categoryFileName = categoryFileName.replace(' ', '_');
    	exportCategoryAsFileName(categoryFileName);

		// zip the directory

    	String[] filenames = new String[]{eventFileName, competitionFileName, categoryFileName };
    	String[] zipentrynames = new String[]{PREFIX_EXPORT_EVENT_FILENAME + ".csv", PREFIX_EXPORT_COMPETITION_FILENAME + ".csv", PREFIX_EXPORT_CATEGORY_FILENAME + ".csv" };

    	// Create a buffer for reading the files
    	byte[] buf = new byte[1024];
   	    // Create the ZIP file
	    String outFilename = PREFIX_EXPORT_ADMIN_FILENAME + "-" + today + ".zip" ;

	    ZipOutputStream out = null;

    	try {

    	    out = new ZipOutputStream(new FileOutputStream(outFilename));

    	    // Compress the files
    	    for (int i=0; i<filenames.length; i++) {
    	        FileInputStream in = new FileInputStream(filenames[i]);

    	        // Add ZIP entry to output stream.
    	        out.putNextEntry(new ZipEntry(zipentrynames[i]));

    	        // Transfer bytes from the file to the ZIP file
    	        int len;
    	        while ((len = in.read(buf)) > 0) {
    	            out.write(buf, 0, len);
    	        }

    	        // Complete the entry
    	        out.closeEntry();
    	        in.close();
    	    }

    	    // Complete the ZIP file
    	    out.close();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

    	return outFilename;
	}

	/**
	 * Export the competitions list in a CSV file
	 * @param file name
	 * @return the file name
	 */
	private String exportCompetitionAsFileName(String fileName) {
		// get all events
    	List<IEntityCompetition> competitions = findAllCompetitions();

    	// Open the file
    	FileWriter writer = null;

		try {
			writer = new FileWriter(fileName);

			// Header
			writer.append("Name");
			writer.append(',');
			writer.append("LowerLabelNumber");
			writer.append(',');
			writer.append("HigherLabelNumber");
			writer.append(',');
			writer.append("LastLabelNumber");
			writer.append(',');
			writer.append("Teamed");
			writer.append(',');
			writer.append("StartingDate");
			writer.append(',');
			writer.append("Event");
			writer.append('\n');

			// Browse data and export
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");

			for (IEntityCompetition competition : competitions) {

				// Name
				writer.append(competition.getName());
				writer.append(',');

				// LowerLabelNumber
				writer.append(Integer.toString(competition.getLowerLabelNumber()));
				writer.append(',');

				// HigherLabelNumber
				writer.append(Integer.toString(competition.getHigherLabelNumber()));
				writer.append(',');

				// LastLabelNumber
				writer.append(Integer.toString(competition.getLastLabelNumber()));
				writer.append(',');

				// Teamed
				writer.append(competition.isTeamed().toString());
				writer.append(',');

				// StartingDate
				if (competition.getStartingDate() != null) {
					writer.append(df.format(competition.getStartingDate()));
				}
				writer.append(',');

				// Event
				writer.append(competition.getEvent().getName());

				writer.append('\n');

			}



		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			// Flush & Close the file
			try {
				writer.flush();
			    writer.close();
			} catch (Exception e) {

			}

		}

		return fileName;

	}

	/**
	 * Export the categories list in a CSV file
	 * @param file name
	 * @return the file name
	 */
	private String exportCategoryAsFileName(String fileName) {

		// get all events
    	List<IEntityCategory> categories = findAllCategories();

    	// Open the file
    	FileWriter writer = null;

		try {
			writer = new FileWriter(fileName);

			// Header
			writer.append("Name");
			writer.append(',');
			writer.append("Sex");
			writer.append(',');
			writer.append("MinDate");
			writer.append(',');
			writer.append("MaxDate");
			writer.append(',');
			writer.append("ShortName");
			writer.append(',');
			writer.append("Competitions");
			writer.append(',');
			writer.append("Event");
			writer.append('\n');

			// Browse data and export
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

			for (IEntityCategory category : categories) {

				// Name
				writer.append(category.getName());
				writer.append(',');

				// Sex
				writer.append(category.getSex());
				writer.append(',');

				// MinDate
				writer.append(df.format(category.getMinDate()));
				writer.append(',');

				// MaxDate
				writer.append(df.format(category.getMaxDate()));
				writer.append(',');

				// ShortName
				writer.append(category.getShortName());
				writer.append(',');

				// Competitions, format is
				// line separator : %
				// field separor : #
				// line : NAME
				for (IEntityCompetition competition : category.getCompetitions()) {

					// name
					writer.append(competition.getName());
					writer.append('%');

				}

				writer.append(',');
				// Event
				writer.append(category.getEvent().getName());

				writer.append('\n');

			}



		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			// Flush & Close the file
			try {
				writer.flush();
			    writer.close();
			} catch (Exception e) {

			}

		}

		return fileName;
	}

	/**
	 * Export the events list in a CSV file
	 * @param file name
	 * @return the file name
	 */
	private String exportEventAsFileName(String fileName) {

		// get all events
    	List<IEntityEvent> events = findAllEvents();

    	// Open the file
    	FileWriter writer = null;

		try {
			writer = new FileWriter(fileName);

			// Header
			writer.append("Name");
			writer.append(',');
			writer.append("Date");
			writer.append(',');
			writer.append("Default");
			writer.append('\n');

			// Browse data and export
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

			for (IEntityEvent event : events) {

				// Name
				writer.append(event.getName());
				writer.append(',');

				// Date
				writer.append(df.format(event.getDate()));
				writer.append(',');

				// Default
				writer.append(Boolean.toString(event.isTheDefault()));

				writer.append('\n');

			}



		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			// Flush & Close the file
			try {
				writer.flush();
			    writer.close();
			} catch (Exception e) {

			}

		}

		return fileName;

	}

	/**
	 * Import the admin data from a zip file containing 3 CSV files (event, competition, category)
	 * @return the total number of imported lines
	 */
	public int importAdminFromZip(String zipFileName) throws IOException, AdminException {

	    // check if the file exists
        if(!zipFileName.endsWith(".zip")){
        	logger.severe("Invalid file name : " + zipFileName);
        	throw new AdminException("Invalid file name : " + zipFileName);
        }

        if(!new File(zipFileName).exists()){
        	logger.severe("The file doesn't exist : " + zipFileName);
        	throw new AdminException("The file doesn't exist : " + zipFileName);
        }

		// creates a temp dir to expand the zip
    	String basename = zipFileName.substring(zipFileName.lastIndexOf(File.separator) + 1,  zipFileName.indexOf(".zip"));

    	final File sysTempDir = new File(System.getProperty("java.io.tmpdir"));
        File newTempDir = new File(sysTempDir, basename);


        if(!newTempDir.exists() && !newTempDir.mkdirs()) {

            throw new IOException(
                    "Failed to create temp dir named " +
                    newTempDir.getAbsolutePath());
        }

        // unzip IT
        ZipFile zipFile = new ZipFile(zipFileName);
        Enumeration enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
            System.out.println("Unzipping: " + zipEntry.getName());
            BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
            int size;
            byte[] buffer = new byte[2048];
            BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(newTempDir.getAbsolutePath() + File.separator + zipEntry.getName()), buffer.length);
            while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
              bos.write(buffer, 0, size);
            }
            bos.flush();
            bos.close();
            bis.close();
          }

        // import event
        int nbEvents= importEvent(newTempDir.getAbsolutePath() + File.separator + AdminBean.PREFIX_EXPORT_EVENT_FILENAME + ".csv");

        // import competitions
        int nbCompetitions= importCompetition(newTempDir.getAbsolutePath() + File.separator + AdminBean.PREFIX_EXPORT_COMPETITION_FILENAME + ".csv");

        // import categories
        int nbCategories= importCategory(newTempDir.getAbsolutePath() + File.separator + AdminBean.PREFIX_EXPORT_CATEGORY_FILENAME + ".csv");

        return nbEvents + nbCompetitions + nbCategories;

	}

	//@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int importRegistered(String fileName) {

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
		String line = null;
		int row = 0;
		int error = 0;
		int exists = 0;

		try {

			// Get the file
			BufferedReader bufRdr = new BufferedReader(new FileReader(fileName));

			// read each line of text file
			while ((line = bufRdr.readLine()) != null) {

				logger.fine("Line=" + line + "\n");

				// ignore first line (headers)
				if (row == 0) {
					row++;
					continue;
				}

				String[] fields = line.split(",", 15);
				int fieldIndex = 0;

				// Get Event
				logger.fine("  Event=" + fields[fieldIndex] + "\n");
				IEntityEvent event = findEventFromName(fields[fieldIndex]);
				fieldIndex++;

				// Get Competition
				logger.fine("  Competition=" + fields[fieldIndex]);
				IEntityCompetition competition = findCompetitionFromName(
						fields[fieldIndex], event);
				fieldIndex++;

				// Get Category
				logger.fine("  Category=" + fields[fieldIndex] + "\n");
				IEntityCategory category = findCategoryFromName(fields[fieldIndex],
						event);
				fieldIndex++;

				// Name
				logger.fine("  Name=" + fields[fieldIndex] + "\n");
				String name = fields[fieldIndex];
				fieldIndex++;

				// RegisteringDate
				logger.fine("  RegisteringDate=" + fields[fieldIndex] + "\n");
				Date registeringDate;
				try {
					registeringDate = df.parse(fields[fieldIndex]);
				} catch (ParseException e1) {
					logger.severe(e1.getMessage());
					continue;
				}
				fieldIndex++;

				// Label
				logger.fine("  labelValue=" + fields[fieldIndex] + "\n");
				String labelValue = fields[fieldIndex];
				fieldIndex++;

				String rfid = "";
				if (fields.length >= 15) { // to be compliant with version wihtout rfid
					// Rfid
					logger.fine("  rfid=" + fields[fieldIndex] + "\n");
					rfid = fields[fieldIndex];
					if (! registering.isValidRfid(rfid)) {
						logger.fine("  invalid rfid !" + "\n");
						rfid = "";
					}
					fieldIndex++;
				}

				// isTeamed
				logger.fine("  isTeamed=" + fields[fieldIndex] + "\n");
				boolean isTeamed = Boolean.parseBoolean(fields[fieldIndex]);
				fieldIndex++;

				// isPaid
				logger.fine("  isPaid=" + fields[fieldIndex] + "\n");
				boolean isPaid = Boolean.parseBoolean(fields[fieldIndex]);
				fieldIndex++;

				// isProvidedHealth
				logger.fine("  isProvidedHealth=" + fields[fieldIndex] + "\n");
				boolean isProvidedHealth = Boolean.parseBoolean(fields[fieldIndex]);
				fieldIndex++;

				// Persons
				logger.fine("  Persons=" + fields[fieldIndex] + "\n");
				List<IEntityPerson> personsList = new ArrayList<IEntityPerson>();
				String persons = fields[fieldIndex];
				String[] personsArray = persons.split("%");

				try {
					for (String personString : personsArray) {
						String[] personArray = personString.split("#", 7);
						if (personArray.length > 0) {
							IEntityPerson person = new Person();
							person.setLastname(personArray[0]);
							person.setFirstname(personArray[1]);
							person.setSex(personArray[2].charAt(0));
							person.setBirthday(df.parse(personArray[3]));
							person.setClub(personArray[4]);
							person.setLicense(personArray[5]);
							person.setEmail(personArray[6]);
							personsList.add(person);
						}
					}
				} catch (ParseException e) {
					logger.severe(e.getMessage());
					continue;
				}
				fieldIndex++;

				// Club
				logger.fine("  club=" + fields[fieldIndex] + "\n");
				String club = fields[fieldIndex];
				fieldIndex++;

				// Source
				logger.fine("  source=" + fields[fieldIndex] + "\n");
				String source = fields[fieldIndex];
				fieldIndex++;

				// ArrivalDate
				logger.fine("  ArrivalDate=" + fields[fieldIndex] + "\n");
				String arrivalDateField = fields[fieldIndex];
				Date arrivalDate = null;
				if (arrivalDateField != null
						&& !arrivalDateField.equalsIgnoreCase("")) {
					try {
						arrivalDate = df.parse(arrivalDateField);
					} catch (ParseException e) {
						logger.severe(e.getMessage());
						continue;
					}
				}
				fieldIndex++;

				// ElapsedTime
				logger.fine("  ElapsedTime=" + fields[fieldIndex] + "\n");
				String elapsedTimeField = fields[fieldIndex];
				long elapsedTime = 0;

				if (elapsedTimeField != null
						&& !elapsedTimeField.equalsIgnoreCase("")) {
					elapsedTime = Long.parseLong(elapsedTimeField);
				}
				fieldIndex++;

				// Import
				try {
					this.registering.importRegistered(event, competition,
							category, name, registeringDate, labelValue, rfid,
							isTeamed, isPaid, isProvidedHealth, personsList,
							club, source, arrivalDate, elapsedTime);
				} catch (AlreadyExistException e) {
					logger.log(Level.SEVERE, e.getMessage());
					exists++;
					//e.printStackTrace();
				} catch (RegisteringException e) {
					logger.log(Level.SEVERE, e.getMessage());
					error++;
					//e.printStackTrace();
				}

				row++;
			}

			// close the file
			bufRdr.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		row--;
		logger.log(Level.SEVERE, row + " lignes traitees, dont " + error + " en erreurs, et " + exists + " doublons");

		return row;
	}

	//@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private int importCategory(String fileName) {

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String line = null;
		int row = 0;
		int error = 0;
		int exists = 0;

		try {

			// Get the file
			BufferedReader bufRdr = new BufferedReader(new FileReader(fileName));

			// read each line of text file
			while ((line = bufRdr.readLine()) != null) {

				logger.fine("Line=" + line + "\n");

				// ignore first line (headers)
				if (row == 0) {
					row++;
					continue;
				}

				String[] fields = line.split(",", 7);
				int fieldIndex = 0;

				// Name
				logger.fine("  Name=" + fields[fieldIndex] + "\n");
				String name = fields[fieldIndex];
				fieldIndex++;

				// Sex
				logger.fine("  Sex=" + fields[fieldIndex] + "\n");
				char sex = fields[fieldIndex].charAt(0);
				fieldIndex++;

				// MinDate
				logger.fine("  MinDate=" + fields[fieldIndex] + "\n");
				Date minDate;
				try {
					minDate = df.parse(fields[fieldIndex]);
				} catch (ParseException e1) {
					logger.severe(e1.getMessage());
					continue;
				}
				fieldIndex++;

				// MaxDate
				logger.fine("  MinDate=" + fields[fieldIndex] + "\n");
				Date maxDate;
				try {
					maxDate = df.parse(fields[fieldIndex]);
				} catch (ParseException e1) {
					logger.severe(e1.getMessage());
					continue;
				}
				fieldIndex++;

				// ShortName
				logger.fine("  ShortName=" + fields[fieldIndex] + "\n");
				char shortName = fields[fieldIndex].charAt(0);
				fieldIndex++;

				// Competitions
				logger.fine("  Competitions=" + fields[fieldIndex] + "\n");
				List<IEntityCompetition> competitionsList = new ArrayList<IEntityCompetition>();
				String competitions = fields[fieldIndex];
				String[] competitionsArray = competitions.split("%");
				fieldIndex++;

				// Get Event
				logger.fine("  Event=" + fields[fieldIndex] + "\n");
				IEntityEvent event = findEventFromName(fields[fieldIndex]);
				fieldIndex++;

				// Resolve competitions list after event which is required for the finder
				for (String competitionName : competitionsArray) {
					IEntityCompetition competition = findCompetitionFromName(
							competitionName, event);
					if (competition == null) {
						logger.severe("Competition <" + competitionName
								+ "> introuvable");
						continue;
					}
					competitionsList.add(competition);

				}

				// Import
				try {
					importCategory(name, sex, minDate, maxDate, shortName, competitionsList, event);
				} catch (AlreadyExistException e) {
					logger.log(Level.SEVERE, e.getMessage());
					exists++;
					//e.printStackTrace();
				} catch (AdminException e) {
					logger.log(Level.SEVERE, e.getMessage());
					error++;
					//e.printStackTrace();
				}

				row++;
			}

			// close the file
			bufRdr.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		row--;
		logger.log(Level.SEVERE, row + " lignes traitees, dont " + error + " en erreurs, et " + exists + " doublons");

		return row;
	}

	//@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private int importCompetition(String fileName) {

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
		String line = null;
		int row = 0;
		int error = 0;
		int exists = 0;

		try {

			// Get the file
			BufferedReader bufRdr = new BufferedReader(new FileReader(fileName));

			// read each line of text file
			while ((line = bufRdr.readLine()) != null) {

				logger.fine("Line=" + line + "\n");

				// ignore first line (headers)
				if (row == 0) {
					row++;
					continue;
				}

				String[] fields = line.split(",", 7);
				int fieldIndex = 0;

				// Name
				logger.fine("  Name=" + fields[fieldIndex] + "\n");
				String name = fields[fieldIndex];
				fieldIndex++;

				// LowerLabelNumber
				logger.fine("  LowerLabelNumber=" + fields[fieldIndex] + "\n");
				String lowerLabelNumberField = fields[fieldIndex];
				int lowerLabelNumber = 0;

				if (lowerLabelNumberField != null
						&& !lowerLabelNumberField.equalsIgnoreCase("")) {
					lowerLabelNumber = Integer.parseInt(lowerLabelNumberField);
				}
				fieldIndex++;

				// HigherLabelNumber
				logger.fine("  HigherLabelNumber=" + fields[fieldIndex] + "\n");
				String higherLabelNumberField = fields[fieldIndex];
				int higherLabelNumber = 0;

				if (higherLabelNumberField != null
						&& !higherLabelNumberField.equalsIgnoreCase("")) {
					higherLabelNumber = Integer.parseInt(higherLabelNumberField);
				}
				fieldIndex++;


				// LastLabelNumber
				logger.fine("  LastLabelNumber=" + fields[fieldIndex] + "\n");
				String lastLabelNumberField = fields[fieldIndex];
				int lastLabelNumber = 0;

				if (lastLabelNumberField != null
						&& !lastLabelNumberField.equalsIgnoreCase("")) {
					lastLabelNumber = Integer.parseInt(lastLabelNumberField);
				}
				fieldIndex++;

				// isTeamed
				logger.fine("  isTeamed=" + fields[fieldIndex] + "\n");
				boolean isTeamed = Boolean.parseBoolean(fields[fieldIndex]);
				fieldIndex++;

				// StartingDate
				logger.fine("  StartingDate=" + fields[fieldIndex] + "\n");
				Date startingDate;
				try {
					if (fields[fieldIndex] != null && !fields[fieldIndex].equalsIgnoreCase("")) {
						startingDate = df.parse(fields[fieldIndex]);
					} else {
						startingDate = null;
					}
				} catch (ParseException e1) {
					logger.severe(e1.getMessage());
					continue;
				}
				fieldIndex++;

				// Get Event
				logger.fine("  Event=" + fields[fieldIndex] + "\n");
				IEntityEvent event = findEventFromName(fields[fieldIndex]);
				fieldIndex++;

				// Import
				try {
					importCompetition(name, lowerLabelNumber, higherLabelNumber, lastLabelNumber, isTeamed, startingDate, event);
				} catch (AlreadyExistException e) {
					logger.log(Level.SEVERE, e.getMessage());
					exists++;

					// update the starting date if needed
			    	IEntityCompetition competition = findCompetitionFromName(name, event);
					if (startingDate != null && !startingDate.equals(competition.getStartingDate())) {

				    	competition.setStartingDate(startingDate);
						logger.log(Level.SEVERE, "Update the starting date for competition " + competition);

					}

					//e.printStackTrace();
				} catch (AdminException e) {
					logger.log(Level.SEVERE, e.getMessage());
					error++;
					//e.printStackTrace();
				}

				row++;
			}

			// close the file
			bufRdr.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		row--;
		logger.log(Level.SEVERE, row + " lignes traitees, dont " + error + " en erreurs, et " + exists + " doublons");

		return row;
	}

	//@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private int importEvent(String fileName) {

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String line = null;
		int row = 0;
		int error = 0;
		int exists = 0;

		try {

			// Get the file
			BufferedReader bufRdr = new BufferedReader(new FileReader(fileName));

			// read each line of text file
			while ((line = bufRdr.readLine()) != null) {

				logger.fine("Line=" + line + "\n");

				// ignore first line (headers)
				if (row == 0) {
					row++;
					continue;
				}

				String[] fields = line.split(",", 3);
				int fieldIndex = 0;

				// Name
				logger.fine("  Name=" + fields[fieldIndex] + "\n");
				String name = fields[fieldIndex];
				fieldIndex++;

				// Date
				logger.fine("  Date=" + fields[fieldIndex] + "\n");
				Date date;
				try {
					date = df.parse(fields[fieldIndex]);
				} catch (ParseException e1) {
					logger.severe(e1.getMessage());
					continue;
				}
				fieldIndex++;

				// isDefault
				logger.fine("  isDefault=" + fields[fieldIndex] + "\n");
				boolean isDefault = Boolean.parseBoolean(fields[fieldIndex]);
				fieldIndex++;

				// Import
				try {
					importEvent(name, date,	isDefault);
				} catch (AlreadyExistException e) {
					logger.log(Level.SEVERE, e.getMessage());
					exists++;
					//e.printStackTrace();
				} catch (AdminException e) {
					logger.log(Level.SEVERE, e.getMessage());
					error++;
					//e.printStackTrace();
				}

				row++;
			}

			// close the file
			bufRdr.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		row--;
		logger.log(Level.SEVERE, row + " lignes traitees, dont " + error + " en erreurs, et " + exists + " doublons");

		return row;
	}

    /*
     * Import an event
     */
    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IEntityEvent importEvent(
			final String name,
			final Date date,
			final boolean isDefault) throws AlreadyExistException, AdminException {

    	// Check if the event already exists
    	IEntityEvent event = findEventFromName(name);

    	if (event != null) {
        	throw new AlreadyExistException("L'evenement <" + name + "> existe deja ! - " + event );
    	}

    	event = createEvent(name, date, AdminBean.EVENT_LOGO, false);
    	if (isDefault) {
    		setDefaultEvent(event.getId());
    	}

		logger.log(Level.FINE, "creation <" + event + ">");

        return event;

    }

    /*
     * Import a competition
     */
    @SuppressWarnings("unchecked")

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IEntityCompetition importCompetition(final String name,
			final int lowerLabelNumber,
			final int higherLabelNumber,
			final int lastLabelNumber,
			boolean isTeamed,
			Date startingDate,
			IEntityEvent event) throws AlreadyExistException, AdminException {

    	// Check if the event already exists
    	IEntityCompetition competition = findCompetitionFromName(name, event);

    	if (competition != null) {
        	throw new AlreadyExistException("La competition <" + name + "> existe deja ! - " + competition );
    	}

    	competition = createCompetition(name, lowerLabelNumber, higherLabelNumber, lastLabelNumber, event.getId(), isTeamed);
		competition.setStartingDate(startingDate);

		logger.log(Level.FINE, "creation <" + competition + ">");

        return competition;

    }

    /*
     * Import a category
     */
    @SuppressWarnings("unchecked")

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public IEntityCategory importCategory(final String name,
			final char sex,
			final Date minDate,
			final Date maxDate,
			final char shortName,
			final List<IEntityCompetition> competitions,
			IEntityEvent event) throws AlreadyExistException, AdminException {

    	// Check if the event already exists
    	IEntityCategory category = findCategoryFromName(name, event);

    	if (category != null) {
        	throw new AlreadyExistException("La categorie <" + name + "> existe deja ! - " + category );
    	}

    	category = doCreateCategory(name, minDate, maxDate, sex, shortName, event.getId(), competitions);

		logger.log(Level.FINE, "creation <" + category + ">");

        return category;

    }




	public IEntityEvent duplicateEvent(final int eventId) {
    	IEntityEvent event = findEventFromId(eventId);
    	boolean force = true;
    	// clone event
    	IEntityEvent cloneEvent = createEventWithBinImage(event.getName(), event.getDate(), event.getImageFile(), force);
    	cloneEvent.setTheDefault(false);

    	// clone competitions related to the event
    	// create a mapping table for retrieving competitions when duplicating categories
    	List <IEntityCompetition> competitions = findAllCompetitionsFromEvent(event.getId());
    	Map<Integer,IEntityCompetition> competitionsArray = new HashMap<Integer, IEntityCompetition>();

    	for (IEntityCompetition competition : competitions) {
    		IEntityCompetition newCompetition = createCompetition(
    				competition.getName(),
    				competition.getLowerLabelNumber(),
    				competition.getHigherLabelNumber(),
    				competition.getLastLabelNumber(),
    				cloneEvent.getId(),
    				competition.isTeamed(),
    				force);
    		competitionsArray.put(competition.getId(), newCompetition);

    	}


    	// clone categories related to the event
    	List <IEntityCategory> categories = findAllCategoriesFromEvent(event.getId());
    	for (IEntityCategory category: categories) {
    		IEntityCompetition[] competitionsCategoryArray = new IEntityCompetition[category.getCompetitions().size()];
        	int index = 0;
        	for(IEntityCompetition competition : category.getCompetitions()) {
        		competitionsCategoryArray[index] = competitionsArray.get(competition.getId());
        		index++;
        	}
    		createCategory(
    				force,
    				category.getName(),
    				category.getMinDate(),
    				category.getMaxDate(),
    				category.getSex(),
    				category.getShortName(),
    				cloneEvent.getId(),
    				competitionsCategoryArray);

    	}

    	return cloneEvent;

	}

    /**
     * Reset All admin data and registered data
     */
    public void resetAdminAll() throws AdminException {

		// at first, check if there is not any registered associated with this event
    	List<IEntityEvent> events = findAllEvents();
    	for (IEntityEvent event:events) {

        	List<IEntityRegistered> registereds = registering.findAllRegisteredFromEvent(event.getId());
    		if (registereds != null && !registereds.isEmpty()) {
    			throw new AdminException("There are some registered associated with the event :" + event.getId() + " - " + event.getName());
    		}
    	}

    	// if  ok, remove all events
    	for (IEntityEvent event:events) {

        	// remove the event, cascading removing removes all related objects (competitions/categories)
        	doRemoveEvent(event.getId(), true);

    	}

    }


}
