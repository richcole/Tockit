/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public final class LoaderUtil {

	protected static List loadPluginDescriptorSpecifedClasses(
									File curPluginDir,
									PluginClassLoader classLoader,
									String descriptorFileName,
									Logger logger)
									throws FileNotFoundException, 
									IOException, ClassNotFoundException {
	
		File descriptorFile = new File(curPluginDir, descriptorFileName);
		List descriptorClasses = new ArrayList();
		if (descriptorFile.exists()) {
			logger.finer("Found descriptor file");
			InputStream fileInStream = new FileInputStream(descriptorFile);
			Reader reader = new InputStreamReader(fileInStream);
			BufferedReader br = new BufferedReader(reader);
			String line;
			while ( (line = br.readLine()) != null ) {
				logger.finer("Looking for class with name: '" + line + "'");
				Class className = classLoader.findClass(line.trim());
				descriptorClasses.add(className);
			}
		}
		return descriptorClasses;
	}

	protected static File[] listBaseDirs(File[] pluginsBaseFiles) {
		File[] pluginDirs = null;
		for (int i = 0; i < pluginsBaseFiles.length; i++) {
			File file = pluginsBaseFiles[i];
			if (file.exists()) {
				pluginDirs = file.listFiles( new FileFilter () {
					public boolean accept(File pathname) {
						return pathname.isDirectory();
					}
				});
				break; 
			}
		}
		return pluginDirs;
	}

	protected static Class[] findClassesInDir(File curPluginDir, String pluginDescriptorFileName, Class interfaceClass, Logger logger)
											throws
												FileNotFoundException,
												IOException,
												ClassNotFoundException,
												NoClassDefFoundError {
		PluginClassLoader classLoader = new PluginClassLoader(curPluginDir);
		
		List descriptorClasses = 
				LoaderUtil.loadPluginDescriptorSpecifedClasses(
							curPluginDir, classLoader, 
							pluginDescriptorFileName, logger);
		Class[] foundPlugins = null;
		if (descriptorClasses.size() > 0) {
			foundPlugins = (Class[]) descriptorClasses.toArray(new Class[descriptorClasses.size()]);
		}
		else {
			foundPlugins = classLoader.findClassesImplementingGivenIterface(interfaceClass);
		}
		return foundPlugins;
	}
}
