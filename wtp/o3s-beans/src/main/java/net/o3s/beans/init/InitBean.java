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
package net.o3s.beans.init;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import net.o3s.apis.IEJBAdminLocal;
import net.o3s.apis.IEJBInitRemote;
import net.o3s.apis.IEntityCompetition;
import net.o3s.apis.IEntityEvent;


/**
 * Session Bean implementation class InitBean
 */
@Stateless
@Remote(IEJBInitRemote.class)
public class InitBean implements IEJBInitRemote {

    @EJB
    private IEJBAdminLocal admin;

    // Events
    private static final String EVENT_NAME_0 = "Foulees de Crossey 2010";
    private static final String EVENT_DATE_0 = "17/10/2010";
    private static final String EVENT_IMAGE_0 = "logo_cross_crossey.png";

    // Competitions
    private static final String NOCOMPETITION_NAME = "Unknown";

    private static final String COMPETITION_NAME_0_1 = "course 0.5 km [2005]";
    private static final String COMPETITION_NAME_0_2 = "course 0.5 km [2006]";
    private static final String COMPETITION_NAME_0_3 = "course 0.5 km [2007]";
    private static final String COMPETITION_NAME_1_1 = "course 1 km [2001]";
    private static final String COMPETITION_NAME_1_2 = "course 1 km [2002]";
    private static final String COMPETITION_NAME_1_3 = "course 1 km [2003]";
    private static final String COMPETITION_NAME_1_4 = "course 1 km [2004]";
    private static final String COMPETITION_NAME_2_1 = "course 1.2 km [1999]";
    private static final String COMPETITION_NAME_2_2 = "course 1.2 km [2000]";
    private static final String COMPETITION_NAME_3 = "course 2.5 km";
    private static final String COMPETITION_NAME_4 = "course 3.5 km";
    private static final String COMPETITION_NAME_5 = "course 7.5 km";
    private static final String COMPETITION_NAME_6 = "course 12 km";
    private static final String COMPETITION_NAME_7 = "course 21 km";
    private static final String COMPETITION_NAME_8 = "parcours decouverte (3.5 km)";

    private static final int NOCOMPETITION_LOWER_LABEL_NB = 0000;
    private static final int NOCOMPETITION_HIGHER_LABEL_NB = 9999;
    private static final int COMPETITION_LOWER_LABEL_NB_0_1 = 1000;
    private static final int COMPETITION_HIGHER_LABEL_NB_0_1 = 1299;
    private static final int COMPETITION_LOWER_LABEL_NB_0_2 = 1300;
    private static final int COMPETITION_HIGHER_LABEL_NB_0_2 = 1599;
    private static final int COMPETITION_LOWER_LABEL_NB_0_3 = 1600;
    private static final int COMPETITION_HIGHER_LABEL_NB_0_3 = 1999;
    private static final int COMPETITION_LOWER_LABEL_NB_1_1 = 2000;
    private static final int COMPETITION_HIGHER_LABEL_NB_1_1 = 2199;
    private static final int COMPETITION_LOWER_LABEL_NB_1_2 = 2200;
    private static final int COMPETITION_HIGHER_LABEL_NB_1_2 = 2399;
    private static final int COMPETITION_LOWER_LABEL_NB_1_3 = 2400;
    private static final int COMPETITION_HIGHER_LABEL_NB_1_3 = 2599;
    private static final int COMPETITION_LOWER_LABEL_NB_1_4 = 2600;
    private static final int COMPETITION_HIGHER_LABEL_NB_1_4 = 2999;
    private static final int COMPETITION_LOWER_LABEL_NB_2_1 = 3000;
    private static final int COMPETITION_HIGHER_LABEL_NB_2_1 = 3499;
    private static final int COMPETITION_LOWER_LABEL_NB_2_2 = 3500;
    private static final int COMPETITION_HIGHER_LABEL_NB_2_2 = 3999;
    private static final int COMPETITION_LOWER_LABEL_NB_3 = 4000;
    private static final int COMPETITION_HIGHER_LABEL_NB_3 = 4999;
    private static final int COMPETITION_LOWER_LABEL_NB_4 = 5000;
    private static final int COMPETITION_HIGHER_LABEL_NB_4 = 5999;
    private static final int COMPETITION_LOWER_LABEL_NB_5 = 6000;
    private static final int COMPETITION_HIGHER_LABEL_NB_5 = 6999;
    private static final int COMPETITION_LOWER_LABEL_NB_6 = 7000;
    private static final int COMPETITION_HIGHER_LABEL_NB_6 = 7999;
    private static final int COMPETITION_LOWER_LABEL_NB_7 = 8000;
    private static final int COMPETITION_HIGHER_LABEL_NB_7 = 8999;
    private static final int COMPETITION_LOWER_LABEL_NB_8 = 9000;
    private static final int COMPETITION_HIGHER_LABEL_NB_8 = 9999;

