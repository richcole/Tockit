/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class GlobalConstants {
	public static final Analyzer DEFAULT_ANALYZER = new StandardAnalyzer();
	
	public static final String FIELD_QUERY_BODY = "body";
	
	public static final String FIELD_DOC_URL = "URL";
	public static final String FIELD_DOC_SUMMARY = "summary";
	public static final String FIELD_DOC_TITLE = "title";
	public static final String FIELD_DOC_KEYWORDS = "keywords";
	public static final String FIELD_DOC_MODIFICATION_DATE = "mod_date";
	public static final String FIELD_DOC_CREATION_DATE = "creation_date";
	public static final String FIELD_DOC_AUTHOR = "author";
	public static final String FIELD_DOC_TYPE = "type";
	public static final String FIELD_DOC_SIZE = "size";
	public static final String FIELD_DOC_PATH = "path";
	public static final String FIELD_DOC_EXTENSION = "ext";
	public static final String FIELD_DOC_NAME = "name";
	/**
	 * As opposed to the constant above, this one is used to store the path as separte words for indexing.
	 * This is to experiment with looking at occurrances of words in different paths. The wildcards won't
	 * help too much since they are not allowed at the beginning.
	 */
	public static final String FIELD_DOC_PATH_WORDS = "path_words";
	
	/**
	 * @todo
	 * Not sure where these settings should be kept... It would be good to load them 
	 * dynamically somehow (check for implementators of DoccoFileFilter?).
	 */
	public static final String[] FILE_FILTER_IMPLEMENTAIONS = {
										"org.tockit.docco.filefilter.ExtensionFileFilter",
										"org.tockit.docco.filefilter.RegularExpresionExtensionFileFilter"}; 

	public static final String[] DEFAULT_DOC_HANDLER_IMPLEMENTATIONS = {
										"org.tockit.docco.documenthandler.HtmlDocumentHandler",
										"org.tockit.docco.documenthandler.XmlDocumentHandler",
										"org.tockit.docco.documenthandler.PlainTextDocumentHandler"
										};
}
