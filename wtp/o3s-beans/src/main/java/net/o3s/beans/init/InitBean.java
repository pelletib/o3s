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
import net.o3s.apis.TestData;



/**
 * Session Bean implementation class InitBean
 */
@Stateless
@Remote(IEJBInitRemote.class)
public class InitBean implements IEJBInitRemote {

    @EJB
    private IEJBAdminLocal admin;

    public void init() {

    	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        // Init Events
    	IEntityEvent event = null;
		try {
			event = admin.createEvent(TestData.EVENT_NAME_0, df.parse(TestData.EVENT_DATE_0), TestData.EVENT_IMAGE_0 );
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        // Init Competitions
    	IEntityCompetition nocompetition = admin.createCompetition(TestData.NOCOMPETITION_NAME, TestData.NOCOMPETITION_LOWER_LABEL_NB, TestData.NOCOMPETITION_HIGHER_LABEL_NB, TestData.NOCOMPETITION_LOWER_LABEL_NB, event.getId(), false);

    	IEntityCompetition competition0_1 = admin.createCompetition(TestData.COMPETITION_NAME_0_1, TestData.COMPETITION_LOWER_LABEL_NB_0_1, TestData.COMPETITION_HIGHER_LABEL_NB_0_1, TestData.COMPETITION_LOWER_LABEL_NB_0_1, event.getId(), false);
    	IEntityCompetition competition0_2 = admin.createCompetition(TestData.COMPETITION_NAME_0_2, TestData.COMPETITION_LOWER_LABEL_NB_0_2, TestData.COMPETITION_HIGHER_LABEL_NB_0_2, TestData.COMPETITION_LOWER_LABEL_NB_0_2, event.getId(), false);
    	IEntityCompetition competition0_3 = admin.createCompetition(TestData.COMPETITION_NAME_0_3, TestData.COMPETITION_LOWER_LABEL_NB_0_3, TestData.COMPETITION_HIGHER_LABEL_NB_0_3, TestData.COMPETITION_LOWER_LABEL_NB_0_3, event.getId(), false);
    	IEntityCompetition competition1_1 = admin.createCompetition(TestData.COMPETITION_NAME_1_1, TestData.COMPETITION_LOWER_LABEL_NB_1_1, TestData.COMPETITION_HIGHER_LABEL_NB_1_1, TestData.COMPETITION_LOWER_LABEL_NB_1_1, event.getId(), false);
    	IEntityCompetition competition1_2 = admin.createCompetition(TestData.COMPETITION_NAME_1_2, TestData.COMPETITION_LOWER_LABEL_NB_1_2, TestData.COMPETITION_HIGHER_LABEL_NB_1_2, TestData.COMPETITION_LOWER_LABEL_NB_1_2, event.getId(), false);
    	IEntityCompetition competition1_3 = admin.createCompetition(TestData.COMPETITION_NAME_1_3, TestData.COMPETITION_LOWER_LABEL_NB_1_3, TestData.COMPETITION_HIGHER_LABEL_NB_1_3, TestData.COMPETITION_LOWER_LABEL_NB_1_3, event.getId(), false);
    	IEntityCompetition competition1_4 = admin.createCompetition(TestData.COMPETITION_NAME_1_4, TestData.COMPETITION_LOWER_LABEL_NB_1_4, TestData.COMPETITION_HIGHER_LABEL_NB_1_4, TestData.COMPETITION_LOWER_LABEL_NB_1_4, event.getId(), false);
    	IEntityCompetition competition2_1 = admin.createCompetition(TestData.COMPETITION_NAME_2_1, TestData.COMPETITION_LOWER_LABEL_NB_2_1, TestData.COMPETITION_HIGHER_LABEL_NB_2_1, TestData.COMPETITION_LOWER_LABEL_NB_2_1, event.getId(), false);
    	IEntityCompetition competition2_2 = admin.createCompetition(TestData.COMPETITION_NAME_2_2, TestData.COMPETITION_LOWER_LABEL_NB_2_2, TestData.COMPETITION_HIGHER_LABEL_NB_2_2, TestData.COMPETITION_LOWER_LABEL_NB_2_2, event.getId(), false);
    	IEntityCompetition competition3 = admin.createCompetition(TestData.COMPETITION_NAME_3, TestData.COMPETITION_LOWER_LABEL_NB_3, TestData.COMPETITION_HIGHER_LABEL_NB_3, TestData.COMPETITION_LOWER_LABEL_NB_3, event.getId(), false);
    	IEntityCompetition competition4 = admin.createCompetition(TestData.COMPETITION_NAME_4, TestData.COMPETITION_LOWER_LABEL_NB_4, TestData.COMPETITION_HIGHER_LABEL_NB_4, TestData.COMPETITION_LOWER_LABEL_NB_4, event.getId(), false);
    	IEntityCompetition competition5 = admin.createCompetition(TestData.COMPETITION_NAME_5, TestData.COMPETITION_LOWER_LABEL_NB_5, TestData.COMPETITION_HIGHER_LABEL_NB_5, TestData.COMPETITION_LOWER_LABEL_NB_5, event.getId(), false);
    	IEntityCompetition competition6 = admin.createCompetition(TestData.COMPETITION_NAME_6, TestData.COMPETITION_LOWER_LABEL_NB_6, TestData.COMPETITION_HIGHER_LABEL_NB_6, TestData.COMPETITION_LOWER_LABEL_NB_6, event.getId(), false);
    	IEntityCompetition competition7 = admin.createCompetition(TestData.COMPETITION_NAME_7, TestData.COMPETITION_LOWER_LABEL_NB_7, TestData.COMPETITION_HIGHER_LABEL_NB_7, TestData.COMPETITION_LOWER_LABEL_NB_7, event.getId(), false);
    	IEntityCompetition competition8 = admin.createCompetition(TestData.COMPETITION_NAME_8, TestData.COMPETITION_LOWER_LABEL_NB_8, TestData.COMPETITION_HIGHER_LABEL_NB_8, TestData.COMPETITION_LOWER_LABEL_NB_8, event.getId(), true);

        // Init Categories

    	try {
        admin.createCategory(TestData.NOCATEGORY_NAME, df.parse(TestData.NOCATEGORY_MINDATE), df.parse(TestData.NOCATEGORY_MAXDATE), 'M', TestData.NOCATEGORY_SNAME, event.getId(), nocompetition);

		admin.createCategory(TestData.CATEGORY_NAME_0_1, df.parse(TestData.CATEGORY_MINDATE_0_1), df.parse(TestData.CATEGORY_MAXDATE_0_1), 'M', TestData.CATEGORY_SNAME_0, event.getId(), competition0_1);
		admin.createCategory(TestData.CATEGORY_NAME_0_2, df.parse(TestData.CATEGORY_MINDATE_0_2), df.parse(TestData.CATEGORY_MAXDATE_0_2), 'M', TestData.CATEGORY_SNAME_0, event.getId(), competition0_2);
		admin.createCategory(TestData.CATEGORY_NAME_0_3, df.parse(TestData.CATEGORY_MINDATE_0_3), df.parse(TestData.CATEGORY_MAXDATE_0_3), 'M', TestData.CATEGORY_SNAME_0, event.getId(), competition0_3);
    	admin.createCategory(TestData.CATEGORY_NAME_1_1, df.parse(TestData.CATEGORY_MINDATE_1_1), df.parse(TestData.CATEGORY_MAXDATE_1_1), 'M', TestData.CATEGORY_SNAME_1, event.getId(), competition1_1);
    	admin.createCategory(TestData.CATEGORY_NAME_1_2, df.parse(TestData.CATEGORY_MINDATE_1_2), df.parse(TestData.CATEGORY_MAXDATE_1_2), 'M', TestData.CATEGORY_SNAME_1, event.getId(), competition1_2);
    	admin.createCategory(TestData.CATEGORY_NAME_1_3, df.parse(TestData.CATEGORY_MINDATE_1_3), df.parse(TestData.CATEGORY_MAXDATE_1_3), 'M', TestData.CATEGORY_SNAME_1, event.getId(), competition1_3);
    	admin.createCategory(TestData.CATEGORY_NAME_1_4, df.parse(TestData.CATEGORY_MINDATE_1_4), df.parse(TestData.CATEGORY_MAXDATE_1_4), 'M', TestData.CATEGORY_SNAME_1, event.getId(), competition1_4);
    	admin.createCategory(TestData.CATEGORY_NAME_2_1, df.parse(TestData.CATEGORY_MINDATE_2_1), df.parse(TestData.CATEGORY_MAXDATE_2_1), 'M', TestData.CATEGORY_SNAME_2, event.getId(), competition2_1);
    	admin.createCategory(TestData.CATEGORY_NAME_2_2, df.parse(TestData.CATEGORY_MINDATE_2_2), df.parse(TestData.CATEGORY_MAXDATE_2_2), 'M', TestData.CATEGORY_SNAME_2, event.getId(), competition2_2);
    	admin.createCategory(TestData.CATEGORY_NAME_3, df.parse(TestData.CATEGORY_MINDATE_3), df.parse(TestData.CATEGORY_MAXDATE_3), 'M', TestData.CATEGORY_SNAME_3, event.getId(), competition3);
    	admin.createCategory(TestData.CATEGORY_NAME_4, df.parse(TestData.CATEGORY_MINDATE_4), df.parse(TestData.CATEGORY_MAXDATE_4), 'M', TestData.CATEGORY_SNAME_4, event.getId(), competition4);
    	admin.createCategory(TestData.CATEGORY_NAME_5, df.parse(TestData.CATEGORY_MINDATE_5), df.parse(TestData.CATEGORY_MAXDATE_5), 'M', TestData.CATEGORY_SNAME_5, event.getId(), competition5);
    	admin.createCategory(TestData.CATEGORY_NAME_6, df.parse(TestData.CATEGORY_MINDATE_6), df.parse(TestData.CATEGORY_MAXDATE_6), 'M', TestData.CATEGORY_SNAME_6, event.getId(), competition5, competition6, competition7);
    	admin.createCategory(TestData.CATEGORY_NAME_7, df.parse(TestData.CATEGORY_MINDATE_7), df.parse(TestData.CATEGORY_MAXDATE_7), 'M', TestData.CATEGORY_SNAME_7, event.getId(), competition5, competition6, competition7);
    	admin.createCategory(TestData.CATEGORY_NAME_8, df.parse(TestData.CATEGORY_MINDATE_8), df.parse(TestData.CATEGORY_MAXDATE_8), 'M', TestData.CATEGORY_SNAME_8, event.getId(), competition5, competition6, competition7);
    	admin.createCategory(TestData.CATEGORY_NAME_9, df.parse(TestData.CATEGORY_MINDATE_9), df.parse(TestData.CATEGORY_MAXDATE_9), 'M', TestData.CATEGORY_SNAME_9, event.getId(), competition5, competition6, competition7);
    	admin.createCategory(TestData.CATEGORY_NAME_10, df.parse(TestData.CATEGORY_MINDATE_10), df.parse(TestData.CATEGORY_MAXDATE_10), 'M', TestData.CATEGORY_SNAME_10, event.getId(), competition5, competition6, competition7);
    	admin.createCategory(TestData.CATEGORY_NAME_11, df.parse(TestData.CATEGORY_MINDATE_11), df.parse(TestData.CATEGORY_MAXDATE_11), 'M', TestData.CATEGORY_SNAME_11, event.getId(), competition5, competition6, competition7);
    	admin.createCategory(TestData.CATEGORY_NAME_12, df.parse(TestData.CATEGORY_MINDATE_12), df.parse(TestData.CATEGORY_MAXDATE_12), 'M', TestData.CATEGORY_SNAME_12, event.getId(), competition5, competition6, competition7);

    	admin.createCategory(TestData.CATEGORY_NAME_20_1, df.parse(TestData.CATEGORY_MINDATE_20_1), df.parse(TestData.CATEGORY_MAXDATE_20_1), 'F', TestData.CATEGORY_SNAME_20, event.getId(), competition0_1);
    	admin.createCategory(TestData.CATEGORY_NAME_20_2, df.parse(TestData.CATEGORY_MINDATE_20_2), df.parse(TestData.CATEGORY_MAXDATE_20_2), 'F', TestData.CATEGORY_SNAME_20, event.getId(), competition0_2);
    	admin.createCategory(TestData.CATEGORY_NAME_20_3, df.parse(TestData.CATEGORY_MINDATE_20_3), df.parse(TestData.CATEGORY_MAXDATE_20_3), 'F', TestData.CATEGORY_SNAME_20, event.getId(), competition0_3);
    	admin.createCategory(TestData.CATEGORY_NAME_21_1, df.parse(TestData.CATEGORY_MINDATE_21_1), df.parse(TestData.CATEGORY_MAXDATE_21_1), 'F', TestData.CATEGORY_SNAME_21, event.getId(), competition1_1);
    	admin.createCategory(TestData.CATEGORY_NAME_21_2, df.parse(TestData.CATEGORY_MINDATE_21_2), df.parse(TestData.CATEGORY_MAXDATE_21_2), 'F', TestData.CATEGORY_SNAME_21, event.getId(), competition1_2);
    	admin.createCategory(TestData.CATEGORY_NAME_21_3, df.parse(TestData.CATEGORY_MINDATE_21_3), df.parse(TestData.CATEGORY_MAXDATE_21_3), 'F', TestData.CATEGORY_SNAME_21, event.getId(), competition1_3);
    	admin.createCategory(TestData.CATEGORY_NAME_21_4, df.parse(TestData.CATEGORY_MINDATE_21_4), df.parse(TestData.CATEGORY_MAXDATE_21_4), 'F', TestData.CATEGORY_SNAME_21, event.getId(), competition1_4);
    	admin.createCategory(TestData.CATEGORY_NAME_22_1, df.parse(TestData.CATEGORY_MINDATE_22_1), df.parse(TestData.CATEGORY_MAXDATE_22_1), 'F', TestData.CATEGORY_SNAME_22, event.getId(), competition2_1);
    	admin.createCategory(TestData.CATEGORY_NAME_22_2, df.parse(TestData.CATEGORY_MINDATE_22_2), df.parse(TestData.CATEGORY_MAXDATE_22_2), 'F', TestData.CATEGORY_SNAME_22, event.getId(), competition2_2);
    	admin.createCategory(TestData.CATEGORY_NAME_23, df.parse(TestData.CATEGORY_MINDATE_23), df.parse(TestData.CATEGORY_MAXDATE_23), 'F', TestData.CATEGORY_SNAME_23, event.getId(), competition3);
    	admin.createCategory(TestData.CATEGORY_NAME_24, df.parse(TestData.CATEGORY_MINDATE_24), df.parse(TestData.CATEGORY_MAXDATE_24), 'F', TestData.CATEGORY_SNAME_24, event.getId(), competition4);
    	admin.createCategory(TestData.CATEGORY_NAME_25, df.parse(TestData.CATEGORY_MINDATE_25), df.parse(TestData.CATEGORY_MAXDATE_25), 'F', TestData.CATEGORY_SNAME_25, event.getId(), competition5);
    	admin.createCategory(TestData.CATEGORY_NAME_26, df.parse(TestData.CATEGORY_MINDATE_26), df.parse(TestData.CATEGORY_MAXDATE_26), 'F', TestData.CATEGORY_SNAME_26, event.getId(), competition5, competition6, competition7);
    	admin.createCategory(TestData.CATEGORY_NAME_27, df.parse(TestData.CATEGORY_MINDATE_27), df.parse(TestData.CATEGORY_MAXDATE_27), 'F', TestData.CATEGORY_SNAME_27, event.getId(), competition5, competition6, competition7);
    	admin.createCategory(TestData.CATEGORY_NAME_28, df.parse(TestData.CATEGORY_MINDATE_28), df.parse(TestData.CATEGORY_MAXDATE_28), 'F', TestData.CATEGORY_SNAME_28, event.getId(), competition5, competition6, competition7);
    	admin.createCategory(TestData.CATEGORY_NAME_29, df.parse(TestData.CATEGORY_MINDATE_29), df.parse(TestData.CATEGORY_MAXDATE_29), 'F', TestData.CATEGORY_SNAME_29, event.getId(), competition5, competition6, competition7);
    	admin.createCategory(TestData.CATEGORY_NAME_30, df.parse(TestData.CATEGORY_MINDATE_30), df.parse(TestData.CATEGORY_MAXDATE_30), 'F', TestData.CATEGORY_SNAME_30, event.getId(), competition5, competition6, competition7);
    	admin.createCategory(TestData.CATEGORY_NAME_31, df.parse(TestData.CATEGORY_MINDATE_31), df.parse(TestData.CATEGORY_MAXDATE_31), 'F', TestData.CATEGORY_SNAME_31, event.getId(), competition5, competition6, competition7);
    	admin.createCategory(TestData.CATEGORY_NAME_32, df.parse(TestData.CATEGORY_MINDATE_32), df.parse(TestData.CATEGORY_MAXDATE_32), 'F', TestData.CATEGORY_SNAME_32, event.getId(), competition5, competition6, competition7);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
