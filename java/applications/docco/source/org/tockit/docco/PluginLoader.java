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

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.tockit.plugin.Plugin;
import org.tockit.plugin.PluginClassLoader;


public class PluginLoader {
	
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
		
		if (pluginDirs == null) {
			ErrorDialog.showError(null, null, "Didn't find any plugins");			
		}
		else {
			for (int i = 0; i < pluginDirs.length; i++) {
				try {
					System.out.println("\nloading plugin loader for dir " + pluginDirs[i]);
					PluginClassLoader classLoader = new PluginClassLoader(pluginDirs[i]);

					System.out.println("loaded plugin loader, looking for plugins now");
					Class[] foundPlugins = listPlugins(classLoader);

					System.out.println("trying to load each plugin");
					loadPlugins(foundPlugins);

					System.out.println("finished loading all plugins");
					break;
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
					errors.add(e);
				}
			}
		}
		
		System.out.println("errors: " + errors.size());
		if (errors.size() > 0) {
			/// @todo need to deal with errors in a better fashion. 
			ErrorDialog.showError(null, null, "There were errors loading plugins. Check exceptions stack trace");
		}
		

		System.out.println("finished");
		
	}
	
	private Class[] listPlugins (PluginClassLoader classLoader) {
		try {
			Class[] foundPlugins = classLoader.findClassesImplementingGivenIterface(Plugin.class);
			System.out.println("found num of classes: " + foundPlugins.length);
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
				System.out.println("\t loading plugin " + plugin.getClass().getName());
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
