/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.text.MessageFormat;

import org.tockit.docco.documenthandler.DocumentHandler;
import org.tockit.docco.documenthandler.DocumentHandlerRegistry;
import org.tockit.docco.filefilter.DoccoFileFilter;
import org.tockit.docco.filefilter.FileFilterFactory;
import org.tockit.docco.filefilter.FileFilterFactoryRegistry;
import org.tockit.docco.gui.GuiMessages;

public class DocumentHandlerMapping {
	private DoccoFileFilter fileFilter;
	private DocumentHandler docHandler;
	
	public DocumentHandlerMapping(DoccoFileFilter fileFilter, DocumentHandler docHandler) {
		if(fileFilter == null) {
			throw new IllegalArgumentException(GuiMessages.getString("DocumentHandlerMapping.illegalArgumentMessageFilterMustNotBeNull.text")); //$NON-NLS-1$
		}
		if(docHandler == null) {
			throw new IllegalArgumentException(GuiMessages.getString("DocumentHandlerMapping.illegalArgumentMessageDocumentHandlerMustNotBeNull.text")); //$NON-NLS-1$
		}

		this.fileFilter = fileFilter;
		this.docHandler = docHandler;
	}
	
	public DocumentHandlerMapping(String serialForm) throws ClassNotFoundException {
		int firstColonIndex = serialForm.indexOf(':');
		int lastColonIndex = serialForm.lastIndexOf(':');
		String fileFilterClassName = serialForm.substring(0,firstColonIndex);
		String filterExpression = serialForm.substring(firstColonIndex + 1, lastColonIndex);
		String docHandlerClassName = serialForm.substring(lastColonIndex + 1);
		FileFilterFactory fileFilterFactory = FileFilterFactoryRegistry.getFileFilterFactoryByName(fileFilterClassName);
		if(fileFilterFactory == null) {
			throw new ClassNotFoundException(MessageFormat.format(GuiMessages.getString("DocumentHandlerMapping.fileFilterTypeNotFoundError.text"), new Object[]{fileFilterClassName})); //$NON-NLS-1$
		}
		this.docHandler = DocumentHandlerRegistry.getDocumentHandlerByName(docHandlerClassName);
		if(docHandler == null) {
			throw new ClassNotFoundException(MessageFormat.format(GuiMessages.getString("DocumentHandlerMapping.documentHandlerNotFoundError.text"), new Object[]{docHandlerClassName})); //$NON-NLS-1$
		}
		this.fileFilter = fileFilterFactory.createNewFilter(filterExpression);
	}
	
	public String getSerialization() {
		return this.fileFilter.toSerializationString() + ":" + this.docHandler.getClass().getName(); //$NON-NLS-1$
	}
	
	public DocumentHandler getHandler() {
		return this.docHandler;
	}
	
	public DoccoFileFilter getFileFilter() {
		return this.fileFilter;
	}
	
	public String toString() {
		String str = "DocumentHandlerMapping: from " + this.fileFilter.toString() +  //$NON-NLS-1$
						" to " + this.docHandler.getDisplayName(); //$NON-NLS-1$
		return str;
	}
}
