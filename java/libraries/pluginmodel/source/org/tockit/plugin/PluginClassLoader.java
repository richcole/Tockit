/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Class loader used to load resources found in the plugins directory.
 * <p>
 * This class loader will recurse through given directory and list
 * all resources found, including all resources contained in any jar
 * files found.
 * </p>
 * <p>
 * If duplicate class definitions are found - first class found is taken.
 * At the moment we don't have any control over order of loading, so it is
 * advisable to have one version of each class, otherwise there could be
 * some unpredictable behaviour in a plugin. If PluginClassLoader found a
 * definition of class already defined in the classpath - this class will be
 * loaded from the classpath. 
 * </p>
 * <p>
 * Some info on class loaders:
 * <ul>
 * <li>
 * http://developer.java.sun.com/developer/TechTips/2000/tt1027.html#tip1
 * </li>
 * <li>
 * http://forum.java.sun.com/thread.jsp?forum=37&thread=160773&tstart=0&trange=15
 * </li>
 * <li>
 * Java hotswap technology:
 * http://developers.sun.com/dev/coolstuff/hotswap/more.html
 * </li>
 * </ul>
 * </p>
 * <p>
 * For more information on loading classes or resources 
 * @see #findClass(String name) 
 * @see #findResource(String name)
 * </p>
 */
public class PluginClassLoader extends ClassLoader {

	private static final Logger logger = Logger.getLogger(PluginClassLoader.class.getName());

	private File pluginsDirLocation;

	private List foundResources = new ArrayList();
	
	/**
	 * keys - fully qualified class names
	 * values - classes
	 */
	private Hashtable loadedClasses = new Hashtable();
	
	private String classNotFound;

	private interface Resource {
		public byte[] getData() throws IOException;
		public String getRelativePath();
		public URL getURL() throws MalformedURLException;
	}
	
	
	private class FileResource implements Resource {
		private File file;

		public FileResource (File file) {
			this.file = file;
		}

		public byte[] getData() throws IOException {
			FileInputStream in = new FileInputStream(this.file);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int i;
			while( (i = in.read()) != -1) {
			   out.write(i);
			}	 	
			return out.toByteArray();
		}
		
		public String getRelativePath () {
			return getPathRelativeToPluginsBaseDir(this.file);
		}
		
		public File getFile() {
			return this.file;
		}
		
		public URL getURL () throws MalformedURLException {
			return this.file.toURL();
		}

		public String toString () {
			return "FileResource, file: " + this.file;
		}
	}
	
	private class JarResource implements Resource {
		File jarFile;
		ZipEntry zipEntry;
		
		public JarResource (File jarFile, ZipEntry resourceLoc) {
			this.jarFile = jarFile;
			this.zipEntry = resourceLoc;
		}
		
		public byte[] getData() throws IOException {
			ZipFile zipFile = new ZipFile(this.jarFile);
			InputStream in = zipFile.getInputStream(this.zipEntry);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int i;
			while( (i = in.read()) != -1) {
			   out.write(i);
			}	 	
			return out.toByteArray();
		}
		
		public String getFileName() {
			String zipEntryName = this.zipEntry.getName();
			String resultName = zipEntryName;
			String[] elements = zipEntryName.split("/");
			int arraySize = elements.length;
			
			for (int i = arraySize - 1; i >= 0; i--) {
				String curElement = elements[i];
				if (curElement.length() > 0) {
					resultName = curElement;
					break;
				}
			}
			return resultName;
		}
		
		public String getRelativePath() {
			//String relZipFilePath = getPathRelativeToPluginsBaseDir(this.jarFile);
			//String result = relZipFilePath + "/!/" + this.zipEntry.getName();
			//return result;
			return this.zipEntry.getName();
		}
		
		public URL getURL() throws MalformedURLException {
			URL url = new URL("jar:file:/" + this.jarFile.getPath() + 
								"/!/" + this.zipEntry.getName());
			return url;
		}
		
