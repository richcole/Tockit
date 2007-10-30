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


public abstract class LoaderBase {

	@SuppressWarnings("unchecked")
	protected static <T> List<Class<T>> loadPluginDescriptorSpecifedClasses(
											File curPluginDir,
											PluginClassLoader classLoader,
											String descriptorFileName,
											Class<T> interfaceClass,
											Logger logger)
											throws
												FileNotFoundException,
												IOException,
												ClassNotFoundException,
												PluginLoadFailedException {

		File descriptorFile = new File(curPluginDir, descriptorFileName);
		List<Class<T>> descriptorClasses = new ArrayList<Class<T>>();
		if (descriptorFile.exists()) {
			logger.finer("Found descriptor file");
			InputStream fileInStream = new FileInputStream(descriptorFile);
			Reader reader = new InputStreamReader(fileInStream);
			BufferedReader br = new BufferedReader(reader);
			String line;
			while ((line = br.readLine()) != null) {
				logger.finer("Looking for class with name: '" + line + "'");
				Class<?> className = classLoader.findClass(line.trim());
				if (!interfaceClass.isAssignableFrom(className)) {
					throw new PluginLoadFailedException(
						"Expected implementation of "
							+ interfaceClass
							+ " interface in "
							+ className);
				}
				descriptorClasses.add((Class<T>) className);
			}
		}
		return descriptorClasses;
	}

	protected static File[] findSubDirectories(File parentDirectory) {
		return parentDirectory.listFiles( new FileFilter () {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
	}


	@SuppressWarnings("unchecked")
	protected static <T> Class<T>[] findClassesInDir(
									File curPluginDir,
									String pluginDescriptorFileName,
									Class<T> interfaceClass,
									Logger logger)
									throws
										FileNotFoundException,
										IOException,
										ClassNotFoundException,
										NoClassDefFoundError,
										PluginLoadFailedException {

		PluginClassLoader classLoader = new PluginClassLoader(curPluginDir);
		List<Class<T>> descriptorClasses = LoaderBase.loadPluginDescriptorSpecifedClasses(
											curPluginDir,
											classLoader,
											pluginDescriptorFileName,
											interfaceClass,
											logger);
		Class<T>[] foundPlugins = null;
		if (descriptorClasses.size() > 0) {
			foundPlugins = descriptorClasses.toArray(new Class[descriptorClasses.size()]);
		}
		else {
			foundPlugins = classLoader.findClassesImplementingGivenIterface(interfaceClass);
		}
		return foundPlugins;
	}

}