    // Categories
    private static final String NOCATEGORY_NAME = "Unknown";
    private static final String NOCATEGORY_MINDATE = "01/01/2050";
    private static final String NOCATEGORY_MAXDATE = "31/12/2050";
    private static final char NOCATEGORY_SNAME = '?';

    private static final String CATEGORY_YEAR_0_1 = "2005";
    private static final String CATEGORY_NAME_0_1 = "maternelle-" + CATEGORY_YEAR_0_1;
    private static final String CATEGORY_MINDATE_0_1 = "01/01/2005";
    private static final String CATEGORY_MAXDATE_0_1 = "31/12/2005";
    private static final String CATEGORY_YEAR_0_2 = "2006";
    private static final String CATEGORY_NAME_0_2 = "maternelle-" + CATEGORY_YEAR_0_2;
    private static final String CATEGORY_MINDATE_0_2 = "01/01/2006";
    private static final String CATEGORY_MAXDATE_0_2 = "31/12/2006";
    private static final String CATEGORY_YEAR_0_3 = "2007";
    private static final String CATEGORY_NAME_0_3 = "maternelle-" + CATEGORY_YEAR_0_3;
    private static final String CATEGORY_MINDATE_0_3 = "01/01/2007";
    private static final String CATEGORY_MAXDATE_0_3 = "31/12/2007";
    private static final char CATEGORY_SNAME_0 = 'T';
    private static final String CATEGORY_YEAR_1_1 = "2001";
    private static final String CATEGORY_NAME_1_1 = "mini poussin-" + CATEGORY_YEAR_1_1;
    private static final String CATEGORY_MINDATE_1_1 = "01/01/2001";
    private static final String CATEGORY_MAXDATE_1_1 = "31/12/2001";
    private static final String CATEGORY_YEAR_1_2 = "2002";
    private static final String CATEGORY_NAME_1_2 = "mini poussin-" + CATEGORY_YEAR_1_2;
    private static final String CATEGORY_MINDATE_1_2 = "01/01/2002";
    private static final String CATEGORY_MAXDATE_1_2 = "31/12/2002";
    private static final String CATEGORY_YEAR_1_3 = "2003";
    private static final String CATEGORY_NAME_1_3 = "mini poussin-" + CATEGORY_YEAR_1_3;
    private static final String CATEGORY_MINDATE_1_3 = "01/01/2003";
    private static final String CATEGORY_MAXDATE_1_3 = "31/12/2003";
    private static final String CATEGORY_YEAR_1_4 = "2004";
    private static final String CATEGORY_NAME_1_4 = "mini poussin-" + CATEGORY_YEAR_1_4;
    private static final String CATEGORY_MINDATE_1_4 = "01/01/2004";
    private static final String CATEGORY_MAXDATE_1_4 = "31/12/2004";
    private static final char CATEGORY_SNAME_1 = 'T';
    private static final String CATEGORY_YEAR_2_1 = "1999";
    private static final String CATEGORY_NAME_2_1 = "poussin-" + CATEGORY_YEAR_2_1;
    private static final String CATEGORY_MINDATE_2_1 = "01/01/1999";
    private static final String CATEGORY_MAXDATE_2_1 = "31/12/1999";
    private static final String CATEGORY_YEAR_2_2 = "2000";
    private static final String CATEGORY_NAME_2_2 = "poussin-" + CATEGORY_YEAR_2_2;
    private static final String CATEGORY_MINDATE_2_2 = "01/01/2000";
    private static final String CATEGORY_MAXDATE_2_2 = "31/12/2000";
    private static final char CATEGORY_SNAME_2 = 'V';
    private static final String CATEGORY_NAME_3 = "benjamin";
    private static final String CATEGORY_MINDATE_3 = "01/01/1997";
    private static final String CATEGORY_MAXDATE_3 = "31/12/1998";
    private static final char CATEGORY_SNAME_3 = 'Y';
    private static final String CATEGORY_NAME_4 = "minime";
    private static final String CATEGORY_MINDATE_4 = "01/01/1995";
    private static final String CATEGORY_MAXDATE_4 = "31/12/1996";
    private static final char CATEGORY_SNAME_4 = 'A';
    private static final String CATEGORY_NAME_5 = "cadet";
    private static final String CATEGORY_MINDATE_5 = "01/01/1993";
    private static final String CATEGORY_MAXDATE_5 = "31/12/1994";
    private static final char CATEGORY_SNAME_5 = 'B';
    private static final String CATEGORY_NAME_6 = "junior";
    private static final String CATEGORY_MINDATE_6 = "01/01/1991";
    private static final String CATEGORY_MAXDATE_6 = "31/12/1992";
    private static final char CATEGORY_SNAME_6 = 'C';
    private static final String CATEGORY_NAME_7 = "espoir";
    private static final String CATEGORY_MINDATE_7 = "01/01/1988";
    private static final String CATEGORY_MAXDATE_7 = "31/12/1990";
    private static final char CATEGORY_SNAME_7 = 'D';
    private static final String CATEGORY_NAME_8 = "senior";
    private static final String CATEGORY_MINDATE_8 = "01/01/1971";
    private static final String CATEGORY_MAXDATE_8 = "31/12/1987";
    private static final char CATEGORY_SNAME_8 = 'E';
    private static final String CATEGORY_NAME_9 = "veteran 1";
    private static final String CATEGORY_MINDATE_9 = "01/01/1961";
    private static final String CATEGORY_MAXDATE_9 = "31/12/1970";
    private static final char CATEGORY_SNAME_9 = 'F';
    private static final String CATEGORY_NAME_10 = "veteran 2";
    private static final String CATEGORY_MINDATE_10 = "01/01/1951";
    private static final String CATEGORY_MAXDATE_10 = "31/12/1960";
    private static final char CATEGORY_SNAME_10 = 'G';
    private static final String CATEGORY_NAME_11 = "veteran 3";
    private static final String CATEGORY_MINDATE_11 = "01/01/1941";
    private static final String CATEGORY_MAXDATE_11 = "31/12/1950";
    private static final char CATEGORY_SNAME_11 = 'H';
    private static final String CATEGORY_NAME_12 = "veteran 4";
    private static final String CATEGORY_MINDATE_12 = "01/01/1928";
    private static final String CATEGORY_MAXDATE_12 = "31/12/1940";
    private static final char CATEGORY_SNAME_12 = 'I';

