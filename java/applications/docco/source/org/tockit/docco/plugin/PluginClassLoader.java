/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.plugin;

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
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Class loader used to load resources found in the plugins directory.
 * 
 * This class loader will recurse through given directory and list
 * all resources found, including all resources contained in any jar
 * files found.
 * 
 * For more information on loading classes or resources 
 * @see findClass(String name) and findResource(String name)   
 */
public class PluginClassLoader extends ClassLoader {

	private static final Logger logger = Logger.getLogger(PluginClassLoader.class.getName());

	private File pluginsDirLocation;

	private List foundResources = new ArrayList();
	
	/**
	 * keys - resource.getRelativePath()
	 * values - corresponding class
	 */
	private Hashtable loadedClassesMap = new Hashtable();
	
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
		logger.entering("PluginClassLoader", "Constructor", file.getAbsoluteFile());
		
		if (!file.exists()) {
			throw new FileNotFoundException("Couldn't locate specified plugins directory: " + file.getAbsolutePath());
		}
		
		listAllFiles(file);	
		System.out.println("\nPluginClassLoader: " + this);
		System.out.println("default class loader: " + this.getClass().getClassLoader());
		System.out.println("parent class loader: " + this.getParent());
		System.out.println("system class loader: " + ClassLoader.getSystemClassLoader());
		

		logger.exiting("PluginClassLoader", "Constructor", "found num of resources: " + this.foundResources.size());
	}
	
	public Class[] findClassesImplementingGivenIterface (Class interfaceClass) 
						throws ClassNotFoundException, NoClassDefFoundError {
		List result = new ArrayList();
		Iterator it = this.foundResources.iterator();
		while (it.hasNext()) {
			Resource curResource = (Resource) it.next();
			String name = curResource.getRelativePath();
			if (name.endsWith(".class")) {
				String newName = name.replaceAll(".class","");
				Class curClass = loadClass(curResource);
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
	 * Find a resource with given name in this class loader. This
	 * method will return first resource found with the given name.
	 * In case of duplicate resources with the same name there is no 
	 * guarantee which one will be found first.
	 * @param name - this parameter should refer to the resource path
	 * relative to the class loader directory specified in constructor
	 * parameter for files. OR zip file entry name in case of jar file 
	 * For example, if plugins directory is located at 
	 * C:/projects/someProject/plugins 
	 * and we want to find file:
	 * C:/projects/someProject/plugins/img/someFile.jpg, 
	 * parameter name should be "img/someFile.jpg"
	 * If we are looking for a resource within one of the jar files
	 * included in plugins directory, then specify path to this resource
	 * within the jar file.
	 * NOTE: first found resource will be returned.
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
	 * @param name - fully qualified class name.
	 * If duplicates occur within this class loader - the first one
	 * found will be returned. At this stage there is no way to inforce
	 * the order in which we search for classes, therefore there is no
	 * way to predict which one of the duplicates would be found first.
	 */
	public Class findClass(String name) throws ClassNotFoundException {
		logger.entering("PluginClassLoader", "findClass", name);
		Resource resource = findResourceLocation(name.replace('.','/').replace('\\','/') + ".class");
		if (resource == null) {
			throw new ClassNotFoundException ("Couldn't find class with name " + name);
		}
		return loadClass(resource);
	 }
	 
	 private Class loadClass (Resource resource) 
	 								throws ClassNotFoundException, 
	 								NoClassDefFoundError {
	 	if (this.loadedClassesMap.containsKey(resource.getRelativePath())) {
	 		return (Class) this.loadedClassesMap.get(resource.getRelativePath());
	 	}
//	 	try {
//	 		String className = resource.getRelativePath().replace('/','.').replaceAll(".class", "");
//	 		Class resClass = this.getClass().getClassLoader().loadClass(className);
//	 		System.out.println(className + " - YES");	 
//	 		return resClass;
//	 	}
//	 	catch (ClassNotFoundException e) {
//			System.out.println(resource.getRelativePath() + " - NO");	 		
//	 	}
		try {
			byte [] b = resource.getData();
			Class resClass = defineClass(null, b, 0, b.length);
			this.loadedClassesMap.put(resource.getRelativePath(), resClass);
			return resClass;	 	
		} catch (IOException e) {
			throw new ClassNotFoundException("Couldn't find resource " + resource.getRelativePath(), e);			
		}
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
