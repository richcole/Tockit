/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer.filefilter;

import java.io.File;
import java.util.regex.Pattern;


public class RegularExpresionExtensionFileFilter implements DoccoFileFilter {
	String regexString;

	public RegularExpresionExtensionFileFilter (String regexString) {
		this.regexString = regexString;
	}
	
	public boolean accept(File file) {
		try {
			String fileExtension = FileExtensionExtractor.getExtension(file.getAbsoluteFile());
			if (Pattern.matches(regexString, fileExtension)) {
				return true;
			}
		}
		catch (NotFoundFileExtensionException e) {
			return false;
		}
		return false;
	}
	
	public String getFilteringString () {
		return this.regexString;
	}

	public String getDisplayName() {
		return "Filter using Regular Expression File Extensions";
	}
	
	public String toString() {
		String str = "Extension FileFilter for extension " + this.regexString;
		return str;
	}

	
	

}
