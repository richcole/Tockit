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
	private static final String INDEX_DIR = System.getProperty("user.dir") + System.getProperty("file.separator") + ".doccoIndex";	
	private static final String DEFAULT_INDEX_NAME = "test";

	public static final String DEFAULT_INDEX_LOCATION = INDEX_DIR + System.getProperty("file.separator") + DEFAULT_INDEX_NAME;
	public static final Analyzer DEFAULT_ANALYZER = new StandardAnalyzer();
	
	public static final String FIELD_QUERY_BODY = "body";
	
	public static final String FIELD_DOC_URL = "URL";
	public static final String FIELD_DOC_SUMMARY = "summary";
	public static final String FIELD_DOC_TITLE = "title";
	public static final String FIELD_DOC_KEYWORDS = "keywords";
	public static final String FIELD_DOC_DATE = "mod_date";
	public static final String FIELD_DOC_AUTHOR = "author";
	public static final String FIELD_DOC_TYPE = "type";
	public static final String FIELD_DOC_SIZE = "size";
	public static final String FIELD_DOC_PATH = "path";
}
