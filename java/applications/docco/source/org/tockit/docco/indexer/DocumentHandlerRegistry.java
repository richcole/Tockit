/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.util.LinkedList;
import java.util.List;

import org.tockit.docco.documenthandler.HtmlDocumentHandler;
import org.tockit.docco.documenthandler.PlainTextDocumentHandler;
import org.tockit.docco.documenthandler.XmlDocumentHandler;
import org.tockit.docco.filefilter.ExtensionFileFilterFactory;

public class DocumentHandlerRegistry {
	private static final DocumentHandlerMapping[] DEFAULT_MAPPINGS = new DocumentHandlerMapping[]{
		new DocumentHandlerMapping(new ExtensionFileFilterFactory().createNewFilter("html;htm"),
								   new HtmlDocumentHandler()),
		new DocumentHandlerMapping(new ExtensionFileFilterFactory().createNewFilter("txt"),
								   new PlainTextDocumentHandler()),
		new DocumentHandlerMapping(new ExtensionFileFilterFactory().createNewFilter("xml"),
								   new XmlDocumentHandler())
	};
	
	private List docHandlersList = new LinkedList();

	public DocumentHandlerRegistry () {
		for (int i = 0; i < DEFAULT_MAPPINGS.length; i++) {
			DocumentHandlerMapping mapping = DEFAULT_MAPPINGS[i];
			this.docHandlersList.add(mapping);
		}
	}
	
	public DocumentHandlerRegistry (String[] mappings) {
		try {
			for (int i = 0; i < mappings.length; i++) {
            	docHandlersList.add(new DocumentHandlerMapping(mappings[i]));
            }
		}
		catch (Exception e) {
			// @todo what to do with these exceptions here?!!
			e.printStackTrace();
		}
	}

	public List getDocumentMappingList() {
		return this.docHandlersList; 	
	}
	
	public void setDocumentMappingList(List documentMappingslist) {
		this.docHandlersList = documentMappingslist; 	
	}
}
