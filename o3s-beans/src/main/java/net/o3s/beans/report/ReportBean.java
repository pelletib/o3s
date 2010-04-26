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
package net.o3s.beans.report;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.o3s.apis.IEJBAdminLocal;
import net.o3s.apis.IEJBRegisteringLocal;
import net.o3s.apis.IEJBReportLocal;
import net.o3s.apis.IEJBReportRemote;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.RegisteringException;
import net.o3s.apis.ReportException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.query.JRJpaQueryExecuterFactory;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * Session Bean implementation class AdminBean
 */
@Stateless
@Local(IEJBReportLocal.class)
@Remote(IEJBReportRemote.class)
public class ReportBean implements IEJBReportLocal,IEJBReportRemote {

    private static Logger logger = Logger.getLogger(ReportBean.class.getName());
    private static String SCRATCH_RANKING_REPORT="ScratchRankingReport";
    private static String CATEGORIES_RANKING_REPORT="CategoriesRankingReport";
    private static String LABEL_REPORT="LabelReport";


    @PersistenceContext
    private EntityManager entityManager;

    @EJB
    private IEJBAdminLocal admin;

    @EJB
    private IEJBRegisteringLocal registering;

    @SuppressWarnings("unchecked")
	private JasperPrint getJasperPrint(String sourceFileName, Map parameters, Collection collection) throws ReportException {

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InputStream sourceIs = cl.getResourceAsStream(sourceFileName);

		JasperDesign jasperDesign;
		try {
			jasperDesign = JRXmlLoader.load(sourceIs);
		} catch (JRException e1) {
			e1.printStackTrace();
			throw new ReportException("Unable to load the report ["
					+ sourceFileName + "]", e1);

		}
		JasperReport jasperReport = null;
		try {

			jasperReport = JasperCompileManager.compileReport(jasperDesign);

		} catch (JRException e) {
			e.printStackTrace();
			throw new ReportException("Unable to compile the report ["
					+ sourceFileName + "]", e);
		}
		logger.fine("Compiling ok");

		JasperPrint jasperPrint;
		try {
			jasperPrint = JasperFillManager.fillReport(jasperReport,
					parameters, new JRBeanCollectionDataSource(collection));
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ReportException("Unable to fill the report", e);

		}
		logger.fine("Filling ok");
		return jasperPrint;

	}

    @SuppressWarnings("unchecked")
	private void invokeJasperReportPdf(String sourceFileName, String pdfFileName,
			Map parameters, Collection collection) throws ReportException {

    	JasperPrint jasperPrint = getJasperPrint(sourceFileName, parameters, collection);

		try {
			JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFileName);
		} catch (JRException e) {
			e.printStackTrace();
			throw new ReportException(
					"Unable to export in pdf the ranking report", e);

		}

