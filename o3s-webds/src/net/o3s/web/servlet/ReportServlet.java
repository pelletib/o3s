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
package net.o3s.web.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.o3s.apis.IEJBReportLocal;


public class ReportServlet extends HttpServlet {


	/**
	 * Logger
	 */
    private static Logger logger = Logger.getLogger(ReportServlet.class.getName());

    /**
     * Serial version number
     */
    private static final long serialVersionUID = 1L;

	private static final String PRM_TYPE = "type";
	private static final String PRM_REGISTERED_ID = "registeredId";
	private static final String PRM_COMPETITION_ID = "competitionId";
	private static final String PRM_CATEGORIES_ID = "categoriesId";
	private static final String PRM_CATEGORIES_ID_FIELD_SEPARATOR = ",";

	private static final String TYPE_SCRATCH_RANKING = "scratchRanking";
	private static final String TYPE_CATEGORIES_RANKING = "categoriesRanking";
	private static final String TYPE_CLUB_RANKING = "clubRanking";
	private static final String TYPE_LABEL = "label";

	@EJB
	private IEJBReportLocal report;

	private void setReportEJB() {

		InitialContext context=null;

		if (report == null) {
			logger.warning("EJB injection failed, report=null");
			try {
				context = new InitialContext();
				report = (IEJBReportLocal) context.lookup("net.o3s.beans.report.ReportBean_net.o3s.apis.IEJBReportLocal@Local");

			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

		setReportEJB();

		if (request.getParameter(PRM_TYPE) == null) {
			throw new IOException("parameter '"+ PRM_TYPE + "' is missing");
		}
		String type = request.getParameter(PRM_TYPE);

		OutputStream os = response.getOutputStream();

		if (type.equals(TYPE_SCRATCH_RANKING) == true) {
			try  {
				if (request.getParameter(PRM_COMPETITION_ID) == null) {
					throw new IOException("parameter '" + PRM_COMPETITION_ID + "' is missing");
				}

				int competitionId = Integer.valueOf(request.getParameter(PRM_COMPETITION_ID)).intValue();

				response.setContentType( "application/pdf" );
			    response.setHeader("Content-Disposition","attachment;filename=" + report.buildScratchFileName(competitionId));

			    report.getScratchRankingPdfAsByteArray(competitionId, os);

			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());


			} finally {
				os.flush();
				os.close();
			}

		} else if (type.equals(TYPE_CATEGORIES_RANKING) == true) {

			if (request.getParameter(PRM_COMPETITION_ID) == null) {
				throw new IOException("parameter '" + PRM_COMPETITION_ID + "' is missing");
			}

			int competitionId = Integer.valueOf(request.getParameter(PRM_COMPETITION_ID)).intValue();

			if (request.getParameter(PRM_CATEGORIES_ID) == null) {
				throw new IOException("parameter '" + PRM_CATEGORIES_ID + "' is missing");
			}

			String[] categoriesId = request.getParameter(PRM_CATEGORIES_ID).split(PRM_CATEGORIES_ID_FIELD_SEPARATOR);
			List<Integer> catList = new ArrayList<Integer>();
			for (String cat:categoriesId) {
				catList.add(Integer.valueOf(cat));
			}

			try  {
				response.setContentType( "application/pdf" );
				response.setHeader("Content-Disposition","attachment;filename=" + report.buildCategoryFileName(competitionId));
				report.getCategoryRankingPdfAsByteArray(competitionId, catList, os);
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			} finally {
				os.flush();
				os.close();
			}
		} else if (type.equals(TYPE_LABEL) == true) {
			if (request.getParameter(PRM_REGISTERED_ID) == null) {
				throw new IOException("parameter '" + PRM_REGISTERED_ID + "' is missing");
			}

			int registeredId = Integer.valueOf(request.getParameter(PRM_REGISTERED_ID)).intValue();

			try  {
				response.setContentType( "application/pdf" );
				response.setHeader("Content-Disposition","attachment;filename=" + report.buildLabelFileName(registeredId));
				report.getLabelPdfAsByteArray(registeredId, os);
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			} finally {
				os.flush();
				os.close();
			}
		} else if (type.equals(TYPE_CLUB_RANKING) == true) {
			try  {
				if (request.getParameter(PRM_COMPETITION_ID) == null) {
					throw new IOException("parameter '" + PRM_COMPETITION_ID + "' is missing");
				}

				int competitionId = Integer.valueOf(request.getParameter(PRM_COMPETITION_ID)).intValue();

				response.setContentType( "application/pdf" );
			    response.setHeader("Content-Disposition","attachment;filename=" + report.buildClubFileName(competitionId));

			    report.getClubRankingPdfAsByteArray(competitionId, os);

			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());


			} finally {
				os.flush();
				os.close();
			}

		} else {
			throw new IOException("unkown type for report servlet");
		}


	}
}
