/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.tockit.plugin.Plugin;
import org.tockit.plugin.PluginClassLoader;


public class PluginLoader {

	private static final Logger logger = Logger.getLogger(PluginLoader.class.getName());
	
	private List errors = new ArrayList();
	
	private class DirectoryFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			if (pathname.isDirectory()) {
				return true;
			}
			return false;
		}
	}
	
	public PluginLoader () {
		logger.setLevel(Level.FINER);
		
		/// @todo this should be read from config manager?...
		String pluginsDirName = "plugins";

		String pluginsBaseDir = System.getProperty("user.dir") + File.separator;
		
		File pluginsDirFile1 = new File(pluginsBaseDir + pluginsDirName);
		File pluginsDirFile2 = new File(pluginsBaseDir +
										"docco" +
										File.separator + 
										pluginsDirName);
		
		File[] pluginsBaseFiles = { pluginsDirFile1, pluginsDirFile2	};
		
		File[] pluginDirs = null;
		for (int i = 0; i < pluginsBaseFiles.length; i++) {
			File file = pluginsBaseFiles[i];
			if (file.exists()) {
				pluginDirs = file.listFiles(new DirectoryFileFilter());
				break; 
			}
		}
		logger.fine("STARTING to load plugins. Found " + pluginDirs.length + " plugins");
		
		if (pluginDirs == null) {
			ErrorDialog.showError(null, null, "Didn't find any plugins");			
		}
		else {
			for (int i = 0; i < pluginDirs.length; i++) {
				try {
					logger.fine("Loading class loader for " + pluginDirs[i]);
					PluginClassLoader classLoader = new PluginClassLoader(pluginDirs[i]);
					Class[] foundPlugins = listPlugins(classLoader);
					loadPlugins(foundPlugins);
					logger.fine("Finished loading plugins in " + pluginDirs[i]);
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
					errors.add(e);
				}
			}
		}
		
		if (errors.size() > 0) {
			/// @todo need to deal with errors in a better fashion. 
			ErrorDialog.showError(null, null, "There were errors loading plugins. Check exceptions stack trace");
		}
		logger.fine("FINISHED loading plugins");
	}
	
	private Class[] listPlugins (PluginClassLoader classLoader) {
		try {
			Class[] foundPlugins = classLoader.findClassesImplementingGivenIterface(Plugin.class);
			return foundPlugins;
		}
		catch (NoClassDefFoundError e) {
			e.printStackTrace();
			errors.add(e);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			errors.add(e);
		}
		return new Class[0];
	}
	
	private void loadPlugins (Class[] plugins) {
		try {
			for (int i = 0; i < plugins.length; i++) {
				Class cur = plugins[i];
				Plugin plugin = (Plugin) cur.newInstance();
				logger.finer("Loading plugin " + plugin.getClass().getName());
				plugin.load();
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
			errors.add(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			errors.add(e);
		}
	}
	
}
