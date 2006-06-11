/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco;

import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.tockit.docco.documenthandler.DocumentHandler;
import org.tockit.docco.documenthandler.HtmlDocumentHandler;
import org.tockit.docco.documenthandler.OpenDocumentFormatDocumentHandler;
import org.tockit.docco.documenthandler.OpenOffice1DocumentHandler;
import org.tockit.docco.documenthandler.PlainTextDocumentHandler;
import org.tockit.docco.documenthandler.RtfDocumentHandler;
import org.tockit.docco.documenthandler.XmlDocumentHandler;

public class GlobalConstants {
	public static final String ANALYZERS_PROPERTIES_FILE_NAME = "/analyzers.properties";
	public static final String DEFAULT_ANALYZER_NAME;
	public static final Analyzer DEFAULT_ANALYZR;

	public static final String FIELD_QUERY_BODY = "body";
	
	public static final String FIELD_DOC_URL = "URL";
	public static final String FIELD_DOC_SUMMARY = "summary";
	public static final String FIELD_DOC_TITLE = "title";
	public static final String FIELD_DOC_KEYWORD = "keyword";
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
	
	// TODO load this from a ressource file, too
	public static final DocumentHandler[] DEFAULT_DOC_HANDLER_IMPLEMENTATIONS = {
										new HtmlDocumentHandler(),
										new XmlDocumentHandler(),
                                        new OpenOffice1DocumentHandler(),
                                        new OpenDocumentFormatDocumentHandler(),
										new PlainTextDocumentHandler(),
										new RtfDocumentHandler()
										};

	public static final Properties ANALYZERS = new Properties();
	
	static {
		String analyzerName = null;
		String analyserClassName = null;
		try {
			ANALYZERS.load(GlobalConstants.class.getResourceAsStream(ANALYZERS_PROPERTIES_FILE_NAME));
			analyserClassName = ANALYZERS.getProperty("default");
			if(analyserClassName == null) {
				System.err.println("Could not find default entry in analyzer file, default will be arbitrary");
			} else {
				analyzerName = ANALYZERS.getProperty(analyserClassName);
				if(analyzerName == null) {
					System.err.println("Could not find setup for default entry (" + analyserClassName +
							") in analyzer file, default will be arbitrary");
				}
			}
			ANALYZERS.remove("default");
		} catch (Exception e) {
			System.err.println("Could not load analyzer list ('" + ANALYZERS_PROPERTIES_FILE_NAME + "')");
			e.printStackTrace();
		}
		DEFAULT_ANALYZER_NAME = analyzerName;
		Analyzer analyzer;
		try {
			analyzer = (Analyzer) Class.forName(analyserClassName).newInstance();
		} catch (Exception e) {
			System.err.println("Could not initialize default analyzer class (" + DEFAULT_ANALYZER_NAME + "), " +
					"falling back to StandardAnalyzer");
			e.printStackTrace();
			analyzer = new StandardAnalyzer();
		}
		DEFAULT_ANALYZR = analyzer;
	}
}