		logger.fine("Export ok");
	}

    @SuppressWarnings("unchecked")
	private void invokeJasperReportStream(String sourceFileName, OutputStream outputStream,
			Map parameters, Collection collection) throws ReportException {

    	JasperPrint jasperPrint = getJasperPrint(sourceFileName, parameters, collection);

		try {
			JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
		} catch (JRException e) {
			e.printStackTrace();
			throw new ReportException(
					"Unable to export in output stream the ranking report", e);

		}

		logger.fine("Export ok");
	}

    @SuppressWarnings("unchecked")
	private void printJasperReport(String sourceFileName, Map parameters, Collection collection, boolean withPrintDialog) throws ReportException {

    	JasperPrint jasperPrint = getJasperPrint(sourceFileName, parameters, collection);

		try {
			JasperPrintManager.printReport(jasperPrint, withPrintDialog);
		} catch (JRException e) {
			e.printStackTrace();
			throw new ReportException(
					"Unable to export in output stream the ranking report", e);

		}

		logger.fine("Print ok");
	}

    public String buildScratchFileName(final int competitionId)  throws ReportException{
    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }

        String pdfFileName = SCRATCH_RANKING_REPORT + "-" + competition.getName() + ".pdf";
		pdfFileName = pdfFileName.replace(' ', '_');

		return pdfFileName;

    }

    public Boolean isNotEmptyScratchRanking(final int competitionId) {
    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		logger.warning("competition is null");
    		return false;
    	}

     	List<IEntityRegistered> registereds;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByDuration(competitionId);
		} catch (RegisteringException re) {
			re.printStackTrace();
    		return false;
		}

    	if (registereds.isEmpty()) {
    		logger.warning("Not yet arrived");

    		return false;
        }

    	return true;
    }

    @SuppressWarnings("unchecked")
	public String getScratchRankingPdfAsFileName(final int competitionId) throws ReportException {

    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }

     	List<IEntityRegistered> registereds;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByDuration(competitionId);
		} catch (RegisteringException re) {
			re.printStackTrace();
    		throw new ReportException("Unable to generate report for competition " + competitionId, re);
		}

    	if (registereds.isEmpty()) {
    		throw new ReportException("No yet arrived!");
        }

    	// set parameters
		Map parameters = new HashMap();
		parameters.put(JRJpaQueryExecuterFactory.PARAMETER_JPA_ENTITY_MANAGER, entityManager);
		parameters.put("competitionId",Integer.valueOf(competitionId));
		parameters.put("competitionName", competition.getName());
		parameters.put("eventName", competition.getEvent().getName());

		logger.fine("eventName=" + competition.getEvent().getName());
		String sourceFileName = SCRATCH_RANKING_REPORT + ".jrxml";

        String pdfFileName = buildScratchFileName(competitionId);

		invokeJasperReportPdf(sourceFileName, pdfFileName, parameters, registereds);

        return pdfFileName;
    }

    @SuppressWarnings("unchecked")
	public byte[] getScratchRankingPdfAsByteArray(final int competitionId) throws ReportException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		getScratchRankingPdfAsByteArray(competitionId, outputStream);
	    return outputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
	public void getScratchRankingPdfAsByteArray(final int competitionId, final OutputStream outputStream) throws ReportException {

    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }

     	List<IEntityRegistered> registereds;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByDuration(competitionId);
		} catch (RegisteringException re) {
			re.printStackTrace();
    		throw new ReportException("Unable to generate report for competition " + competitionId, re);
		}

    	if (registereds.isEmpty()) {
    		throw new ReportException("No yet arrived!");
        }

    	// set parameters
		Map parameters = new HashMap();
		parameters.put(JRJpaQueryExecuterFactory.PARAMETER_JPA_ENTITY_MANAGER, entityManager);
		parameters.put("competitionId",Integer.valueOf(competitionId));
		parameters.put("competitionName", competition.getName());
		parameters.put("eventName", competition.getEvent().getName());

		logger.fine("eventName=" + competition.getEvent().getName());
		String sourceFileName = SCRATCH_RANKING_REPORT + ".jrxml";
		invokeJasperReportStream(sourceFileName, outputStream, parameters, registereds);
    }

    public Boolean isNotEmptyCategoryRanking(final int competitionId, List<Integer> categoriesId) {
    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		logger.warning("competition is null!");
    		return false;
        }

     	List<IEntityRegistered> registereds;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByCategoryAndDuration(competitionId);
		} catch (RegisteringException re) {
			re.printStackTrace();
    		return false;
		}

    	if (registereds.isEmpty()) {
    		logger.warning("No yet arrived!");
    		return false;
        }

    	if (categoriesId != null) {

    		// filter the registered according to the categories list
    		List<IEntityRegistered> filteredRegistereds = new ArrayList<IEntityRegistered>();

    		for (IEntityRegistered registered:registereds) {
    			for (Integer categoryId:categoriesId) {
    				if ( registered.getCategory().getId() == categoryId.intValue()) {
    					filteredRegistereds.add(registered);
    					break;
    				}
    			}
    		}

    		if (filteredRegistereds.isEmpty()) {
    			logger.warning("No yet arrived!");
    			return false;
    		}
    	}

    	return true;
    }

    @SuppressWarnings("unchecked")
	public byte[] getCategoryRankingPdfAsByteArray(final int competitionId, List<Integer> categoriesId) throws ReportException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		getCategoryRankingPdfAsByteArray(competitionId, categoriesId, outputStream);
	    return outputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
	public void getCategoryRankingPdfAsByteArray(final int competitionId,  List<Integer> categoriesId, final OutputStream outputStream) throws ReportException {

    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }

     	List<IEntityRegistered> registereds;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByCategoryAndDuration(competitionId);
		} catch (RegisteringException re) {
			re.printStackTrace();
    		throw new ReportException("Unable to generate report for competition " + competitionId, re);
		}

    	// filter the registered according to the categories list
    	List<IEntityRegistered> filteredRegistereds = new ArrayList<IEntityRegistered>();

    	for (IEntityRegistered registered:registereds) {
    		for (Integer categoryId:categoriesId) {
    			if ( registered.getCategory().getId() == categoryId.intValue()) {
    				filteredRegistereds.add(registered);
    				break;
    			}
    		}
    	}

    	if (filteredRegistereds.isEmpty()) {
    		throw new ReportException("No yet arrived!");
        }

    	// set parameters
		Map parameters = new HashMap();
		parameters.put(JRJpaQueryExecuterFactory.PARAMETER_JPA_ENTITY_MANAGER, entityManager);
		parameters.put("competitionId",Integer.valueOf(competitionId));
		parameters.put("competitionName", competition.getName());
		parameters.put("eventName", competition.getEvent().getName());
		logger.fine("eventName=" + competition.getEvent().getName());

		String sourceFileName = CATEGORIES_RANKING_REPORT + ".jrxml";
		invokeJasperReportStream(sourceFileName, outputStream, parameters, filteredRegistereds);

    }

    public String buildCategoryFileName(final int competitionId)  throws ReportException{
    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }
    	String pdfFileName = CATEGORIES_RANKING_REPORT + "-" + competition.getName() + ".pdf";

		pdfFileName = pdfFileName.replace(' ', '_');

		return pdfFileName;

    }

    @SuppressWarnings("unchecked")
	public String getCategoryRankingPdfAsFileName(final int competitionId) throws ReportException {

    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }

     	List<IEntityRegistered> registereds;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByCategoryAndDuration(competitionId);
		} catch (RegisteringException re) {
			re.printStackTrace();
    		throw new ReportException("Unable to generate report for competition " + competitionId, re);
		}

    	if (registereds.isEmpty()) {
    		throw new ReportException("No yet arrived!");
        }

    	// set parameters
		Map parameters = new HashMap();
		parameters.put(JRJpaQueryExecuterFactory.PARAMETER_JPA_ENTITY_MANAGER, entityManager);
		parameters.put("competitionId",Integer.valueOf(competitionId));
		parameters.put("competitionName", competition.getName());
		parameters.put("eventName", competition.getEvent().getName());
		logger.fine("eventName=" + competition.getEvent().getName());

		String sourceFileName = CATEGORIES_RANKING_REPORT + ".jrxml";

        String pdfFileName = buildCategoryFileName(competitionId);

		invokeJasperReportPdf(sourceFileName, pdfFileName, parameters, registereds);

        return pdfFileName;
    }

    @SuppressWarnings("unchecked")
	public String getLabelPdfAsFileName(final int registeredId) throws ReportException {

    	IEntityRegistered registered = null;

    	registered = registering.findRegisteredFromId(registeredId);

    	if (registered == null) {
    		throw new ReportException("Registered <" + registeredId + "> unknown");
        }


    	// set parameters
		Map parameters = new HashMap();
		parameters.put("eventName", registered.getEvent().getName());

		String sourceFileName = LABEL_REPORT + ".jrxml";

        String pdfFileName = buildLabelFileName(registeredId);

		List<IEntityRegistered> registereds = new ArrayList<IEntityRegistered>();
		registereds.add(registered);
		invokeJasperReportPdf(sourceFileName, pdfFileName, parameters, registereds);

        return pdfFileName;
    }

    public String buildLabelFileName(final int registeredId)  throws ReportException{
    	IEntityRegistered registered = null;

    	registered = registering.findRegisteredFromId(registeredId);

    	if (registered == null) {
    		throw new ReportException("Registered <" + registeredId + "> unknown");
        }
    	String pdfFileName = LABEL_REPORT + "-" + registered.getName() + ".pdf";
  		pdfFileName = pdfFileName.replace(' ', '_');

		return pdfFileName;

    }

    @SuppressWarnings("unchecked")
	public byte[] getLabelPdfAsByteArray(final int registeredId) throws ReportException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		getLabelPdfAsByteArray(registeredId, outputStream);
	    return outputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
	public void getLabelPdfAsByteArray(final int registeredId, final OutputStream outputStream) throws ReportException {

    	IEntityRegistered registered = null;

    	registered = registering.findRegisteredFromId(registeredId);

    	if (registered == null) {
    		throw new ReportException("Registered <" + registeredId + "> unknown");
        }


    	// set parameters
		Map parameters = new HashMap();
		parameters.put("eventName", registered.getEvent().getName());

		String sourceFileName = LABEL_REPORT + ".jrxml";

		List<IEntityRegistered> registereds = new ArrayList<IEntityRegistered>();
		registereds.add(registered);
		logger.fine("registered=" + registered);
		invokeJasperReportStream(sourceFileName, outputStream, parameters, registereds);
    }

    @SuppressWarnings("unchecked")
	public void printLabel(final int registeredId, boolean withPrintDialog) throws ReportException {

    	IEntityRegistered registered = null;

    	registered = registering.findRegisteredFromId(registeredId);

    	if (registered == null) {
    		throw new ReportException("Registered <" + registeredId + "> unknown");
        }


    	// set parameters
		Map parameters = new HashMap();
		parameters.put("eventName", registered.getEvent().getName());

		String sourceFileName = LABEL_REPORT + ".jrxml";

        String pdfFileName = buildLabelFileName(registeredId);

		List<IEntityRegistered> registereds = new ArrayList<IEntityRegistered>();
		registereds.add(registered);
		printJasperReport(sourceFileName, parameters, registereds, withPrintDialog);

    }

}
