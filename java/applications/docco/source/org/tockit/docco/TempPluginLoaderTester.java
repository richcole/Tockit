/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tockit.docco.indexer.documenthandler.DocumentHandler;
import org.tockit.docco.plugin.PluginClassLoader;


public class TempPluginLoaderTester {

	public static void main (String[] args) {
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

			ClassLoader doccoClassLoader = Docco.class.getClassLoader();

			ClassLoader pluginClassLoader;
			try {
				pluginClassLoader = new PluginClassLoader(new File(pluginsDirLoc1));
			}
			catch (FileNotFoundException e) {
				pluginClassLoader = new PluginClassLoader(new File(pluginsDirLoc2));
			}
			
			//pluginClassLoader = doccoClassLoader;
			
			System.out.println("getResource(libs/PDFBox.jar): " + pluginClassLoader.getResource("libs/PDFBox.jar"));
			System.out.println("getResource(org/pdfbox/pdfparser/PDFParser.class): " + pluginClassLoader.getResource("org/pdfbox/pdfparser/PDFParser.class"));
			System.out.println("getResource(PDFParser.class): " + pluginClassLoader.getResource("PDFParser.class"));
			System.out.println("getResource(PdfDocumentHandler.class): " + pluginClassLoader.getResource("PdfDocumentHandler.class"));
			System.out.println("getResource(org/pdfbox/pdfparser/PDFParser.class): " + pluginClassLoader.getResource("org/pdfbox/pdfparser/PDFParser.class"));
			System.out.println("getResource(org\\tockit\\docco\\indexer\\documenthandler\\PdfDocumentHandler.class): " + pluginClassLoader.getResource("org\\tockit\\docco\\indexer\\documenthandler\\PdfDocumentHandler.class"));
			System.out.println("loadClass(org.tockit.docco.indexer.documenthandler.PdfDocumentHandler): " + pluginClassLoader.loadClass("org.tockit.docco.indexer.documenthandler.PdfDocumentHandler"));
			//System.out.println("findClass(org/pdfbox/pdfparser/PDFParser.class): " + pluginClassLoader.findClass("org/pdfbox/pdfparser/PDFParser.class"));
			System.out.println("getResource(doc/UQlogo.jpg): " + pluginClassLoader.getResource("doc/UQlogo.jpg"));
			System.out.println("getResource(test/doc.jar/!/doc/UQlogo.jpg): " + pluginClassLoader.getResource("test/doc.jar/!/doc/UQlogo.jpg"));


				
//			Class[] classes = pluginClassLoader.findClassesImplementingGivenIterface(DocumentHandler.class);
//			System.out.println("\n\n");
//			for (int i = 0; i < classes.length; i++) {
//				Class class1 = classes[i];
//				DocumentHandler docHandler = (DocumentHandler) class1.newInstance();
//				System.out.println("instantiated doc handler: " + docHandler.getDisplayName());
//				docHandler.parseDocument(new URL("file:/E:/mumsWork/docco-test-data/papers/input.pdf"));
//			}
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
