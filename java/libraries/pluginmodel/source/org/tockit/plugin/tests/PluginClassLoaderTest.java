/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.plugin.tests;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.tockit.docco.documenthandler.DocumentHandler;
import org.tockit.plugin.PluginClassLoader;

import junit.framework.TestCase;


public class PluginClassLoaderTest extends TestCase {
	

	private PluginClassLoader classLoader;
	
	/**
	 * @todo all tests rely on hardcoded values. Change to something
	 * more dynamic. 
	 */
	public PluginClassLoaderTest(String name) {
		super(name);
		String pluginsDirLoc1 = System.getProperty("user.dir") 
							+ System.getProperty("file.separator")
							+ "pluginmodel/thirdParty/testJars";
		try {	
			Logger logger = Logger.getLogger(PluginClassLoader.class.getName());
			logger.setLevel(Level.FINE);
//			logger.addHandler(new FileHandler("%h/log%u.log"));

			classLoader = new PluginClassLoader(new File(pluginsDirLoc1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void testFindResource() throws IOException {
		assertNotNull(classLoader.getResource("Multivalent.jar"));
		assertNotNull(classLoader.getResource("multivalent/Multivalent.class"));
		assertNotNull(classLoader.getResource("org/tockit/docco/documenthandler/MultivalentDocumentHandler.class"));
		assertNull(classLoader.getResource("Multivalent.class"));
		assertNotNull(classLoader.getResource("sys/Preferences.txt"));

		Enumeration enumeration = classLoader.getResources("sys/Preferences.txt");
		assertNotNull(enumeration);
		assertEquals(true, enumeration.hasMoreElements());
	}

	public void testFindClass() throws ClassNotFoundException {
        // @todo find simpler test case using smaller JARs
		assertNotNull("load class ", classLoader.loadClass("org.tockit.docco.documenthandler.MultivalentDocumentHandler"));
		assertNotNull("load class from jar file", classLoader.loadClass("multivalent.Multivalent"));
	}
	
	public void testFindClassException () {
		try {
			classLoader.loadClass("HelloWorld");
			fail("Expected ClassNotFoundException");
		} catch (ClassNotFoundException e) {
            // this is the expected behaviour
        }
	}
}