    private static final String CATEGORY_NAME_20_1 = "maternelle-" + CATEGORY_YEAR_0_1;
    private static final String CATEGORY_MINDATE_20_1 = CATEGORY_MINDATE_0_1;
    private static final String CATEGORY_MAXDATE_20_1 = CATEGORY_MAXDATE_0_1;
    private static final String CATEGORY_NAME_20_2 = "maternelle-" + CATEGORY_YEAR_0_2;
    private static final String CATEGORY_MINDATE_20_2 = CATEGORY_MINDATE_0_2;
    private static final String CATEGORY_MAXDATE_20_2 = CATEGORY_MAXDATE_0_2;
    private static final String CATEGORY_NAME_20_3 = "maternelle-" + CATEGORY_YEAR_0_3;
    private static final String CATEGORY_MINDATE_20_3 = CATEGORY_MINDATE_0_3;
    private static final String CATEGORY_MAXDATE_20_3 = CATEGORY_MAXDATE_0_3;
    private static final char CATEGORY_SNAME_20 = 'S';
    private static final String CATEGORY_NAME_21_1 = "mini poussine-" + CATEGORY_YEAR_1_1;
    private static final String CATEGORY_MINDATE_21_1 = CATEGORY_MINDATE_1_1;
    private static final String CATEGORY_MAXDATE_21_1 = CATEGORY_MAXDATE_1_1;
    private static final String CATEGORY_NAME_21_2 = "mini poussine-" + CATEGORY_YEAR_1_2;
    private static final String CATEGORY_MINDATE_21_2 = CATEGORY_MINDATE_1_2;
    private static final String CATEGORY_MAXDATE_21_2 = CATEGORY_MAXDATE_1_2;
    private static final String CATEGORY_NAME_21_3 = "mini poussine-" + CATEGORY_YEAR_1_3;
    private static final String CATEGORY_MINDATE_21_3 = CATEGORY_MINDATE_1_3;
    private static final String CATEGORY_MAXDATE_21_3 = CATEGORY_MAXDATE_1_3;
    private static final String CATEGORY_NAME_21_4 = "mini poussine-" + CATEGORY_YEAR_1_4;
    private static final String CATEGORY_MINDATE_21_4 = CATEGORY_MINDATE_1_4;
    private static final String CATEGORY_MAXDATE_21_4 = CATEGORY_MAXDATE_1_4;
    private static final char CATEGORY_SNAME_21 = 'S';
    private static final String CATEGORY_NAME_22_1 = "poussine-" + CATEGORY_YEAR_2_1;
    private static final String CATEGORY_MINDATE_22_1 = CATEGORY_MINDATE_2_1;
    private static final String CATEGORY_MAXDATE_22_1 = CATEGORY_MAXDATE_2_1;
    private static final String CATEGORY_NAME_22_2 = "poussine-" + CATEGORY_YEAR_2_2;
    private static final String CATEGORY_MINDATE_22_2 = CATEGORY_MINDATE_2_2;
    private static final String CATEGORY_MAXDATE_22_2 = CATEGORY_MAXDATE_2_2;
    private static final char CATEGORY_SNAME_22 = 'U';
    private static final String CATEGORY_NAME_23 = "benjamine";
    private static final String CATEGORY_MINDATE_23 = CATEGORY_MINDATE_3;
    private static final String CATEGORY_MAXDATE_23 = CATEGORY_MAXDATE_3;
    private static final char CATEGORY_SNAME_23 = 'X';
    private static final String CATEGORY_NAME_24 = "minime";
    private static final String CATEGORY_MINDATE_24 = CATEGORY_MINDATE_4;
    private static final String CATEGORY_MAXDATE_24 = CATEGORY_MAXDATE_4;
    private static final char CATEGORY_SNAME_24 = 'J';
    private static final String CATEGORY_NAME_25 = "cadette";
    private static final String CATEGORY_MINDATE_25 = CATEGORY_MINDATE_5;
    private static final String CATEGORY_MAXDATE_25 = CATEGORY_MAXDATE_5;
    private static final char CATEGORY_SNAME_25 = 'K';
    private static final String CATEGORY_NAME_26 = "junior";
    private static final String CATEGORY_MINDATE_26 = CATEGORY_MINDATE_6;
    private static final String CATEGORY_MAXDATE_26 = CATEGORY_MAXDATE_6;
    private static final char CATEGORY_SNAME_26 = 'L';
    private static final String CATEGORY_NAME_27 = "espoir";
    private static final String CATEGORY_MINDATE_27 = CATEGORY_MINDATE_7;
    private static final String CATEGORY_MAXDATE_27 = CATEGORY_MAXDATE_7;
    private static final char CATEGORY_SNAME_27 = 'M';
    private static final String CATEGORY_NAME_28 = "senior";
    private static final String CATEGORY_MINDATE_28 = CATEGORY_MINDATE_8;
    private static final String CATEGORY_MAXDATE_28 = CATEGORY_MAXDATE_8;
    private static final char CATEGORY_SNAME_28 = 'N';
    private static final String CATEGORY_NAME_29 = "veteran 1";
    private static final String CATEGORY_MINDATE_29 = CATEGORY_MINDATE_9;
    private static final String CATEGORY_MAXDATE_29 = CATEGORY_MAXDATE_9;
    private static final char CATEGORY_SNAME_29 = 'O';
    private static final String CATEGORY_NAME_30 = "veteran 2";
    private static final String CATEGORY_MINDATE_30 = CATEGORY_MINDATE_10;
    private static final String CATEGORY_MAXDATE_30 = CATEGORY_MAXDATE_10;
    private static final char CATEGORY_SNAME_30 = 'O';
    private static final String CATEGORY_NAME_31 = "veteran 3";
    private static final String CATEGORY_MINDATE_31 = CATEGORY_MINDATE_11;
    private static final String CATEGORY_MAXDATE_31 = CATEGORY_MAXDATE_11;
    private static final char CATEGORY_SNAME_31 = 'O';
    private static final String CATEGORY_NAME_32 = "veteran 4";
    private static final String CATEGORY_MINDATE_32 = CATEGORY_MINDATE_12;
    private static final String CATEGORY_MAXDATE_32 = CATEGORY_MAXDATE_12;
    private static final char CATEGORY_SNAME_32 = 'O';


