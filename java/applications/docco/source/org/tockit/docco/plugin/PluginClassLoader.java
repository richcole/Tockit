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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class PluginClassLoader extends ClassLoader {
	private static final Logger logger = Logger.getLogger(PluginClassLoader.class.getName());
	
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
			if (this.file.compareTo(pluginsDirLocation) > 0) {
				String result = "";
				File curFile = this.file;
				File parent = curFile.getParentFile();
				while (parent != null) {
					if (curFile.equals(pluginsDirLocation)) {
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
	
	private List foundResources = new ArrayList();
	private File pluginsDirLocation;
		
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
		logger.exiting("PluginClassLoader", "Constructor", "found num of resources: " + this.foundResources.size());		
	}
	
	public Class[] findClassesImplementingGivenIterface (Class interfaceClass) {
		List result = new ArrayList();
		Iterator it = this.foundResources.iterator();
		while (it.hasNext()) {
			Resource curResource = (Resource) it.next();
			String name = curResource.getRelativePath();
			if (name.endsWith(".class")) {
				String newName = name.replaceAll(".class","");
				try {
					Class curClass = loadClass(curResource);
					Class[] interfaces = curClass.getInterfaces();
					for (int i = 0; i < interfaces.length; i++) {
						Class class1 = interfaces[i];
						if (class1.equals(interfaceClass)) {
							result.add(curClass);
						}
					}
				} catch (ClassNotFoundException e) {
					// @todo deal with exception
					e.printStackTrace();
				}
			}
		}
		return (Class[]) result.toArray(new Class[result.size()]);
	}
	
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
	 
	public Class findClass(String name) throws ClassNotFoundException {
		logger.entering("PluginClassLoader", "findClass", name);
		Resource resource = findResourceLocation(name.replace('.','/') + ".class");
		if (resource == null) {
			throw new ClassNotFoundException ("Couldn't find class with name " + name);
		}
		return loadClass(resource);
	 }
	 
	 private Class loadClass (Resource resource) throws ClassNotFoundException {
		try {
			byte [] b = resource.getData();
			Class resClass = defineClass(null, b, 0, b.length);
			return resClass;	 	
		} catch (IOException e) {
			throw new ClassNotFoundException("Couldn't find resource " + resource.getRelativePath(), e);			
		} catch (NoClassDefFoundError e) {
			throw new ClassNotFoundException("Errors reading resource " + resource.getRelativePath(), e);			
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

}
