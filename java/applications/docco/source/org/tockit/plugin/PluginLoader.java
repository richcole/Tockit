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
import java.util.logging.Level;
import java.util.logging.Logger;


public class PluginLoader {

	private static final Logger logger = Logger.getLogger(PluginLoader.class.getName());
	private static final String pluginDescriptorFileName = "plugin.txt";
	
	private static List errors = new ArrayList();
	
	
	public static class Error {
		private File file;
		private Exception e;
		private Error (File file, Exception e) {
			this.file = file;
			this.e = e;
		}
		public Exception getException() {
			return e;
		}
		public File getPluginLocation() {
			return file;
		}

	}
		
	private PluginLoader () {
	}
	
	public static PluginLoader.Error[] loadPlugins (File[] pluginsBaseFiles) throws PluginLoaderException {
		logger.setLevel(Level.FINER);

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
		
		if (pluginDirs == null) {
			throw new PluginLoaderException("Didn't find any plugins in specified plugins directories: " + pluginDirs);
		}
		logger.fine("STARTING to load plugins. Found " + pluginDirs.length + " plugins");

		for (int i = 0; i < pluginDirs.length; i++) {
			File curPluginDir = pluginDirs[i];
			try {
				logger.fine("Loading class loader for " + curPluginDir);

				PluginClassLoader classLoader = new PluginClassLoader(curPluginDir);
				
				List descriptorClasses = 
						loadPluginDescriptorSpecifedClasses(curPluginDir, classLoader);
				Class[] foundPlugins = null;
				if (descriptorClasses.size() > 0) {
					foundPlugins = (Class[]) descriptorClasses.toArray(new Class[descriptorClasses.size()]);
				}
				else {
					foundPlugins = classLoader.findClassesImplementingGivenIterface(Plugin.class);
				}

				loadPluginClasses(foundPlugins);

				logger.fine("Finished loading plugins in " + curPluginDir);
			}
			catch (ClassCastException e) {
				String errMsg = "Expected implementation of org.tockit.plugin.Plugin " + 
								"interface in " + e.getMessage();
				errors.add( new PluginLoader.Error(
							curPluginDir, 
							new PluginLoaderException(errMsg, e)));
			} catch (Exception e) {
				errors.add( new PluginLoader.Error(curPluginDir, e));
			}
		}
		
		logger.fine("FINISHED loading plugins with " + errors.size() + " error(s)");
		PluginLoader.Error[] res = (PluginLoader.Error[]) errors.toArray(new PluginLoader.Error[errors.size()]);
		return res;
	}

	private static List loadPluginDescriptorSpecifedClasses(
									File curPluginDir,
									PluginClassLoader classLoader)
									throws FileNotFoundException, 
									IOException, ClassNotFoundException {

		File pluginDescriptorFile = new File(curPluginDir, pluginDescriptorFileName);
		List descriptorClasses = new ArrayList();
		if (pluginDescriptorFile.exists()) {
			logger.finer("Found plugin descriptor file");
			InputStream fileInStream = new FileInputStream(pluginDescriptorFile);
			Reader reader = new InputStreamReader(fileInStream);
			BufferedReader br = new BufferedReader(reader);
			String line;
			while ( (line = br.readLine()) != null ) {
				logger.finer("Looking for plugin with name: '" + line + "'");
				Class pluginClass = classLoader.findClass(line.trim());
				descriptorClasses.add(pluginClass);
			}
		}
		return descriptorClasses;
	}
	
	
	private static void loadPluginClasses (Class[] plugins) throws InstantiationException,
											IllegalAccessException {
		for (int i = 0; i < plugins.length; i++) {
			Class cur = plugins[i];
			Plugin plugin = (Plugin) cur.newInstance();
			logger.finer("Loading plugin " + plugin.getClass().getName());
			plugin.load();
		}
	}
	
}
