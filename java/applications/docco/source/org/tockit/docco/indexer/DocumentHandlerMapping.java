/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import org.tockit.docco.documenthandler.DocumentHandler;
import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.FileFilterFactory;

public class DocumentHandlerMapping {
	private DoccoFileFilter fileFilter;
	private DocumentHandler docHandler;
	
	public DocumentHandlerMapping(DoccoFileFilter fileFilter, DocumentHandler docHandler) {
		this.fileFilter = fileFilter;
		this.docHandler = docHandler;
	}
	
	public DocumentHandlerMapping(String serialForm) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		int firstColonIndex = serialForm.indexOf(':');
		int lastColonIndex = serialForm.lastIndexOf(':');
		String fileFilterClassName = serialForm.substring(0,firstColonIndex);
		String filterExpression = serialForm.substring(firstColonIndex + 1, lastColonIndex);
		String docHandlerClassName = serialForm.substring(lastColonIndex + 1);			
		Class fileFilterFactoryClass = Class.forName(fileFilterClassName);
		FileFilterFactory fileFilterFactory = (FileFilterFactory) fileFilterFactoryClass.newInstance();
		DocumentHandler docHandler = (DocumentHandler) Class.forName(docHandlerClassName).newInstance();
	}
	
	public String getSerialization() {
		return this.fileFilter.toSerializationString() + ":" + this.docHandler.getClass().getName();
	}
	
	public DocumentHandler getHandler() {
		return this.docHandler;
	}
	
	public DoccoFileFilter getFileFilter() {
		return this.fileFilter;
	}
	
	public String toString() {
		String str = "DocumentHandlerMapping: from " + this.fileFilter.toString() + 
						" to " + this.docHandler.getDisplayName();
		return str;
	}

}
