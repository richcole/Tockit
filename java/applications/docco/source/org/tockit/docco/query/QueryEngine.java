/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package org.tockit.docco.query;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.tockit.docco.query.util.*;


public class QueryEngine {
	private Searcher searcher;
	private String defaultQueryField;
	private Analyzer analyzer;
	private QueryDecomposer queryDecomposer;
	
	public QueryEngine (String indexLocation, String defaultQueryField, 
								Analyzer analyzer, QueryDecomposer queryDecomposer) 
								throws IOException {
		this.searcher = new IndexSearcher(indexLocation);
		this.defaultQueryField = defaultQueryField;
		this.analyzer = analyzer;
		this.queryDecomposer = queryDecomposer;
	}
	
	public QueryWithResultSet executeQueryUsingDecomposer (String queryString) throws ParseException, IOException {
		QueryWithResultSet queryResult = new QueryWithResultSetImplementation();
		List queryTermsCombinations = this.queryDecomposer.breakQueryIntoTerms(queryString);
		Iterator it = queryTermsCombinations.iterator();
		while (it.hasNext()) {
			Query cur = (Query) it.next();
			QueryWithResult qwr = executeQuery(cur);
			queryResult.add(qwr);
		}
		return queryResult;
	}
	
	private QueryWithResult executeQuery (Query query) throws IOException {
		HitReferencesSet result = new HitReferencesSetImplementation();
		Hits hits = searcher.search(query);
		for (int i = 0; i < hits.length(); i++) {
			Document doc = hits.doc(i);
			HitReference hitRef = new HitReference(doc, hits.score(i));
			result.add(hitRef);
		}
		return new QueryWithResult(query, result);
	}
	
	public void finishQueries() throws IOException {
		this.searcher.close();
	}
}
