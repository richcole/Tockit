/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.plugin;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PluginLoader {

	private static final Logger logger = Logger.getLogger(PluginLoader.class.getName());
	
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
		logger.fine("STARTING to load plugins. Found " + pluginDirs.length + " plugins");
		
		if (pluginDirs == null) {
			throw new PluginLoaderException("Didn't find any plugins in specified plugins directories: " + pluginDirs);
		}

		for (int i = 0; i < pluginDirs.length; i++) {
			try {
				logger.fine("Loading class loader for " + pluginDirs[i]);

				PluginClassLoader classLoader = new PluginClassLoader(pluginDirs[i]);
				Class[] foundPlugins = classLoader.findClassesImplementingGivenIterface(Plugin.class);
				loadPluginClasses(foundPlugins);

				logger.fine("Finished loading plugins in " + pluginDirs[i]);
			}
			catch (Exception e) {
				errors.add(new PluginLoader.Error(pluginDirs[i], e));
			}
		}
		
		logger.fine("FINISHED loading plugins with " + errors.size() + " error(s)");
		PluginLoader.Error[] res = (PluginLoader.Error[]) errors.toArray(new PluginLoader.Error[errors.size()]);
		return res;
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
