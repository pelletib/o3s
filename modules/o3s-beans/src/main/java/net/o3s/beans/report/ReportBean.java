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
package net.o3s.beans.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterName;

import net.o3s.apis.IEJBAdminLocal;
import net.o3s.apis.IEJBRegisteringLocal;
import net.o3s.apis.IEJBReportLocal;
import net.o3s.apis.IEJBReportRemote;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityPerson;
import net.o3s.apis.IEntityRegistered;
import net.o3s.apis.RegisteringException;
import net.o3s.apis.ReportException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;
import net.sf.jasperreports.engine.query.JRJpaQueryExecuterFactory;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * Session Bean implementation class AdminBean
 */
@Stateless
@Local(IEJBReportLocal.class)
@Remote(IEJBReportRemote.class)
public class ReportBean implements IEJBReportLocal,IEJBReportRemote {

	/**
	 * Logger
	 */
    private static Logger logger = Logger.getLogger(ReportBean.class.getName());

    /**
     * Prefix for scratch ranking report
     */
    private static String SCRATCH_RANKING_REPORT="ScratchRankingReport";

    /**
     * Prefix for categories ranking report
     */
    private static String CATEGORIES_RANKING_REPORT="CategoriesRankingReport";

    /**
     * Prefix for club ranking report
     */
    private static String CLUB_RANKING_REPORT="ClubRankingReport";

    /**
     * Prefix for label report
     */
    private static String LABEL_REPORT="LabelReport";

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
     * Registering EJB
     */
    @EJB
    private IEJBRegisteringLocal registering;

    /**
     * Get a Jasper document (that can be viewed, printed, exported)
     * @param sourceFileName JRXML source file
     * @param parameters parameters for the JRXML file
     * @param collection datas for the JRXML file
     * @return Jasper document
     * @throws ReportException whenever an error occurs
     */
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

    /**
     * Generate a pdf file from the Jasper report description
     * @param sourceFileName JRXML source file
     * @param pdfFileName target file name
     * @param parameters parameters for the JRXML file
     * @param collection datas for the JRXML file
     * @throws ReportException whenever an error occurs (compile, ...)
     */
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

    /**
     * Generate a csv file from the Jasper report description
     * @param sourceFileName JRXML source file
     * @param csvFileName target file name
     * @param parameters parameters for the JRXML file
     * @param collection datas for the JRXML file
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	private void invokeJasperReportCsv(String sourceFileName, String csvFileName,
			Map parameters, Collection collection) throws ReportException {

    	JasperPrint jasperPrint = getJasperPrint(sourceFileName, parameters, collection);

    	JRCsvExporter exporterCSV = new JRCsvExporter();
    	exporterCSV.setParameter(JRCsvExporterParameter.JASPER_PRINT, jasperPrint);
    	exporterCSV.setParameter(JRCsvExporterParameter.OUTPUT_FILE_NAME, csvFileName);
    	exporterCSV.setParameter(JRCsvExporterParameter.IGNORE_PAGE_MARGINS, true);

		try {
	    	exporterCSV.exportReport();
		} catch (JRException e) {
			e.printStackTrace();
			throw new ReportException(
					"Unable to export in pdf the ranking report", e);

		}

		logger.fine("Export ok");
	}

    /**
     * Generate a pdf stream from the Jasper report description
     * @param sourceFileName JRXML source file
     * @param outputStream target output stream
     * @param parameters parameters for the JRXML file
     * @param collection datas for the JRXML file
     * @throws ReportException whenever an error occurs (compile, ...)
     */
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

