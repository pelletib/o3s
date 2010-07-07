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

package net.o3s.apis;

import java.io.OutputStream;
import java.util.List;

public interface IEJBReportLocal {

	String getScratchRankingPdfAsFileName(final int competitionId) throws ReportException ;
	String getScratchRankingCsvAsFileName(final int competitionId) throws ReportException ;
	byte[] getScratchRankingPdfAsByteArray(final int competitionId) throws ReportException;
	void getScratchRankingPdfAsByteArray(final int competitionId, final OutputStream outputStream) throws ReportException;
	String buildScratchPdfFileName(final int competitionId)  throws ReportException;
	String buildScratchCsvFileName(final int competitionId)  throws ReportException;
	Boolean isNotEmptyScratchRanking(final int competitionId);

	String getCategoryRankingPdfAsFileName(final int competitionId) throws ReportException;
	String getCategoryRankingCsvAsFileName(final int competitionId) throws ReportException;
	void getCategoryRankingPdfAsByteArray(final int competitionId,  List<Integer> categoriesId, final OutputStream outputStream)  throws ReportException;
	byte[] getCategoryRankingPdfAsByteArray(final int competitionId, List<Integer> categoriesId) throws ReportException;
	String buildCategoryPdfFileName(final int competitionId)  throws ReportException;
	String buildCategoryCsvFileName(final int competitionId)  throws ReportException;
	Boolean isNotEmptyCategoryRanking(final int competitionId, List<Integer> categoriesId);

	String getClubRankingPdfAsFileName(final int competitionId) throws ReportException;
	String getClubRankingCsvAsFileName(final int competitionId) throws ReportException;
	void getClubRankingPdfAsByteArray(final int competitionId, final OutputStream outputStream)  throws ReportException;
	byte[] getClubRankingPdfAsByteArray(final int competitionId) throws ReportException;
	String buildClubPdfFileName(final int competitionId)  throws ReportException;
	String buildClubCsvFileName(final int competitionId)  throws ReportException;
	Boolean isNotEmptyClubRanking(final int competitionId);

	String getLabelPdfAsFileName(final int registeredId) throws ReportException;
	void getLabelPdfAsByteArray(final int registeredId, final OutputStream outputStream) throws ReportException;
	byte[] getLabelPdfAsByteArray(final int registeredId) throws ReportException;
	String buildLabelFileName(final int registeredId)  throws ReportException;
	void printLabel(final int registeredId, boolean withPrintDialog) throws ReportException;
}
