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
package net.o3s.beans.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.o3s.apis.AdminException;
import net.o3s.apis.IEJBAdminLocal;
import net.o3s.apis.IEJBAdminRemote;
import net.o3s.apis.IEJBNotificationProducerLocal;
import net.o3s.apis.IEntityCategory;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityEvent;
import net.o3s.apis.NotificationMessageException;
import net.o3s.persistence.Category;
import net.o3s.persistence.Competition;
import net.o3s.persistence.Event;


/**
 * Session Bean implementation class AdminBean
 */
@Stateless
@Local(IEJBAdminLocal.class)
@Remote(IEJBAdminRemote.class)
public class AdminBean implements IEJBAdminLocal,IEJBAdminRemote {

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

    public IEntityEvent createEvent(final String name, final Date date, final String fileName) {
    	IEntityEvent event = null;

    	event = findEventFromName(name);
    	if (event == null) {
        	event = new Event();
            event.setName(name);
            event.setDate(date);
            event.setTheDefault(false);

            if (fileName != null) {
				try {
					// Lets open an image file
					event.setImageFile(readImage(fileName));
				} catch (Exception ex) {
					logger.log(Level.SEVERE, null, ex);
				}
            }

            this.entityManager.persist(event);
        }
        return event;
    }

    public void setDefaultEvent(final int id) {

    	IEntityEvent oldDefaultEvent = findDefaultEvent();
    	IEntityEvent newDefaultEvent = findEventFromId(id);

    	if (newDefaultEvent != null) {
    		newDefaultEvent.setTheDefault(true);
        	oldDefaultEvent.setTheDefault(false);
    	}
    }

    public IEntityCompetition findCompetitionFromName(final String name) {
        Query query = this.entityManager.createNamedQuery("COMPETITION_FROM_NAME");
        query.setParameter("NAME", name);
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
        Query query = this.entityManager.createNamedQuery("ALL_COMPETITIONS_FROM_EVENT");
        IEntityEvent event = findDefaultEvent();

        List<IEntityCompetition> competitions = null;
        try {
            query.setParameter("EVENT_ID", event.getId());
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

    public IEntityCompetition createCompetition(final String name, final int lowerLabelNumber, final int higherLabelNumber, final int lastLabelNumber, final IEntityEvent event, final boolean isTeamed) {
    	IEntityCompetition competition = null;
    	competition = findCompetitionFromName(name);
        if (competition == null) {
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


    public Date setStartDateInCompetition(final int id) throws AdminException {
    	IEntityCompetition competition = findCompetitionFromId(id);
    	Date date = new Date();
    	if (competition == null) {
    		throw new AdminException("Unable to find Competition (id=" + id + ")");

    	} else {
    		competition.setStartingDate(date);

    		try {
    			notification.sendDepartureNotification(competition);
    		} catch (NotificationMessageException e) {
    			logger.severe("Unable to send a notification :" + e.getMessage());
    		}

    	}
    	return date;
    }

    @SuppressWarnings("unchecked")
    public List<IEntityCategory> findCategoryFromDatesAndSex(final Date date, final char sex) {
    	Query query = this.entityManager.createNamedQuery("CATEGORY_FROM_DATES_AND_SEX");
    	query.setParameter("DATE", date);
    	query.setParameter("SEX", sex);

    	 List<IEntityCategory> categories = null;
    	 try {
    		 categories = query.getResultList();
         } catch (javax.persistence.NoResultException e) {
         	categories = new ArrayList<IEntityCategory>();

         }
         return categories;

    }


    public IEntityCategory findNoCategory() {
    	Query query = this.entityManager.createNamedQuery("NOCATEGORY");
    	IEntityCategory category = null;
        try {
       	 category = (IEntityCategory) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        }
         return category;

    }

    public IEntityCategory findCategoryFromNameAndSex(final String name, final char sex) {
    	IEntityCategory category = null;

        try {
    		Query query = this.entityManager.createNamedQuery("CATEGORY_FROM_NAME");
    		query.setParameter("NAME", name + " (" + sex + ")");
    		category = (IEntityCategory) query.getSingleResult();
        } catch (javax.persistence.NoResultException e) {
        	Query query = this.entityManager.createNamedQuery("CATEGORY_FROM_NAME_AND_SEX");
    		query.setParameter("NAME", name);
    		query.setParameter("SEX", sex);
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
        Query query = this.entityManager.createNamedQuery("ALL_CATEGORIES_FROM_EVENT");
        IEntityEvent event = findDefaultEvent();
        query.setParameter("EVENT_ID", event.getId());

        List<IEntityCategory> categories = null;
        try {
        	categories = query.getResultList();
        } catch (javax.persistence.NoResultException e) {
        	categories = new ArrayList<IEntityCategory>();
        }
        return categories;
    }

    @SuppressWarnings("unchecked")
	public IEntityCategory createCategory(final String name, final Date minDate, final Date maxDate, final char sex, final char shortName, final IEntityEvent event, final IEntityCompetition... competitions) {
    	IEntityCategory category = null;
    	category = findCategoryFromNameAndSex(name, sex);
        if (category == null) {
        	category = new Category();
        	category.setName(name + " (" + sex + ")");
        	category.setMinDate(minDate);
        	category.setMaxDate(maxDate);
        	category.setSex(sex);
        	category.setShortName(shortName);
        	category.setEvent(event);
        	category.setCompetitions(new HashSet(Arrays.asList(competitions)));

            this.entityManager.persist(category);
        }

        return category;
    }

}
