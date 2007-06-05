/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco;

import java.text.MessageFormat;
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
	public static final String ANALYZERS_PROPERTIES_FILE_NAME = "/analyzers.properties"; //$NON-NLS-1$
	public static final String DEFAULT_ANALYZER_NAME;
	public static final Analyzer DEFAULT_ANALYZR;

	public static final String FIELD_QUERY_BODY = "body"; //$NON-NLS-1$
	
	public static final String FIELD_DOC_URL = "URL"; //$NON-NLS-1$
	public static final String FIELD_DOC_SUMMARY = "summary"; //$NON-NLS-1$
	public static final String FIELD_DOC_TITLE = "title"; //$NON-NLS-1$
	public static final String FIELD_DOC_KEYWORD = "keyword"; //$NON-NLS-1$
	public static final String FIELD_DOC_MODIFICATION_DATE = "mod_date"; //$NON-NLS-1$
	public static final String FIELD_DOC_CREATION_DATE = "creation_date"; //$NON-NLS-1$
	public static final String FIELD_DOC_AUTHOR = "author"; //$NON-NLS-1$
	public static final String FIELD_DOC_TYPE = "type"; //$NON-NLS-1$
	public static final String FIELD_DOC_SIZE = "size"; //$NON-NLS-1$
	public static final String FIELD_DOC_PATH = "path"; //$NON-NLS-1$
	public static final String FIELD_DOC_EXTENSION = "ext"; //$NON-NLS-1$
	public static final String FIELD_DOC_NAME = "name"; //$NON-NLS-1$
	/**
	 * As opposed to the constant above, this one is used to store the path as separte words for indexing.
	 * This is to experiment with looking at occurrances of words in different paths. The wildcards won't
	 * help too much since they are not allowed at the beginning.
	 */
	public static final String FIELD_DOC_PATH_WORDS = "path_words"; //$NON-NLS-1$
	
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
			analyserClassName = ANALYZERS.getProperty("default"); //$NON-NLS-1$
			if(analyserClassName == null) {
				System.err.println(CliMessages.getString("GlobalConstants.analyserListHasNoDefaultWarning.text")); //$NON-NLS-1$
			} else {
				analyzerName = ANALYZERS.getProperty(analyserClassName);
				if(analyzerName == null) {
					System.err.println(MessageFormat.format(CliMessages.getString("GlobalConstants.analyserListHasUnknownDefaultWarning.text"), new Object[]{analyserClassName})); //$NON-NLS-1$
				}
			}
			ANALYZERS.remove("default"); //$NON-NLS-1$
		} catch (Exception e) {
			System.err.println(MessageFormat.format(CliMessages.getString("GlobalConstants.failedToLoadAnalyserListWarning.text"), new Object[]{ANALYZERS_PROPERTIES_FILE_NAME})); //$NON-NLS-1$
			e.printStackTrace();
		}
		DEFAULT_ANALYZER_NAME = analyzerName;
		Analyzer analyzer;
		try {
			analyzer = (Analyzer) Class.forName(analyserClassName).newInstance();
		} catch (Exception e) {
			System.err.println(MessageFormat.format(CliMessages.getString("GlobalConstants.failedToInitializeDefaultAnalyserWarning.text"), new Object[]{DEFAULT_ANALYZER_NAME})); //$NON-NLS-1$
			e.printStackTrace();
			analyzer = new StandardAnalyzer();
		}
		DEFAULT_ANALYZR = analyzer;
	}
}
