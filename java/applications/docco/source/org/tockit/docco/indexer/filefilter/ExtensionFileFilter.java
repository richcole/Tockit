/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer.filefilter;

import java.io.File;


public class ExtensionFileFilter implements DoccoFileFilter {
	String extensionString;

	public ExtensionFileFilter (String extensionString) {
		this.extensionString = extensionString;
	}
	
	public boolean accept(File file) {
		try {
			String fileExtension = FileExtensionExtractor.getExtension(file.getAbsoluteFile());
			if (fileExtension.equalsIgnoreCase(extensionString)) {
				return true;
			}
		}
		catch (NotFoundFileExtensionException e) {
			return false;
		}
		return false;
	}
	
	public String getDisplayString () {
		String str = this.extensionString;
		return str;
	}
	
	public String toString() {
		String str = "Extension FileFilter for extension " + this.extensionString;
		return str;
	}
	
	

}
