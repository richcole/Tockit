/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer.filefilter;

import java.io.File;

public class FileExtensionExtractor {
	public static String getExtension (File file) throws NotFoundFileExtensionException {
		String fileName = file.getName();
		int index = fileName.lastIndexOf(".") + 1;
		if (index > 0) {
			return fileName.substring(index, fileName.length()).toLowerCase();
		}
		else {
			throw new NotFoundFileExtensionException("Couldn't extract " +
						"extension from file " + file.getAbsolutePath());
		}
	}
}
