/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tockit.docco.indexer.documenthandler.DocumentHandler;

import junit.framework.TestCase;


public class PluginClassLoaderTest extends TestCase {
	

	private PluginClassLoader classLoader;
	
	public PluginClassLoaderTest(String name) {
		super(name);
		String pluginsDirLoc1 = System.getProperty("user.dir") 
							+ System.getProperty("file.separator")
							+ "plugins";
		String pluginsDirLoc2 = System.getProperty("user.dir") 
							+ System.getProperty("file.separator")
							+ "docco"
							+ System.getProperty("file.separator")
							+ "plugins";
		try {	
			Logger logger = Logger.getLogger(PluginClassLoader.class.getName());
			logger.setLevel(Level.FINE);
//			logger.addHandler(new FileHandler("%h/log%u.log"));

			try {
				classLoader = new PluginClassLoader(new File(pluginsDirLoc1));
			}
			catch (FileNotFoundException e) {
				classLoader = new PluginClassLoader(new File(pluginsDirLoc2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void testFindResource() {
		assertNotNull(classLoader.getResource("libs/PDFBox.jar"));
		assertNotNull(classLoader.getResource("org/pdfbox/pdfparser/PDFParser.class"));
		assertNull(classLoader.getResource("PDFParser.class"));
		assertNull(classLoader.getResource("PdfDocumentHandler.class"));
		assertNotNull(classLoader.getResource("org\\tockit\\docco\\indexer\\documenthandler\\PdfDocumentHandler.class"));
		assertNotNull(classLoader.getResource("doc/UQlogo.jpg"));
		//assertEquals(true, classLoader.getResource("test/doc.jar/!/doc/UQlogo.jpg") != null);		
	}

	public void testFindClass() throws ClassNotFoundException {
		assertNotNull("load class ", classLoader.loadClass("org.tockit.docco.indexer.documenthandler.PdfDocumentHandler"));
		assertNotNull("load class from jar file", classLoader.loadClass("org.pdfbox.pdfparser.PDFParser"));
	}
	
	public void testFindClassException () {
		try {
			classLoader.loadClass("HelloWorld");
			fail("Expected ClassNotFoundException");
		} catch (ClassNotFoundException e) {}
	}

	public void testFindClassesImplementingGivenIterface() throws Exception {
		Class[] classes = classLoader.findClassesImplementingGivenIterface(DocumentHandler.class);
		assertEquals("should be able to find and load some classes implementing given interface ",
										true, classes.length != 0);
		/// @todo should we also test if we can instantiate found classes?
	}
}
