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
			PluginClassLoader pluginClassLoader;
			try {
				pluginClassLoader = new PluginClassLoader(new File(pluginsDirLoc1));
			}
			catch (FileNotFoundException e) {
				pluginClassLoader = new PluginClassLoader(new File(pluginsDirLoc2));
			}
				
			Class[] classes = pluginClassLoader.findClasses(DocumentHandler.class);
			System.out.println("\n\n");
			for (int i = 0; i < classes.length; i++) {
				Class class1 = classes[i];
				DocumentHandler docHandler = (DocumentHandler) class1.newInstance();
				System.out.println("instantiated doc handler: " + docHandler.getDisplayName());
				docHandler.parseDocument(new URL("file:/C:/nataliya/testData/input.pdf"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