    /**
     * Print a report
     * @param sourceFileName JRXML source file
     * @param parameters parameters for the JRXML file
     * @param collection datas for the JRXML file
     * @param withPrintDialog open or not the print dialog
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	private void printJasperReport(String sourceFileName, Map parameters, Collection collection, boolean withPrintDialog) throws ReportException {

    	JasperPrint jasperPrint = getJasperPrint(sourceFileName, parameters, collection);

		try {
			// Jasper native method
			//JasperPrintManager.printReport(jasperPrint, withPrintDialog);

			/*
			  PrintService[] serv = PrintServiceLookup.lookupPrintServices(null, null);
			  if (serv.length==0) {
				  logger.fine("no PrintService  found");
			  } else {
				  logger.fine("number of Services "+serv.length);
			     }


			     for (int i = 0; i<serv.length ;i++) {
			         PrintServiceAttributeSet psa = serv[i].getAttributes();
			         logger.fine("printer name "+(i+1)+" "+psa.get(PrinterName.class));
			         logger.fine("accepting "+psa.get(PrinterIsAcceptingJobs.class));
			     }

			 */


			// with more advanced option to adjust the printer configuration
			PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
			//printRequestAttributeSet.add(MediaSizeName.ISO_A4);
			//printRequestAttributeSet.add(OrientationRequested.PORTRAIT);

			PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
			//printServiceAttributeSet.add(new PrinterName("Epson Stylus 820 ESC/P 2", null));
			//printServiceAttributeSet.add(new PrinterName("hp LaserJet 1320 PCL 6", null));
			//printServiceAttributeSet.add(new PrinterName("PDFCreator", null));

