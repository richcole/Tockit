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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PluginClassLoader extends ClassLoader {
	private File file;
	private List foundFiles = new ArrayList();
		
	/**
	 * @param file - path where to start looking for classes
	 */
	public PluginClassLoader(File file) throws FileNotFoundException {
		super();
		
		if (!file.exists()) {
			throw new FileNotFoundException("Couldn't locate specified plugins directory: " + file.getAbsolutePath());
		}
		
		this.file = file;
		System.out.println("STARTING AT " + file.getAbsolutePath());
		listAllFiles(this.file);		
	}
	
	public Class[] findClasses (Class interfaceClass) {
		List result = new ArrayList();
		Iterator it = this.foundFiles.iterator();
		while (it.hasNext()) {
			File curFile = (File) it.next();
			String name = curFile.getName();
			if (name.endsWith(".class")) {
				String newName = name.replaceAll(".class","");
				try {
					Class curClass = loadClass(curFile);
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
	
	public Class findClass(String name) throws ClassNotFoundException {
		File file = findFileLocation(name);
		if (file == null) {
			throw new ClassNotFoundException ("Couldn't find class with name " + name);
		}
		return loadClass(file);
	 }
	 
	 private Class loadClass (File file) throws ClassNotFoundException {
		try {
			byte [] b = loadClassData(file);
			return defineClass(null, b, 0, b.length);	 	
		} catch (FileNotFoundException e) {
			throw new ClassNotFoundException("Couldn't find class at specified location ", e);
		} catch (IOException e) {
			throw new ClassNotFoundException("Errors reading class " + file.getAbsolutePath(), e);			
		}
	 }

	 private byte[] loadClassData (File file) 
	 								throws FileNotFoundException,
	 								IOException {
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i;
		while( (i = in.read()) != -1) {
		   out.write(i);
		}	 	
		return out.toByteArray();
	 }

	private File findFileLocation (String name) {
		Iterator it = this.foundFiles.iterator();
		while (it.hasNext()) {
			File curFile = (File) it.next();
			if (curFile.getName().equals(name)) {
				return curFile;
			}
			if (curFile.getName().equals(name + ".class")) {
				return curFile;
			}
		}
		return null;
	}
	 
	 private void listAllFiles (File file) {
	 	File[] files = file.listFiles();
	 	for (int i = 0; i < files.length; i++) {
			File curFile = files[i];
			this.foundFiles.add(curFile);
			if (curFile.isDirectory()) {
				listAllFiles(curFile);
			}
		}	
	 }
	 
	 

}
