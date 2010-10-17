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
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.o3s.apis.IEJBAdminRemote;
import net.o3s.apis.IEJBReportRemote;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityEvent;
import net.o3s.apis.ReportException;
import net.o3s.web.vo.CategoryVO;
import net.o3s.web.vo.FlexException;


public class Reporting {

	//@EJB
	private IEJBReportRemote report;

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
	private void setReportEJB() {

		InitialContext context=null;

		if (report == null) {
			try {
				context = new InitialContext();
				report = (IEJBReportRemote) context.lookup("net.o3s.beans.report.ReportBean_net.o3s.apis.IEJBReportRemote@Remote");

			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public byte[] getScratchRankingPdfAsByteArray(final int competitionId) {
		setReportEJB();
		byte[] data = null;
		try  {
			data = report.getScratchRankingPdfAsByteArray(competitionId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		return data;
	}

	public String getCategoryRankingPdfAsFileName(final int competitionId) {
		setReportEJB();
		String fileName = null;
		try  {
			fileName = report.getCategoryRankingPdfAsFileName(competitionId, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		return fileName;
	}

	public String getCategoryRankingCsvAsFileName(final int competitionId) {
		setReportEJB();
		String fileName = null;
		try  {
			fileName = report.getCategoryRankingCsvAsFileName(competitionId, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		return fileName;
	}

	public String getClubRankingPdfAsFileName(final int competitionId) {
		setReportEJB();
		String fileName = null;
		try  {
			fileName = report.getClubRankingPdfAsFileName(competitionId, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		return fileName;
	}

	public String getClubRankingCsvAsFileName(final int competitionId) {
		setReportEJB();
		String fileName = null;
		try  {
			fileName = report.getClubRankingCsvAsFileName(competitionId, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		return fileName;
	}

	public String getScratchRankingPdfAsFileName(final int competitionId) {
		setReportEJB();
		String fileName = null;
		try  {
			fileName = report.getScratchRankingPdfAsFileName(competitionId, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		return fileName;
	}

	public String getScratchRankingCsvAsFileName(final int competitionId) {
		setReportEJB();
		String fileName = null;
		try  {
			fileName = report.getScratchRankingCsvAsFileName(competitionId, false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
		return fileName;
	}

	public Boolean isNotEmptyScratchRanking(final int competitionId) {
		setReportEJB();
		try  {
			return report.isNotEmptyScratchRanking(competitionId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
	}

	public Boolean isNotEmptyCategoryRanking(final int competitionId, List<CategoryVO> categoriesVO) {
		setReportEJB();
		List<Integer> categoriesId = new ArrayList<Integer>();
		for (CategoryVO categoryVO:categoriesVO) {
			categoriesId.add(categoryVO.getId());
		}
		try  {
			return report.isNotEmptyCategoryRanking(competitionId, categoriesId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
	}

	public Boolean isNotEmptyClubRanking(final int competitionId) {
		setReportEJB();
		try  {
			return report.isNotEmptyClubRanking(competitionId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FlexException(e.getMessage());
		}
	}

	public void printLabel(final int registeredId, boolean withPrintDialog) {
		setReportEJB();
		try  {
			report.printLabel(registeredId, withPrintDialog);
		} catch (Exception e) {
			e.printStackTrace();
			String cause = "";
			if (e.getCause() != null) {
				cause = ", " + e.getCause().getMessage();
			}
			throw new FlexException(e.getMessage() + cause);
		}
	}

	public String getAll(final int eventId) {

		setAdminEJB();
		setReportEJB();

    	List<IEntityCompetition> competitions = admin.findAllCompetitionsFromEvent(eventId);

    	String ret = "";

    	for (IEntityCompetition competition:competitions) {

    		try {
    			if (report.isNotEmptyScratchRanking(competition.getId())) {

					ret += report.getCategoryRankingPdfAsFileName(competition.getId(),true) + "\n";
					ret += report.getCategoryRankingCsvAsFileName(competition.getId(),true) + "\n";
					ret += report.getScratchRankingCsvAsFileName(competition.getId(),true) + "\n";
					ret += report.getScratchRankingPdfAsFileName(competition.getId(),true) + "\n";
    			}

    			if (report.isNotEmptyClubRanking(competition.getId())) {
    				ret += report.getClubRankingCsvAsFileName(competition.getId(),true) + "\n";
    				ret += report.getClubRankingPdfAsFileName(competition.getId(),true) + "\n";
    			}

    		} catch (Exception e) {
    			e.printStackTrace();
    			throw new FlexException(e.getMessage());
    		}
    	}

    	return ret;
	}
}