    public void init() {

    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        // Init Events
    	IEntityEvent event = null;
		try {
			event = admin.createEvent(EVENT_NAME_0, df.parse(EVENT_DATE_0), EVENT_IMAGE_0 );
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        // Init Competitions
    	IEntityCompetition nocompetition = admin.createCompetition(NOCOMPETITION_NAME, NOCOMPETITION_LOWER_LABEL_NB, NOCOMPETITION_HIGHER_LABEL_NB, NOCOMPETITION_LOWER_LABEL_NB, event, false);

    	IEntityCompetition competition0_1 = admin.createCompetition(COMPETITION_NAME_0_1, COMPETITION_LOWER_LABEL_NB_0_1, COMPETITION_HIGHER_LABEL_NB_0_1, COMPETITION_LOWER_LABEL_NB_0_1, event, false);
    	IEntityCompetition competition0_2 = admin.createCompetition(COMPETITION_NAME_0_2, COMPETITION_LOWER_LABEL_NB_0_2, COMPETITION_HIGHER_LABEL_NB_0_2, COMPETITION_LOWER_LABEL_NB_0_2, event, false);
    	IEntityCompetition competition0_3 = admin.createCompetition(COMPETITION_NAME_0_3, COMPETITION_LOWER_LABEL_NB_0_3, COMPETITION_HIGHER_LABEL_NB_0_3, COMPETITION_LOWER_LABEL_NB_0_3, event, false);
    	IEntityCompetition competition1_1 = admin.createCompetition(COMPETITION_NAME_1_1, COMPETITION_LOWER_LABEL_NB_1_1, COMPETITION_HIGHER_LABEL_NB_1_1, COMPETITION_LOWER_LABEL_NB_1_1, event, false);
    	IEntityCompetition competition1_2 = admin.createCompetition(COMPETITION_NAME_1_2, COMPETITION_LOWER_LABEL_NB_1_2, COMPETITION_HIGHER_LABEL_NB_1_2, COMPETITION_LOWER_LABEL_NB_1_2, event, false);
    	IEntityCompetition competition1_3 = admin.createCompetition(COMPETITION_NAME_1_3, COMPETITION_LOWER_LABEL_NB_1_3, COMPETITION_HIGHER_LABEL_NB_1_3, COMPETITION_LOWER_LABEL_NB_1_3, event, false);
    	IEntityCompetition competition1_4 = admin.createCompetition(COMPETITION_NAME_1_4, COMPETITION_LOWER_LABEL_NB_1_4, COMPETITION_HIGHER_LABEL_NB_1_4, COMPETITION_LOWER_LABEL_NB_1_4, event, false);
    	IEntityCompetition competition2_1 = admin.createCompetition(COMPETITION_NAME_2_1, COMPETITION_LOWER_LABEL_NB_2_1, COMPETITION_HIGHER_LABEL_NB_2_1, COMPETITION_LOWER_LABEL_NB_2_1, event, false);
    	IEntityCompetition competition2_2 = admin.createCompetition(COMPETITION_NAME_2_2, COMPETITION_LOWER_LABEL_NB_2_2, COMPETITION_HIGHER_LABEL_NB_2_2, COMPETITION_LOWER_LABEL_NB_2_2, event, false);
    	IEntityCompetition competition3 = admin.createCompetition(COMPETITION_NAME_3, COMPETITION_LOWER_LABEL_NB_3, COMPETITION_HIGHER_LABEL_NB_3, COMPETITION_LOWER_LABEL_NB_3, event, false);
    	IEntityCompetition competition4 = admin.createCompetition(COMPETITION_NAME_4, COMPETITION_LOWER_LABEL_NB_4, COMPETITION_HIGHER_LABEL_NB_4, COMPETITION_LOWER_LABEL_NB_4, event, false);
    	IEntityCompetition competition5 = admin.createCompetition(COMPETITION_NAME_5, COMPETITION_LOWER_LABEL_NB_5, COMPETITION_HIGHER_LABEL_NB_5, COMPETITION_LOWER_LABEL_NB_5, event, false);
    	IEntityCompetition competition6 = admin.createCompetition(COMPETITION_NAME_6, COMPETITION_LOWER_LABEL_NB_6, COMPETITION_HIGHER_LABEL_NB_6, COMPETITION_LOWER_LABEL_NB_6, event, false);
    	IEntityCompetition competition7 = admin.createCompetition(COMPETITION_NAME_7, COMPETITION_LOWER_LABEL_NB_7, COMPETITION_HIGHER_LABEL_NB_7, COMPETITION_LOWER_LABEL_NB_7, event, false);
    	IEntityCompetition competition8 = admin.createCompetition(COMPETITION_NAME_8, COMPETITION_LOWER_LABEL_NB_8, COMPETITION_HIGHER_LABEL_NB_8, COMPETITION_LOWER_LABEL_NB_8, event, true);

        // Init Categories

    	try {
        admin.createCategory(NOCATEGORY_NAME, df.parse(NOCATEGORY_MINDATE), df.parse(NOCATEGORY_MAXDATE), 'M', NOCATEGORY_SNAME, event, nocompetition);

		admin.createCategory(CATEGORY_NAME_0_1, df.parse(CATEGORY_MINDATE_0_1), df.parse(CATEGORY_MAXDATE_0_1), 'M', CATEGORY_SNAME_0, event, competition0_1);
		admin.createCategory(CATEGORY_NAME_0_2, df.parse(CATEGORY_MINDATE_0_2), df.parse(CATEGORY_MAXDATE_0_2), 'M', CATEGORY_SNAME_0, event, competition0_2);
		admin.createCategory(CATEGORY_NAME_0_3, df.parse(CATEGORY_MINDATE_0_3), df.parse(CATEGORY_MAXDATE_0_3), 'M', CATEGORY_SNAME_0, event, competition0_3);
    	admin.createCategory(CATEGORY_NAME_1_1, df.parse(CATEGORY_MINDATE_1_1), df.parse(CATEGORY_MAXDATE_1_1), 'M', CATEGORY_SNAME_1, event, competition1_1);
    	admin.createCategory(CATEGORY_NAME_1_2, df.parse(CATEGORY_MINDATE_1_2), df.parse(CATEGORY_MAXDATE_1_2), 'M', CATEGORY_SNAME_1, event, competition1_2);
    	admin.createCategory(CATEGORY_NAME_1_3, df.parse(CATEGORY_MINDATE_1_3), df.parse(CATEGORY_MAXDATE_1_3), 'M', CATEGORY_SNAME_1, event, competition1_3);
    	admin.createCategory(CATEGORY_NAME_1_4, df.parse(CATEGORY_MINDATE_1_4), df.parse(CATEGORY_MAXDATE_1_4), 'M', CATEGORY_SNAME_1, event, competition1_4);
    	admin.createCategory(CATEGORY_NAME_2_1, df.parse(CATEGORY_MINDATE_2_1), df.parse(CATEGORY_MAXDATE_2_1), 'M', CATEGORY_SNAME_2, event, competition2_1);
    	admin.createCategory(CATEGORY_NAME_2_2, df.parse(CATEGORY_MINDATE_2_2), df.parse(CATEGORY_MAXDATE_2_2), 'M', CATEGORY_SNAME_2, event, competition2_2);
    	admin.createCategory(CATEGORY_NAME_3, df.parse(CATEGORY_MINDATE_3), df.parse(CATEGORY_MAXDATE_3), 'M', CATEGORY_SNAME_3, event, competition3);
    	admin.createCategory(CATEGORY_NAME_4, df.parse(CATEGORY_MINDATE_4), df.parse(CATEGORY_MAXDATE_4), 'M', CATEGORY_SNAME_4, event, competition4);
    	admin.createCategory(CATEGORY_NAME_5, df.parse(CATEGORY_MINDATE_5), df.parse(CATEGORY_MAXDATE_5), 'M', CATEGORY_SNAME_5, event, competition5);
    	admin.createCategory(CATEGORY_NAME_6, df.parse(CATEGORY_MINDATE_6), df.parse(CATEGORY_MAXDATE_6), 'M', CATEGORY_SNAME_6, event, competition5, competition6, competition7);
    	admin.createCategory(CATEGORY_NAME_7, df.parse(CATEGORY_MINDATE_7), df.parse(CATEGORY_MAXDATE_7), 'M', CATEGORY_SNAME_7, event, competition5, competition6, competition7);
    	admin.createCategory(CATEGORY_NAME_8, df.parse(CATEGORY_MINDATE_8), df.parse(CATEGORY_MAXDATE_8), 'M', CATEGORY_SNAME_8, event, competition5, competition6, competition7);
    	admin.createCategory(CATEGORY_NAME_9, df.parse(CATEGORY_MINDATE_9), df.parse(CATEGORY_MAXDATE_9), 'M', CATEGORY_SNAME_9, event, competition5, competition6, competition7);
    	admin.createCategory(CATEGORY_NAME_10, df.parse(CATEGORY_MINDATE_10), df.parse(CATEGORY_MAXDATE_10), 'M', CATEGORY_SNAME_10, event, competition5, competition6, competition7);
    	admin.createCategory(CATEGORY_NAME_11, df.parse(CATEGORY_MINDATE_11), df.parse(CATEGORY_MAXDATE_11), 'M', CATEGORY_SNAME_11, event, competition5, competition6, competition7);
    	admin.createCategory(CATEGORY_NAME_12, df.parse(CATEGORY_MINDATE_12), df.parse(CATEGORY_MAXDATE_12), 'M', CATEGORY_SNAME_12, event, competition5, competition6, competition7);

    	admin.createCategory(CATEGORY_NAME_20_1, df.parse(CATEGORY_MINDATE_20_1), df.parse(CATEGORY_MAXDATE_20_1), 'F', CATEGORY_SNAME_20, event, competition0_1);
    	admin.createCategory(CATEGORY_NAME_20_2, df.parse(CATEGORY_MINDATE_20_2), df.parse(CATEGORY_MAXDATE_20_2), 'F', CATEGORY_SNAME_20, event, competition0_2);
    	admin.createCategory(CATEGORY_NAME_20_3, df.parse(CATEGORY_MINDATE_20_3), df.parse(CATEGORY_MAXDATE_20_3), 'F', CATEGORY_SNAME_20, event, competition0_3);
    	admin.createCategory(CATEGORY_NAME_21_1, df.parse(CATEGORY_MINDATE_21_1), df.parse(CATEGORY_MAXDATE_21_1), 'F', CATEGORY_SNAME_21, event, competition1_1);
    	admin.createCategory(CATEGORY_NAME_21_2, df.parse(CATEGORY_MINDATE_21_2), df.parse(CATEGORY_MAXDATE_21_2), 'F', CATEGORY_SNAME_21, event, competition1_2);
    	admin.createCategory(CATEGORY_NAME_21_3, df.parse(CATEGORY_MINDATE_21_3), df.parse(CATEGORY_MAXDATE_21_3), 'F', CATEGORY_SNAME_21, event, competition1_3);
    	admin.createCategory(CATEGORY_NAME_21_4, df.parse(CATEGORY_MINDATE_21_4), df.parse(CATEGORY_MAXDATE_21_4), 'F', CATEGORY_SNAME_21, event, competition1_4);
    	admin.createCategory(CATEGORY_NAME_22_1, df.parse(CATEGORY_MINDATE_22_1), df.parse(CATEGORY_MAXDATE_22_1), 'F', CATEGORY_SNAME_22, event, competition2_1);
    	admin.createCategory(CATEGORY_NAME_22_2, df.parse(CATEGORY_MINDATE_22_2), df.parse(CATEGORY_MAXDATE_22_2), 'F', CATEGORY_SNAME_22, event, competition2_2);
    	admin.createCategory(CATEGORY_NAME_23, df.parse(CATEGORY_MINDATE_23), df.parse(CATEGORY_MAXDATE_23), 'F', CATEGORY_SNAME_23, event, competition3);
    	admin.createCategory(CATEGORY_NAME_24, df.parse(CATEGORY_MINDATE_24), df.parse(CATEGORY_MAXDATE_24), 'F', CATEGORY_SNAME_24, event, competition4);
    	admin.createCategory(CATEGORY_NAME_25, df.parse(CATEGORY_MINDATE_25), df.parse(CATEGORY_MAXDATE_25), 'F', CATEGORY_SNAME_25, event, competition5);
    	admin.createCategory(CATEGORY_NAME_26, df.parse(CATEGORY_MINDATE_26), df.parse(CATEGORY_MAXDATE_26), 'F', CATEGORY_SNAME_26, event, competition5, competition6, competition7);
    	admin.createCategory(CATEGORY_NAME_27, df.parse(CATEGORY_MINDATE_27), df.parse(CATEGORY_MAXDATE_27), 'F', CATEGORY_SNAME_27, event, competition5, competition6, competition7);
    	admin.createCategory(CATEGORY_NAME_28, df.parse(CATEGORY_MINDATE_28), df.parse(CATEGORY_MAXDATE_28), 'F', CATEGORY_SNAME_28, event, competition5, competition6, competition7);
    	admin.createCategory(CATEGORY_NAME_29, df.parse(CATEGORY_MINDATE_29), df.parse(CATEGORY_MAXDATE_29), 'F', CATEGORY_SNAME_29, event, competition5, competition6, competition7);
    	admin.createCategory(CATEGORY_NAME_30, df.parse(CATEGORY_MINDATE_30), df.parse(CATEGORY_MAXDATE_30), 'F', CATEGORY_SNAME_30, event, competition5, competition6, competition7);
    	admin.createCategory(CATEGORY_NAME_31, df.parse(CATEGORY_MINDATE_31), df.parse(CATEGORY_MAXDATE_31), 'F', CATEGORY_SNAME_31, event, competition5, competition6, competition7);
    	admin.createCategory(CATEGORY_NAME_32, df.parse(CATEGORY_MINDATE_32), df.parse(CATEGORY_MAXDATE_32), 'F', CATEGORY_SNAME_32, event, competition5, competition6, competition7);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}