			JRPrintServiceExporter exporter = new JRPrintServiceExporter();

			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
			exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
			exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
			exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, withPrintDialog);

			exporter.exportReport();

		} catch (JRException e) {
			e.printStackTrace();
			throw new ReportException(
					"Unable to export in output stream the ranking report", e);

		}

		logger.fine("Print ok");
	}

    /**
     * Build the file name for the scratch ranking pdf report
     * @param competitionId competition id
     * @return file name
     */
    public String buildScratchPdfFileName(final int competitionId)  throws ReportException{
    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }

        String pdfFileName = SCRATCH_RANKING_REPORT + "-" + competition.getName() + ".pdf";
		pdfFileName = pdfFileName.replace(' ', '_');

		return pdfFileName;
    }

    /**
     * Build the file name for the scratch ranking csv report
     * @param competitionId competition id
     * @return file name
     */
    public String buildScratchCsvFileName(final int competitionId)  throws ReportException{
    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }

        String csvFileName = SCRATCH_RANKING_REPORT + "-" + competition.getName() + ".csv";
        csvFileName = csvFileName.replace(' ', '_');

		return csvFileName;
    }

    /**
     * Test if the ranking is not empty
     * @param competitionId competition id
     * @return true is the ranking is not empty
     */
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

    /**
     * Build directory name according event name & competition name
     * @param eventName
     * @param competitionName
     * @return
     */
    private String buildDirectoryName(final String eventName, final String competitionName) {
    		String dirName = eventName + File.separator + competitionName;
    		dirName = dirName.replace(' ', '_');
    		File dirFile = new File(dirName);
    		try {
    		if (!dirFile.exists()) {
    			dirFile.mkdirs();
    		}
    		} catch (Exception e) {
    			logger.severe("Exception - " + e.getMessage());
    		}
    		return dirName;

    }

    /**
     * Generate a pdf report for the scratch ranking
     * @param competitionId competition id
	 * @return file name
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public String getScratchRankingPdfAsFileName(final int competitionId, final boolean inDirectory) throws ReportException {

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

        String pdfFileName = buildScratchPdfFileName(competitionId);

        if (inDirectory) {
        	pdfFileName = buildDirectoryName(competition.getEvent().getName(), competition.getName()) + File.separator + pdfFileName;
        }

		invokeJasperReportPdf(sourceFileName, pdfFileName, parameters, registereds);

        return pdfFileName;
    }

    /**
     * Generate a csv report for the scratch ranking
     * @param competitionId competition id
	 * @return file name
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public String getScratchRankingCsvAsFileName(final int competitionId, final boolean inDirectory) throws ReportException {

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
        String csvFileName = buildScratchCsvFileName(competitionId);

        if (inDirectory) {
        	csvFileName = buildDirectoryName(competition.getEvent().getName(), competition.getName()) + File.separator + csvFileName;
        }

		invokeJasperReportCsv(sourceFileName, csvFileName, parameters, registereds);

        return csvFileName;
    }

    /**
     * Generate a byte array report for the scratch ranking
     * @param competitionId competition id
	 * @return byte array
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public byte[] getScratchRankingPdfAsByteArray(final int competitionId) throws ReportException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		getScratchRankingPdfAsByteArray(competitionId, outputStream);
	    return outputStream.toByteArray();
    }

    /**
     * Generate a byte array report for the scratch ranking
     * @param competitionId competition id
	 * @param output stream
     * @throws ReportException whenever an error occurs (compile, ...)
     */

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

    /**
     * Test if the category ranking is not empty
     * @param competitionId competition id
     * @param categoryId category id
     * @return true is the ranking is not empty
     */
    public Boolean isNotEmptyCategoryRanking(final int competitionId, List<Integer> categoriesId) {
    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		logger.warning("competition is null!");
    		return false;
        }

     	List<IEntityRegistered> registereds;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByCategoryAndDuration(competitionId, categoriesId);
		} catch (RegisteringException re) {
			re.printStackTrace();
    		return false;
		}

    	if (registereds.isEmpty()) {
    		logger.warning("No yet arrived!");
    		return false;
        }

    	return true;
    }

    /**
     * Test if the club ranking is not empty
     * @param competitionId competition id
     * @return true is the ranking is not empty
     */
    public Boolean isNotEmptyClubRanking(final int competitionId) {
    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		logger.warning("competition is null!");
    		return false;
        }

     	List<IEntityRegistered> registereds;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByClubAndDuration(competitionId);
		} catch (RegisteringException re) {
			re.printStackTrace();
    		return false;
		}

    	if (registereds.isEmpty()) {
    		logger.warning("No yet arrived!");
    		return false;
        }

    	return true;
    }

    /**
     * Generate a byte array report for the category ranking
     * @param competitionId competition id
     * @param categoryId category id
	 * @return byte array
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public byte[] getCategoryRankingPdfAsByteArray(final int competitionId, List<Integer> categoriesId) throws ReportException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		getCategoryRankingPdfAsByteArray(competitionId, categoriesId, outputStream);
	    return outputStream.toByteArray();
    }

    /**
     * Generate a byte array report for the club ranking
     * @param competitionId competition id
	 * @return byte array
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public byte[] getClubRankingPdfAsByteArray(final int competitionId) throws ReportException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		getClubRankingPdfAsByteArray(competitionId, outputStream);
	    return outputStream.toByteArray();
    }

    /**
     * Generate a byte array report for the category ranking
     * @param competitionId competition id
     * @param categoryId category id
	 * @param output stream
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public void getCategoryRankingPdfAsByteArray(final int competitionId,  List<Integer> categoriesId, final OutputStream outputStream) throws ReportException {

    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }

     	List<IEntityRegistered> registereds;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByCategoryAndDuration(competitionId, categoriesId);
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
		invokeJasperReportStream(sourceFileName, outputStream, parameters, registereds);

    }

    /**
     * Generate a byte array report for the club ranking
     * @param competitionId competition id
	 * @param output stream
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public void getClubRankingPdfAsByteArray(final int competitionId, final OutputStream outputStream) throws ReportException {

    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }

     	List<IEntityRegistered> registereds;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByClubAndDuration(competitionId);
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

		String sourceFileName = CLUB_RANKING_REPORT + ".jrxml";
		invokeJasperReportStream(sourceFileName, outputStream, parameters, registereds);

    }

    /**
     * Generate the file name for a category ranking pdf report
     * @param competitionId competition
     */
    public String buildCategoryPdfFileName(final int competitionId)  throws ReportException{
    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }
    	String pdfFileName = CATEGORIES_RANKING_REPORT + "-" + competition.getName() + ".pdf";

		pdfFileName = pdfFileName.replace(' ', '_');

		return pdfFileName;

    }

    /**
     * Generate the file name for a category ranking csv report
     * @param competitionId competition
     */
    public String buildCategoryCsvFileName(final int competitionId)  throws ReportException{
    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }
    	String csvFileName = CATEGORIES_RANKING_REPORT + "-" + competition.getName() + ".csv";

    	csvFileName = csvFileName.replace(' ', '_');

		return csvFileName;

    }

    /**
     * Generate the file name for a club ranking pdf report
     * @param competitionId competition
     */
    public String buildClubPdfFileName(final int competitionId)  throws ReportException{
    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }
    	String pdfFileName = CLUB_RANKING_REPORT + "-" + competition.getName() + ".pdf";

		pdfFileName = pdfFileName.replace(' ', '_');

		return pdfFileName;

    }

    /**
     * Generate the file name for a club ranking csv report
     * @param competitionId competition
     */
    public String buildClubCsvFileName(final int competitionId)  throws ReportException{
    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }
    	String csvFileName = CLUB_RANKING_REPORT + "-" + competition.getName() + ".csv";

    	csvFileName = csvFileName.replace(' ', '_');

		return csvFileName;

    }

    /**
     * Generate a pdf report for the category ranking
     * @param competitionId competition id
	 * @return file name
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public String getCategoryRankingPdfAsFileName(final int competitionId, final boolean inDirectory) throws ReportException {

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

        String pdfFileName = buildCategoryPdfFileName(competitionId);

        if (inDirectory) {
        	pdfFileName = buildDirectoryName(competition.getEvent().getName(), competition.getName()) + File.separator + pdfFileName;
        }


		invokeJasperReportPdf(sourceFileName, pdfFileName, parameters, registereds);

        return pdfFileName;
    }

    /**
     * Generate a csv report for the category ranking
     * @param competitionId competition id
	 * @return file name
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public String getCategoryRankingCsvAsFileName(final int competitionId, final boolean inDirectory) throws ReportException {

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

        String csvFileName = buildCategoryCsvFileName(competitionId);

        if (inDirectory) {
        	csvFileName = buildDirectoryName(competition.getEvent().getName(), competition.getName()) + File.separator + csvFileName;
        }

		invokeJasperReportCsv(sourceFileName, csvFileName, parameters, registereds);

        return csvFileName;
    }

    /**
     * Generate a pdf report for the club ranking
     * @param competitionId competition id
	 * @return file name
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public String getClubRankingPdfAsFileName(final int competitionId, final boolean inDirectory) throws ReportException {

    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }

     	List<IEntityRegistered> registereds;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByClubAndDuration(competitionId);
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

		String sourceFileName = CLUB_RANKING_REPORT + ".jrxml";

        String pdfFileName = buildClubPdfFileName(competitionId);

        if (inDirectory) {
        	pdfFileName = buildDirectoryName(competition.getEvent().getName(), competition.getName()) + File.separator + pdfFileName;
        }


		invokeJasperReportPdf(sourceFileName, pdfFileName, parameters, registereds);

        return pdfFileName;
    }

    /**
     * Generate a csv report for the club ranking
     * @param competitionId competition id
	 * @return file name
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public String getClubRankingCsvAsFileName(final int competitionId, final boolean inDirectory) throws ReportException {

    	IEntityCompetition competition = null;

    	competition = admin.findCompetitionFromId(competitionId);

    	if (competition == null) {
    		throw new ReportException("Competition <" + competitionId + "> unknown");
        }

     	List<IEntityRegistered> registereds;
		try {
			registereds = this.registering.findRegisteredFromCompetitionOrderByClubAndDuration(competitionId);
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

		String sourceFileName = CLUB_RANKING_REPORT + ".jrxml";

        String csvFileName = buildClubCsvFileName(competitionId);
        if (inDirectory) {
        	csvFileName = buildDirectoryName(competition.getEvent().getName(), competition.getName()) + File.separator + csvFileName;
        }

		invokeJasperReportCsv(sourceFileName, csvFileName, parameters, registereds);

        return csvFileName;
    }

    /**
     * Generate a pdf report for the label
     * @param registeredId registered id
	 * @return file name
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public String getLabelPdfAsFileName(final int registeredId) throws ReportException {

    	IEntityRegistered registered = null;

    	registered = registering.findRegisteredFromId(registeredId);

    	if (registered == null) {
    		throw new ReportException("Registered <" + registeredId + "> unknown");
        }

    	// set parameters
		Map parameters = getParameters4LabelReport(registered);

		String sourceFileName = LABEL_REPORT + ".jrxml";

        String pdfFileName = buildLabelFileName(registeredId);

		List<IEntityRegistered> registereds = new ArrayList<IEntityRegistered>();
		registereds.add(registered);
		invokeJasperReportPdf(sourceFileName, pdfFileName, parameters, registereds);

        return pdfFileName;
    }

    /**
     * Generate the label file name
     * @param registeredId registered id
     * @return file name
     */
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

    /**
     * Generate a byte array report for the label
     * @param registeredId registered id
	 * @return byte array
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public byte[] getLabelPdfAsByteArray(final int registeredId) throws ReportException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		getLabelPdfAsByteArray(registeredId, outputStream);
	    return outputStream.toByteArray();
    }

    /**
     * Generate a byte array report for the label
     * @param registeredId registered id
	 * @param output stream
	 * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public void getLabelPdfAsByteArray(final int registeredId, final OutputStream outputStream) throws ReportException {

    	IEntityRegistered registered = null;

    	registered = registering.findRegisteredFromId(registeredId);

    	if (registered == null) {
    		throw new ReportException("Registered <" + registeredId + "> unknown");
        }


    	// set parameters
		Map parameters = getParameters4LabelReport(registered);

		String sourceFileName = LABEL_REPORT + ".jrxml";

		List<IEntityRegistered> registereds = new ArrayList<IEntityRegistered>();
		registereds.add(registered);
		logger.fine("registered=" + registered);
		invokeJasperReportStream(sourceFileName, outputStream, parameters, registereds);
    }

    @SuppressWarnings("unchecked")
    private Map getParameters4LabelReport(IEntityRegistered registered){
    	Map parameters = new HashMap();
		parameters.put("eventName", registered.getEvent().getName());
		if (registered.isPaid()) {
			parameters.put("paid", "OUI");
		} else {
			parameters.put("paid", "NON");
		}
		if (registered.isProvidedHealthForm()) {
			parameters.put("providedHealthForm", "OUI");
		} else {
			parameters.put("providedHealthForm", "NON");
		}

    	DateFormat dfyyyyMMdd = new SimpleDateFormat("dd/MM/yyyy");

		if (!registered.isTeamed()) {
			for (IEntityPerson person:registered.getPersons()) {
				parameters.put("sex",(person.getSex()=='M'?"M":"F"));
				parameters.put("birthday",dfyyyyMMdd.format(person.getBirthday()));
				parameters.put("license",person.getLicense());
				parameters.put("club",person.getClub());
				break;
			}
		} else {
			parameters.put("sex","N/A");
			parameters.put("birthday","N/A");
			parameters.put("license","N/A");
			parameters.put("club","N/A");
		}
		return parameters;
    }

    /**
     * Print a label report
     * @param registeredId registered id
	 * @param withPrintDialog open or not the printing dialog
     * @throws ReportException whenever an error occurs (compile, ...)
     */
    @SuppressWarnings("unchecked")
	public void printLabel(final int registeredId, boolean withPrintDialog) throws ReportException {

    	IEntityRegistered registered = null;

    	registered = registering.findRegisteredFromId(registeredId);

    	if (registered == null) {
    		throw new ReportException("Registered <" + registeredId + "> unknown");
        }

    	// set parameters
		Map parameters = getParameters4LabelReport(registered);

		String sourceFileName = LABEL_REPORT + ".jrxml";

        String pdfFileName = buildLabelFileName(registeredId);

		List<IEntityRegistered> registereds = new ArrayList<IEntityRegistered>();
		registereds.add(registered);
		printJasperReport(sourceFileName, parameters, registereds, withPrintDialog);

    }

}
