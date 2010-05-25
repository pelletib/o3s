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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.o3s.apis.AdminException;
import net.o3s.apis.AlreadyExistException;
import net.o3s.apis.IEJBAdminRemote;
import net.o3s.apis.IEJBEventMessageProducerRemote;
import net.o3s.apis.IEJBInitRemote;
import net.o3s.apis.IEJBRegisteringRemote;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityPerson;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.RegisteringException;
import net.o3s.apis.TrackingMessage;

public class Init {

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

	public void populateAdminData() {
		initor.init();
		System.out.println("initOk!");

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
			team0.add( registering.createPerson("PELLETIER", "Benoit", "Crossey", "L0", "btpelletier@gmail.com", 'M', df.parse("09/12/1971")));
			team1.add( registering.createPerson("PELLETIER", "Lea", "Crossey", "L1", "leapelletier@gmail.com", 'F', df.parse("06/02/2000")));

			team2.add( registering.createPerson("PELLETIER", "Cleo", "Crossey", "L2-1", "cleopelletier@gmail.com", 'F', df.parse("18/04/2002")));
			team2.add( registering.createPerson("PELLETIER", "Lou", "Crossey", "L2-2", "loupelletier@gmail.com", 'F', df.parse("19/07/2006")));
			team2.add( registering.createPerson("PELLETIER", "sophie", "Crossey", "L2-3", "sophiepelletier@gmail.com", 'F', df.parse("06/01/1971")));

			team3.add( registering.createPerson("DUPONT", "Jean", "Paris", "L3", "jean.dupont@paris.fr", 'M', df.parse("18/11/1972")));
			team4.add( registering.createPerson("LACAPERE", "Jerome", "Crossey", "L4", "jerome.lacapere@free.fr", 'M', df.parse("06/11/1978")));
			team5.add( registering.createPerson("BOURGEOIS", "Christian", "Grenoble", "L5", "christian.bourgeois@free.fr", 'M', df.parse("20/05/1967")));
			team6.add( registering.createPerson("VOORSPOELS", "Alain", "Crossey", "L6", "alain.voerspoels@free.fr", 'M', df.parse("04/02/1970")));
			team7.add( registering.createPerson("PERGE", "Laurent", "Crossey", "L7", "laurent.perge@neuf.fr", 'M', df.parse("25/07/1971")));
			team8.add( registering.createPerson("PATOU", "Louis", "GrandeVille", "L8", "louis.patou@free.fr", 'M', df.parse("01/11/1971")));
			team9.add( registering.createPerson("TROPLENT", "Jean-Louis", "Paris", "L9", "jean.troplent@paris.fr", 'M', df.parse("25/12/1971")));
			team10.add( registering.createPerson("TROPRAPIDE", "truc", "machin", "L10", "truc@machin.fr", 'M', df.parse("25/09/1955")));
			team11.add( registering.createPerson("SPEEDY", "Gonzales", "Mexico", "L11", "sg@mexico.com", 'M', df.parse("25/09/1982")));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RegisteringException e) {
			e.printStackTrace();
		} catch (AlreadyExistException e) {
			e.printStackTrace();
		}
		IEntityCompetition competition1 = admin.findCompetitionFromId(9);
		IEntityCompetition competition2 = admin.findCompetitionFromId(4);
		IEntityCompetition competition3 = admin.findCompetitionFromId(10);

		vregistered.removeAllElements();

		try {
			vregistered.addAll(registering.createRegistered(team0, competition1.getId(), false, "", true, true));
			registering.createRegistered(team1, competition2.getId(), false, "", true, true);
			registering.createRegistered(team2, competition3.getId(), true,
					"les stroumpfs", false, false);
			vregistered.addAll(registering.createRegistered(team3, competition1.getId(), false, "", true, true));
			vregistered.addAll(registering.createRegistered(team4, competition1.getId(), false, "", true, true));
			vregistered.addAll(registering.createRegistered(team5, competition1.getId(), false, "", true, true));
			vregistered.addAll(registering.createRegistered(team6, competition1.getId(), false, "", true, true));
			vregistered.addAll(registering.createRegistered(team7, competition1.getId(), false, "", true, true));
			vregistered.addAll(registering.createRegistered(team8, competition1.getId(), false, "", true, true));
			vregistered.addAll(registering.createRegistered(team9, competition1.getId(), false, "", true, true));
			vregistered.addAll(registering.createRegistered(team10, competition1.getId(), false, "", true, true));
			vregistered.addAll(registering.createRegistered(team11, competition1.getId(), false, "", true, true));
		} catch (RegisteringException e) {
			e.printStackTrace();
		} catch (AlreadyExistException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void simulateDepartureAndArrivals() {

		Date startingDate = new Date();

		System.out.println("Starting date set to:" + startingDate);

		IEntityCompetition competition1 = admin.findCompetitionFromId(9);

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
				tracking.createEvent("Arrivee1", registeredForThisSprint.get(index).getLabel().getValue(), TrackingMessage.EVENT_INT_TYPE_ARRIVAL, d);
				System.out.println("ARRIVAL Label [" + registeredForThisSprint.get(index).getLabel().getValue() + "] - duration=" + duration[count] + " Date ~" + d);
				registeredForThisSprint.removeElementAt(index);
				count++;
			}
			/*
				Thread.sleep(5000);
				Random randomGenerator = new Random();
				while (! vregistered.isEmpty()) {
					int index = randomGenerator.nextInt(vregistered.size());
					tracking.createEvent("Arrivee1", vregistered.get(index).getLabel().getValue(), TrackingMessage.EVENT_INT_TYPE_ARRIVAL);
					System.out.println("ARRIVAL Label [" + vregistered.get(index).getLabel().getValue() + "] - Date ~" + new Date());
					Thread.sleep(randomGenerator.nextInt(2000));
					vregistered.removeElementAt(index);
				}*/

		} catch (Exception e) {

			e.printStackTrace();

		}
	}
}



