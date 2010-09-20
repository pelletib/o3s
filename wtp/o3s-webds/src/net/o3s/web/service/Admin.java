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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.o3s.apis.AdminException;
import net.o3s.apis.AlreadyExistException;
import net.o3s.apis.IEJBAdminRemote;
import net.o3s.apis.IEntityCategory;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityEvent;
import net.o3s.apis.IEntityPerson;
import net.o3s.apis.RegisteringException;
import net.o3s.web.common.Util;
import net.o3s.web.vo.CategoryVO;
import net.o3s.web.vo.CompetitionVO;
import net.o3s.web.vo.EventVO;
import net.o3s.web.vo.FlexException;
import net.o3s.web.vo.PersonVO;


public class Admin {

	/**
	 * Logger
	 */
    private static Logger logger = Logger.getLogger(Admin.class.getName());

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

	public Date setStartDate(int competitionId) {

		setAdminEJB();

		Date date = null;
		try {
			date = admin.setStartDateInCompetition(competitionId);
		} catch (AdminException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return date;
	}

	public EventVO getDefaultEvent() {
		setAdminEJB();
		IEntityEvent event = admin.findDefaultEvent();
		logger.fine("Default event=" + event);
		return Util.createEventVO(event);
	}

	public void setDefaultEvent(final int id) {
		setAdminEJB();
		admin.setDefaultEvent(id);
	}

	public EventVO createEvent(EventVO eventVO) {
		setAdminEJB();

		IEntityEvent event = null;
		try {

			event = admin.createEvent(eventVO.getName(), eventVO.getDate(), null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new FlexException(e.getMessage());
		}

		EventVO eVO = Util.createEventVO(event);

		logger.fine("event=" + eVO);
		return eVO;
	}

	public CompetitionVO createCompetition(CompetitionVO competitionVO) {
		setAdminEJB();

		IEntityCompetition competition = null;

		try {

			competition = admin.createCompetition(
					competitionVO.getName(),
					competitionVO.getLowerLabelNumber(),
					competitionVO.getHigherLabelNumber(),
					competitionVO.getLastLabelNumber(),
					competitionVO.getEvent().getId(),
					competitionVO.isTeamed());
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new FlexException(e.getMessage());
		}

		CompetitionVO cVO = Util.createCompetitionVO(competition);

		logger.fine("competition=" + cVO);
		return cVO;
	}

	public CategoryVO createCategory(CategoryVO categoryVO) {
		setAdminEJB();

		IEntityCategory category = null;

		List<IEntityCompetition> competitions = new ArrayList<IEntityCompetition>();
		IEntityCompetition[] competitionsArray = null;

		if (categoryVO.getCompetitions() != null) {
			for (CompetitionVO competitionVO:categoryVO.getCompetitions()) {
				competitions.add(admin.findCompetitionFromId(competitionVO.getId()));
			}
	    	competitionsArray = new IEntityCompetition[categoryVO.getCompetitions().size()];
		} else {
	    	competitionsArray = new IEntityCompetition[1];
		}

		try {
			category = admin.createCategory(
					categoryVO.getName(),
					categoryVO.getMinDate(),
					categoryVO.getMaxDate(),
					categoryVO.getSex(),
					categoryVO.getShortName(),
					categoryVO.getEvent().getId(),
					competitions.toArray(competitionsArray));
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new FlexException(e.getMessage());
		}

		CategoryVO cVO = Util.createCategoryVO(category);

		logger.fine("category=" + cVO);
		return cVO;
	}


	public EventVO duplicateEvent(int id) {
		setAdminEJB();

		IEntityEvent event = null;
		try {
			event = admin.duplicateEvent(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new FlexException(e.getMessage());
		}

		EventVO eVO = Util.createEventVO(event);

		logger.fine("event=" + eVO);
		return eVO;
	}

	// update event
	public EventVO updateEvent(EventVO eventVO) {
		setAdminEJB();
		IEntityEvent event = null;
		logger.fine("update event" + eventVO.getId());

		try {
			event = admin.updateEvent(eventVO.getId(), eventVO.getName(), eventVO.getDate());
		} catch (AdminException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}

		EventVO eVO = Util.createEventVO(event);

		logger.fine("event=" + eVO);
		return eVO;
	}

	// update competition
	public CompetitionVO updateCompetition(CompetitionVO competitionVO) {
		setAdminEJB();
		IEntityCompetition competition = null;
		logger.fine("update competition" + competitionVO.getId());

		try {
			competition = admin.updateCompetition(
					competitionVO.getId(),
					competitionVO.getName(),
					competitionVO.getHigherLabelNumber(),
					competitionVO.getLastLabelNumber(),
					competitionVO.getLowerLabelNumber(),
					competitionVO.getStartingDate(),
					competitionVO.isTeamed());
		} catch (AdminException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}

		CompetitionVO cVO = Util.createCompetitionVO(competition);

		logger.fine("competition=" + cVO);
		return cVO;
	}

	// update category
	@SuppressWarnings("unchecked")
	public CategoryVO updateCategory(CategoryVO categoryVO) {
		setAdminEJB();
		IEntityCategory category = null;
		logger.fine("update category" + categoryVO.getId());

		List<IEntityCompetition> competitions = new ArrayList<IEntityCompetition>();
		if (categoryVO.getCompetitions() != null) {
			for (CompetitionVO competitionVO:categoryVO.getCompetitions()) {
				competitions.add(admin.findCompetitionFromId(competitionVO.getId()));
			}
		}
		try {
			category = admin.updateCategory(
					categoryVO.getId(),
					categoryVO.getName(),
					categoryVO.getSex(),
					categoryVO.getShortName(),
					categoryVO.getMinDate(),
					categoryVO.getMaxDate(),
					competitions);
		} catch (AdminException e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}

		CategoryVO cVO = Util.createCategoryVO(category);

		logger.fine("category=" + cVO);
		return cVO;
	}




	public void removeEvent(int id) {
		setAdminEJB();
		try {
			admin.removeEvent(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new FlexException(e.getMessage());
		}

	}

	public void removeCompetition(int id) {
		setAdminEJB();
		try {
			admin.removeCompetition(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new FlexException(e.getMessage());
		}

	}

	public void removeCategory(int id) {
		setAdminEJB();
		try {
			admin.removeCategory(id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new FlexException(e.getMessage());
		}

	}



	public List<EventVO> getAllEvents() {
		setAdminEJB();
		List<IEntityEvent> events = admin.findAllEvents();
		logger.fine("events=" + events);
		return Util.createEventListVO(events);
	}

	// get competitions related to the default event
	public List<CompetitionVO> getCompetitions() {
		setAdminEJB();
		List<IEntityCompetition> competitions = admin.findAllCompetitionsFromDefaultEvent();
		logger.fine("competitions=" + competitions);
		return Util.createCompetitionListVO(competitions);
	}

	// get startedCompetitions
	public List<CompetitionVO> getStartedCompetitions() {
		List<CompetitionVO> competitions = getCompetitions();
		List<CompetitionVO> startedCompetitions = new ArrayList<CompetitionVO>();

		for (CompetitionVO competition:competitions) {
			if (competition.getStartingDate() != null) {
				startedCompetitions.add(competition);
			}

		}
		logger.fine("startedCompetitions=" + startedCompetitions);
		return startedCompetitions;
	}

	public List<CompetitionVO> getAllCompetitions() {
		setAdminEJB();
		List<IEntityCompetition> competitions = admin.findAllCompetitions();
		logger.fine("competitions=" + competitions);
		return Util.createCompetitionListVO(competitions);
	}


	// get categories related to the default event
	public List<CategoryVO> getCategories() {

		setAdminEJB();
		List<IEntityCategory> categories = admin.findAllCategoriesFromDefaultEvent();
		logger.fine("categories=" + categories);
		return Util.createCategoryListVO(categories);
	}

	public List<CategoryVO> getAllCategories() {

		setAdminEJB();
		List<IEntityCategory> categories = admin.findAllCategories();
		logger.fine("categories=" + categories);
		return Util.createCategoryListVO(categories);
	}

	public String exportRegisteredAsFileName(final Date from) {
		setAdminEJB();
		String fileName = null;
		try  {
			fileName = admin.exportRegisteredAsFileName(from);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		return fileName;
	}

	public int importRegistered(final String fileName) {
		setAdminEJB();
		int ret;
		try  {
			ret = admin.importRegistered(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		return ret;
	}
}
