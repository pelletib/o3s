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
package net.o3s.client.init;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;

import org.ow2.carol.jndi.spi.MultiOrbInitialContextFactory;
import org.ow2.easybeans.injection.api.ArchiveInjectionException;
import org.ow2.easybeans.injection.impl.ArchiveInjection;
import org.ow2.util.archive.impl.MemoryArchive;


public class AnnotationInjection {

	public static void inject() {
		 List<String> classes = new ArrayList<String>();
		 classes.add(Main.class.getName());

		 System.setProperty(Context.INITIAL_CONTEXT_FACTORY, MultiOrbInitialContextFactory.class.getName());
		 System.setProperty(Context.PROVIDER_URL, "rmi://localhost:1099");

		 MemoryArchive memoryArchive = new MemoryArchive(Main.class.getClassLoader(), classes);
		 memoryArchive.addResource("META-INF/application-client.xml", Main.class.getClassLoader().getResource("META-INF/application-client.xml"));
		 memoryArchive.addResource("META-INF/MANIFEST.MF", Main.class.getClassLoader().getResource("META-INF/MANIFEST.MF"));
		 ArchiveInjection archiveInjection = new ArchiveInjection(memoryArchive);

         try {
             archiveInjection.init(Main.class);
         } catch (ArchiveInjectionException e) {
             e.printStackTrace();
         }
	}
}