		public String toString () {
			return "JarResource, file: " + this.zipEntry + ", jar: " + this.jarFile;
		}
	}
	
		
	/**
	 * @param file - path where to start looking for classes
	 */
	public PluginClassLoader(File file) throws FileNotFoundException {
		super();
		this.pluginsDirLocation = file;
		logger.setLevel(Level.FINER);
		
		logger.entering("PluginClassLoader", "Constructor", file.getAbsoluteFile());
		
		if (!file.exists()) {
			throw new FileNotFoundException("Couldn't locate specified plugins directory: " + file.getAbsolutePath());
		}
		
		listAllFiles(file);	

		logger.exiting("PluginClassLoader", "Constructor", "found num of resources: " + this.foundResources.size());
	}
	
	/**
	 * Find all classes implementing given interface.
	 * <p>
	 * This method will go through all classes found in the plugin directory,
	 * load each one and check if it implements given interface.
	 * Note that this could be an expensive process depending on a number of
	 * classes contained in the plugin directory.
	 * </p>
	 */
	public Class[] findClassesImplementingGivenIterface (Class interfaceClass) 
						throws ClassNotFoundException, NoClassDefFoundError {
		List result = new ArrayList();
		Iterator it = this.foundResources.iterator();
		while (it.hasNext()) {
			Resource curResource = (Resource) it.next();
			String name = curResource.getRelativePath();
			if (name.endsWith(".class")) {
				Class curClass = loadClass(curResource);
				if (result.contains(curClass)) {
					continue;
				}
				Class[] interfaces = curClass.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					Class class1 = interfaces[i];
					if (class1.equals(interfaceClass)) {
						result.add(curClass);
					}
				}
			}
		}
		return (Class[]) result.toArray(new Class[result.size()]);
	}
	
	/**
	 * Find a resource with given name in this class loader. 
	 * <p>
	 * This method will return first resource found with the given name.
	 * In case of duplicate resources with the same name there is no 
	 * guarantee which one will be found first.
	 * </p>
	 * <p>
	 * @param name - this parameter should refer to the resource path
	 * relative to the class loader directory specified in constructor
	 * parameter for files. OR zip file entry name in case of jar file
	 * <br/> 
	 * For example, if plugins directory is located at <br/>
	 * C:/projects/someProject/plugins <br/>
	 * and we want to find file: <br/>
	 * C:/projects/someProject/plugins/img/someFile.jpg, <br/> 
	 * parameter name should be "img/someFile.jpg"
	 * If we are looking for a resource within one of the jar files
	 * included in plugins directory, then specify path to this resource
	 * within the jar file.
	 * </br>
	 * NOTE: first found resource will be returned.
	 * </p>
	 */
	public URL findResource (String name ) {
		Resource resource = findResourceLocation(name);
		if (resource != null) {
			try {
				return resource.getURL();
			}
			catch (MalformedURLException e) {
				/// @todo what to do with exception here?
			}
		}
		return null;
	}
	 
	/**
	 * Find a class with the specified name.
	 * <p>
	 * If duplicates occur within this class loader - the first one
	 * found will be returned. At this stage there is no way to inforce
	 * the order in which we search for classes, therefore there is no
	 * way to predict which one of the duplicates would be found first.
	 * </p>
	 * <p>
	 * Classes defined in classpath are loaded by default class loader 
	 * and take precedence to classes found in plugins path.
	 * </p>
	 * <p>
	 * Please note that although we do ignore multiple class definitions
	 * in a plugin path - it would more efficient if plugins path didn't 
	 * contain duplicates
	 * </p>
	 * <p>
	 * @param name - fully qualified class name.
	 * </p>
	 */
	public Class findClass(String name) throws ClassNotFoundException {
		// the ClassNotFoundException will not go up through the native ClassLoader code,
		// thus we use a member variable to indicate the class causing the error
		this.classNotFound = null;
		logger.entering("PluginClassLoader", "findClass", name);
		Resource resource = findResourceLocation(name.replace('.','/').replace('\\','/') + ".class");
		if (resource == null) {
			this.classNotFound = name;
			ClassNotFoundException exc = new ClassNotFoundException ("Couldn't find class with name " + name); 
			logger.throwing("PluginClassLoader", "findClass", exc);
			throw exc;
		}
		Class res = loadClass(resource);
		logger.exiting("PluginClassLoader", "findClass",res);
		return res;
	 }
	 
	 private Class loadClass (Resource resource) 
	 								throws ClassNotFoundException, 
	 								NoClassDefFoundError {
	 	Class resClass = null;
		logger.fine("Trying to load class for resource " + resource);
		try {
			byte [] b = resource.getData();
			resClass = defineClass(null, b, 0, b.length);
		} catch (LinkageError e) {
			if (e.getMessage().startsWith("duplicate class definition: ")) {
				String className = resource.getRelativePath().replace('/','.').replaceAll(".class", "");
				logger.fine("DUPLICATE class def for " + className);
				if (this.loadedClasses.containsKey(className)) {
					resClass = (Class) this.loadedClasses.get(className);
					logger.fine("Class " + className + " is loaded using " + this.getClass().getName());
				}
				else {
					resClass = this.getClass().getClassLoader().loadClass(className);
					logger.fine("Class " + className + " is loaded using " + this.getClass().getClassLoader().getClass().getName());
				}
			}
		} catch (IOException exc) {
			logger.throwing("PluginClassLoader", "loadClass", exc);
			throw new ClassNotFoundException("Couldn't find resource " + resource.getRelativePath(), exc);			
		}
		
		if (resClass == null) {
			String errMessage = "Couldn't find and load class '" + resource.getRelativePath() + "'";
			if(this.classNotFound != null) {
				errMessage +=  " since '" + this.classNotFound + "' could not be loaded.";
			}
			ClassNotFoundException exc = new ClassNotFoundException(errMessage);
			logger.throwing("PluginClassLoader", "loadClass", exc);
			throw exc;
		}
		
		this.loadedClasses.put(resClass.getName(), resClass);
		return resClass;
	 }

	private Resource findResourceLocation (String name) {
		name = name.replace('\\', '/');
		Iterator it = this.foundResources.iterator();
		while (it.hasNext()) {
			Resource curResource = (Resource) it.next();
			String curResourceName = curResource.getRelativePath().replace('\\','/');
			if (curResourceName.equals(name)) {
				return curResource;
			}
		}
		return null;
	}
	 
	 private void listAllFiles (File file) {
	 	File[] files = file.listFiles();
	 	for (int i = 0; i < files.length; i++) {
			File curFile = files[i];
			if (curFile.getName().endsWith("jar")) {
				List jarEntriesList = readArchive(curFile);
				Iterator it = jarEntriesList.iterator();
				while (it.hasNext()) {
					ZipEntry curEntry = (ZipEntry) it.next();
					JarResource jarResource = new JarResource(curFile, curEntry); 
					logger.fine("Found " + jarResource);
					this.foundResources.add(jarResource);
				}
			}
			FileResource fileResource = new FileResource(curFile); 
			logger.fine("Found " + fileResource);
			this.foundResources.add(fileResource);
			if (curFile.isDirectory()) {
				listAllFiles(curFile);
			}
		}	
	 }
	 
	 
	private List readArchive(File file) {
		List zipFileEntriesList = new ArrayList();
		try {
			ZipFile zipFile = new ZipFile(file);
			Enumeration enum = zipFile.entries();
			while (enum.hasMoreElements()) {
				ZipEntry curEntry = (ZipEntry) enum.nextElement();
				zipFileEntriesList.add(curEntry);
			}
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return zipFileEntriesList;
	}

	private String getPathRelativeToPluginsBaseDir (File file) {
		if (file.compareTo(this.pluginsDirLocation) > 0) {
			String result = "";
			File curFile = file;
			File parent = curFile.getParentFile();
			while (parent != null) {
				if (curFile.equals(this.pluginsDirLocation)) {
					break;
				}
				else {
					if (result.length() == 0) {
						result = curFile.getName();
					}
					else {
						result = curFile.getName() + File.separator + result;
					}
				}
				curFile = parent;
				parent = curFile.getParentFile();
			}
			return result;
		}
		return file.getName();
	}

}
