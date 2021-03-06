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
package net.o3s.web.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.o3s.apis.AdminException;
import net.o3s.apis.AlreadyExistException;
import net.o3s.apis.IEJBAdminRemote;
import net.o3s.apis.IEJBEventMessageProducerRemote;
import net.o3s.apis.IEJBInitRemote;
import net.o3s.apis.IEJBRegisteringRemote;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityEvent;
import net.o3s.apis.IEntityPerson;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.RegisteringException;
import net.o3s.apis.TestData;
import net.o3s.apis.TrackingMessage;

public class Init {

	/**
	 * Logger
	 */
    private static Logger logger = Logger.getLogger(Init.class.getName());

	//@EJB
	private static IEJBInitRemote initor;

	//@EJB
	private static IEJBRegisteringRemote registering;

	//@EJB
	private static IEJBAdminRemote admin;

	//@EJB
	private static IEJBEventMessageProducerRemote tracking;

	public Init() {
		InitialContext context=null;
		try {
			context = new InitialContext();
			initor = (IEJBInitRemote) context.lookup("net.o3s.beans.init.InitBean_net.o3s.apis.IEJBInitRemote@Remote");
			registering = (IEJBRegisteringRemote) context.lookup("net.o3s.beans.registering.RegisteringBean_net.o3s.apis.IEJBRegisteringRemote@Remote");
			admin = (IEJBAdminRemote) context.lookup("net.o3s.beans.admin.AdminBean_net.o3s.apis.IEJBAdminRemote@Remote");
			tracking = (IEJBEventMessageProducerRemote) context.lookup("net.o3s.beans.tracking.TrackingMessageProducerBean_net.o3s.apis.IEJBEventMessageProducerRemote@Remote");


		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private static Vector<IEntityRegistered> vregistered = new Vector<IEntityRegistered>();
	private static Vector<IEntityRegistered> vregisteredOverflow = new Vector<IEntityRegistered>();

	public void populateAdminData() {
		initor.init();
		logger.fine("initOk!");

	}

	public void populateRegistered() {

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		List<IEntityPerson> team0 = new ArrayList<IEntityPerson>();
		List<IEntityPerson> team1 = new ArrayList<IEntityPerson>();
		List<IEntityPerson> team2 = new ArrayList<IEntityPerson>();
		List<IEntityPerson> team3 = new ArrayList<IEntityPerson>();
		List<IEntityPerson> team4 = new ArrayList<IEntityPerson>();
		List<IEntityPerson> team5 = new ArrayList<IEntityPerson>();
		List<IEntityPerson> team6 = new ArrayList<IEntityPerson>();
		List<IEntityPerson> team7 = new ArrayList<IEntityPerson>();
		List<IEntityPerson> team8 = new ArrayList<IEntityPerson>();
		List<IEntityPerson> team9 = new ArrayList<IEntityPerson>();
		List<IEntityPerson> team10 = new ArrayList<IEntityPerson>();
		List<IEntityPerson> team11 = new ArrayList<IEntityPerson>();

		try {
			team0.add( registering.createPerson("TNOM1", "Tprenom1", "CLUB1", "L0", "Tprenom1.Tnom1@gmail.com", 'M', df.parse("09/12/1975")));
			team1.add( registering.createPerson("TNOM2", "Tprenom2", "CLUB1", "L1", "Tprenom2.Tnom2@gmail.com", 'F', df.parse("06/02/2002")));

			team2.add( registering.createPerson("TNOM3", "Tprenom3", "CLUB1", "L2-1", "Tprenom3.Tnom3@gmail.com", 'F', df.parse("18/04/2003")));
			team2.add( registering.createPerson("TNOM4", "Tprenom4", "CLUB1", "L2-2", "Tprenom4.Tnom4@gmail.com", 'F', df.parse("19/07/2007")));
			team2.add( registering.createPerson("TNOM5", "Tprenom5", "CLUB1", "L2-3", "Tprenom5.Tnom5@gmail.com", 'F', df.parse("06/01/1972")));

			team3.add( registering.createPerson("TNOM6", "Tprenom6", "CLUB1", "L3", "Tprenom6.Tnom6@paris.fr", 'M', df.parse("18/11/1975")));
			team4.add( registering.createPerson("TNOM7", "Tprenom7", "CLUB1", "L4", "Tprenom7.Tnom7@free.fr", 'M', df.parse("06/11/1979")));
			team5.add( registering.createPerson("TNOM8", "Tprenom8", "CLUB1", "L5", "Tprenom8.Tnom8@free.fr", 'M', df.parse("20/05/1968")));
			team6.add( registering.createPerson("TNOM9", "Tprenom9",  "CLUB2", "L6", "Tprenom9.Tnom9@free.fr", 'M', df.parse("04/02/1971")));
			team7.add( registering.createPerson("TNOM10", "Tprenom10", "CLUB2", "L7", "Tprenom10.Tnom10@neuf.fr", 'M', df.parse("25/07/1972")));
			team8.add( registering.createPerson("TNOM11", "Tprenom11", "CLUB2", "L8", "Tprenom11.Tnom11@free.fr", 'M', df.parse("01/11/1972")));
			team9.add( registering.createPerson("TNOM12", "Tprenom12", "CLUB2", "L9", "Tprenom12.Tnom12@paris.fr", 'M', df.parse("25/12/1972")));
			team10.add( registering.createPerson("TNOM13", "Tprenom13", "CLUB2", "L10", "Tprenom13.Tnom13@free.fr", 'M', df.parse("25/09/1956")));
			team11.add( registering.createPerson("TNOM14", "Tprenom14", "CLUB2", "L11", "Tprenom14.Tnom14@free.fr", 'M', df.parse("25/09/1983")));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RegisteringException e) {
			e.printStackTrace();
		} catch (AlreadyExistException e) {
			e.printStackTrace();
		}
		IEntityEvent event = admin.findDefaultEvent();

		IEntityCompetition competition1 = admin.findCompetitionFromName(TestData.COMPETITION_NAME_7,event);
		IEntityCompetition competition2 = admin.findCompetitionFromName(TestData.COMPETITION_NAME_2_2,event);
		IEntityCompetition competition3 = admin.findCompetitionFromName(TestData.COMPETITION_NAME_8,event);

		vregistered.removeAllElements();

		try {
			vregistered.addAll(registering.createRegistered(team0, competition1.getId(), false, "", true, true, "web"));
			registering.createRegistered(team1, competition2.getId(), false, "", true, true, "internal");
			registering.createRegistered(team2, competition3.getId(), true,"EQUIPE1", false, false, "internal");
			vregistered.addAll(registering.createRegistered(team3, competition1.getId(), false, "", true, true, "web"));
			vregistered.addAll(registering.createRegistered(team4, competition1.getId(), false, "", true, true, "web"));
			vregistered.addAll(registering.createRegistered(team5, competition1.getId(), false, "", true, true, "web"));
			vregistered.addAll(registering.createRegistered(team6, competition1.getId(), false, "", true, true, "web"));
			vregistered.addAll(registering.createRegistered(team7, competition1.getId(), false, "", true, true, "web"));
			vregistered.addAll(registering.createRegistered(team8, competition1.getId(), false, "", true, true, "web"));
			vregistered.addAll(registering.createRegistered(team9, competition1.getId(), false, "", true, true, "web"));
			vregistered.addAll(registering.createRegistered(team10, competition1.getId(), false, "", true, true, "web"));
			vregistered.addAll(registering.createRegistered(team11, competition1.getId(), false, "", true, true, "web"));
		} catch (RegisteringException e) {
			e.printStackTrace();
		} catch (AlreadyExistException e) {
			e.printStackTrace();
		}
	}

	public void populateRegisteredWithOverflow() {

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");


		List<IEntityPerson> team0 = new ArrayList<IEntityPerson>();
		vregisteredOverflow.removeAllElements();
		IEntityEvent event = admin.findDefaultEvent();
		IEntityCompetition competition1 = admin.findCompetitionFromName(TestData.COMPETITION_NAME_7, event);

		for (int i = 0 ; i < ( (competition1.getHigherLabelNumber() - competition1.getLowerLabelNumber()) + 1) ; i++) {

			team0.clear();
			try {
				team0.add(registering.createPerson("TN" + i, "TP" + i, "TC",
						"TL", "", 'M', df.parse("09/12/1971")));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RegisteringException e) {
				e.printStackTrace();
			} catch (AlreadyExistException e) {
				e.printStackTrace();
			}

			try {
				vregisteredOverflow.addAll(registering.createRegistered(team0,
						competition1.getId(), false, "", true, true, "web"));
			} catch (RegisteringException e) {
				e.printStackTrace();
			} catch (AlreadyExistException e) {
				e.printStackTrace();
			}
		}
	}

	public void removeRegisteredWithOverflow() {
		for (IEntityRegistered registered : vregisteredOverflow) {
			try {
				registering.removeRegistered(registered.getId());
			} catch (RegisteringException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (IEntityPerson person : registered.getPersons()) {
				try {
					registering.removePerson(person.getId());
				} catch (RegisteringException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		vregisteredOverflow.removeAllElements();
	}

	@SuppressWarnings("unchecked")
	public void simulateDepartureAndArrivals() {

		Date startingDate = new Date();

		logger.fine("Starting date set to:" + startingDate);

		IEntityEvent event = admin.findDefaultEvent();
		IEntityCompetition competition1 = admin.findCompetitionFromName(TestData.COMPETITION_NAME_7, event);

		try {
			admin.setStartDateInCompetition(competition1.getId());
		} catch (AdminException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Vector<IEntityRegistered> registeredForThisSprint= (Vector<IEntityRegistered>) vregistered.clone();

		try {
			Thread.sleep(5000);
			Random randomGenerator = new Random();
			int[] duration = {0,300,1200,6000,25010,70020,300050,1860005,3660006,86400002};
			int count=0;
			while (! registeredForThisSprint.isEmpty()) {
				int index = randomGenerator.nextInt(registeredForThisSprint.size());
				Date d = new Date(System.currentTimeMillis() + duration[count]);
				tracking.createEvent("Arrivee1", registeredForThisSprint.get(index).getLabel().getValue(), TrackingMessage.EVENT_INT_TYPE_ARRIVAL_CTIME, d);
				logger.fine("ARRIVAL Label [" + registeredForThisSprint.get(index).getLabel().getValue() + "] - duration=" + duration[count] + " Date ~" + d);
				registeredForThisSprint.removeElementAt(index);
				count++;
			}
			/*
				Thread.sleep(5000);
				Random randomGenerator = new Random();
				while (! vregistered.isEmpty()) {
					int index = randomGenerator.nextInt(vregistered.size());
					tracking.createEvent("Arrivee1", vregistered.get(index).getLabel().getValue(), TrackingMessage.EVENT_INT_TYPE_ARRIVAL);
					logger.fine("ARRIVAL Label [" + vregistered.get(index).getLabel().getValue() + "] - Date ~" + new Date());
					Thread.sleep(randomGenerator.nextInt(2000));
					vregistered.removeElementAt(index);
				}*/

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
}



