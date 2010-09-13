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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
import net.o3s.apis.ReportException;
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
    	IEntityEvent event = null;

    	event = findEventFromName(name);
    	if (event == null) {
        	logger.fine("Create new event : " + name);

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
        Query query = this.entityManager.createNamedQuery("ALL_COMPETITIONS_FROM_EVENT");
        IEntityEvent event = findDefaultEvent();

        List<IEntityCompetition> competitions = null;
        try {
            query.setParameter("EVENTID", event.getId());
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
        Query query = this.entityManager.createNamedQuery("ALL_CATEGORIES_FROM_EVENT");
        IEntityEvent event = findDefaultEvent();
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
	public IEntityCategory createCategory(final String name, final Date minDate, final Date maxDate, final char sex, final char shortName, final IEntityEvent event, final IEntityCompetition... competitions) {
    	IEntityCategory category = null;
    	category = findCategoryFromNameAndSex(name, sex);
        if (category == null) {
        	logger.fine("Create new category : " + name);

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

				String[] fields = line.split(",", 14);

				// Get Event
				logger.fine("  Event=" + fields[0] + "\n");
				IEntityEvent event = findEventFromName(fields[0]);

				// Get Competition
				logger.fine("  Competition=" + fields[1]);
				IEntityCompetition competition = findCompetitionFromName(
						fields[1], event);

				// Get Category
				logger.fine("  Category=" + fields[2] + "\n");
				IEntityCategory category = findCategoryFromName(fields[2],
						event);

				// Name
				logger.fine("  Name=" + fields[3] + "\n");
				String name = fields[3];

				// RegisteringDate
				logger.fine("  RegisteringDate=" + fields[4] + "\n");
				Date registeringDate;
				try {
					registeringDate = df.parse(fields[4]);
				} catch (ParseException e1) {
					logger.severe(e1.getMessage());
					continue;
				}

				// Label
				logger.fine("  labelValue=" + fields[5] + "\n");
				String labelValue = fields[5];

				// isTeamed
				logger.fine("  isTeamed=" + fields[6] + "\n");
				boolean isTeamed = Boolean.parseBoolean(fields[6]);

				// isPaid
				logger.fine("  isPaid=" + fields[7] + "\n");
				boolean isPaid = Boolean.parseBoolean(fields[7]);

				// isProvidedHealth
				logger.fine("  isProvidedHealth=" + fields[8] + "\n");
				boolean isProvidedHealth = Boolean.parseBoolean(fields[8]);

				// Persons
				logger.fine("  Persons=" + fields[9] + "\n");
				List<IEntityPerson> personsList = new ArrayList<IEntityPerson>();
				String persons = fields[9];
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

				// Club
				logger.fine("  club=" + fields[10] + "\n");
				String club = fields[10];

				// Source
				logger.fine("  source=" + fields[11] + "\n");
				String source = fields[11];

				// ArrivalDate
				logger.fine("  ArrivalDate=" + fields[12] + "\n");
				String arrivalDateField = fields[12];
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

				// ElapsedTime
				logger.fine("  ElapsedTime=" + fields[13] + "\n");
				String elapsedTimeField = fields[13];
				long elapsedTime = 0;

				if (elapsedTimeField != null
						&& !elapsedTimeField.equalsIgnoreCase("")) {
					elapsedTime = Long.parseLong(elapsedTimeField);
				}

				// Import
				try {
					this.registering.importRegistered(event, competition,
							category, name, registeringDate, labelValue,
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

}
