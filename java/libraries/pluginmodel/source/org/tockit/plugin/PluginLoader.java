/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Finds and loads plugins from specified location.
 * <p>
 * PluginLoader responsibilities:
 * <ul>
 * <li>
 * will scan plugins directory (specified in constructor parameters) for
 * any subdirectories and will assume that each found subdirectory corresponds
 * to a plugin.
 * </li>
 * <li>
 * once plugin directories are identified, PluginLoader will check for
 * file plugin.txt containing a list of fully qualified plugin class names.
 * If class names are found - these classes will be loaded. Otherwise,
 * PluginLoader will check the entire plugin directory for classes
 * implementing Plugin interface and load all such classes. The former
 * approach using plugin.txt file is more efficient. Another advantage
 * of the formet approach is that you don't need include some libraries
 * not used in the production. For example, if your code is using junit
 * test framework for TestCases, but you are not making use of this
 * classes in the actual plugin - we don't need to load them, but unless you
 * specify what classes to load - plugin loader will attempt to load
 * each class in order to figure out which ones of those are plugins.
 * Loading each class means loading all classes used by this class. Therefore
 * all TestCases will be loaded and if they use junit package, then classes
 * from this package will need to be loaded as well.
 * ( plugin.txt file should contain class name per line.)
 * </li>
 * <li>
 * after all plugin classes found for the current plugin - PluginLoader
 * will instantiate these using PluginClassLoader and call method load() on
 * each plugin implementation.
 * </li>
 *  </ul>
 * </p>
 */
public class PluginLoader extends LoaderBase {

	private static final Logger logger = Logger.getLogger(PluginLoader.class.getName());
	private static final String pluginDescriptorFileName = "plugin.txt";
	
	private static List errors = new ArrayList();
	
	/**
	 * <p>
	 * Encapsulate a plugin loading error.
	 * </p>
	 * <p>
	 * Error includes plugin location and exception that occured 
	 * during plugin loading
	 * </p>
	 */
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
	
	/**
	 * Instances of this class shouldn't be created - access it via static method loadPlugins.
	 */
	private PluginLoader () {
	}
	
	/**
	 * <p>
	 * Load plugins from the specified locations.
	 * </p>
	 * <p>
	 * @return errors that occured during plugin loading so they can be reported to a user.
	 * </p>
	 */
	public static PluginLoader.Error[] loadPlugins (File pluginDirectory) throws FileNotFoundException {
		logger.setLevel(Level.FINER);

		File[] pluginDirs = findSubDirectories(pluginDirectory);
		if (pluginDirs == null) {
			throw new FileNotFoundException("Didn't find specified plugins directory: " + pluginDirs);
		}
		logger.fine("STARTING to load plugins. Found " + pluginDirs.length + " plugins");

		for (int i = 0; i < pluginDirs.length; i++) {
			File curPluginDir = pluginDirs[i];
			try {
				logger.fine("Loading class loader for " + curPluginDir);
				Class[] foundPlugins = findClassesInDir(curPluginDir, pluginDescriptorFileName, Plugin.class, logger);
				loadPluginClasses(foundPlugins);
				logger.fine("Finished loading plugins in " + curPluginDir);
			} catch (Exception e) {
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                logger.warning("Error loading classes -- " + writer.toString());
				errors.add( new PluginLoader.Error(curPluginDir, e));
			}
		}
		
		logger.warning("FINISHED loading plugins with " + errors.size() + " error(s)");
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
