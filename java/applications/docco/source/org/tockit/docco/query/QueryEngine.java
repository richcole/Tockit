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
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searchable;
import org.apache.lucene.search.Searcher;
import org.tockit.docco.index.Index;
import org.tockit.docco.query.util.*;


public class QueryEngine {
    private Index[] indexes;
	private String defaultQueryField;
	private Analyzer analyzer;
	private QueryDecomposer queryDecomposer;
	
	public QueryEngine (Index[] indexes, String defaultQueryField, 
								Analyzer analyzer, QueryDecomposer queryDecomposer) 
								throws IOException {
		// we store only the locations, not the searcher since the searcher has to be
		// recreated if the index is updated (which happens from the other thread)
		this.indexes = indexes;
		this.defaultQueryField = defaultQueryField;
		this.analyzer = analyzer;
		this.queryDecomposer = queryDecomposer;
	}
	
	public QueryWithResultSet executeQueryUsingDecomposer (String queryString) throws ParseException, IOException {
		QueryWithResultSet queryResult = new QueryWithResultSetImplementation();
		List queryTermsCombinations = this.queryDecomposer.breakQueryIntoTerms(queryString);
		Searchable[] searchers = new Searchable[this.indexes.length];
		for (int i = 0; i < this.indexes.length; i++) {
            searchers[i] = new IndexSearcher(this.indexes[i].getIndexLocation().getPath());
        }
		Searcher searcher = new MultiSearcher(searchers);
		Iterator it = queryTermsCombinations.iterator();
		while (it.hasNext()) {
			QueryDecomposer.LabeledQuery cur = (QueryDecomposer.LabeledQuery) it.next();
			QueryWithResult qwr = executeQuery(searcher, cur.getQuery(), cur.getLabel());
			queryResult.add(qwr);
		}
		searcher.close();
		return queryResult;
	}
	
	private QueryWithResult executeQuery (Searcher searcher, Query query, String label) throws IOException {
		HitReferencesSet result = new HitReferencesSetImplementation();
		Hits hits = searcher.search(query);
		for (int i = 0; i < hits.length(); i++) {
			Document doc = hits.doc(i);
			HitReference hitRef = new HitReference(doc, hits.score(i));
			result.add(hitRef);
		}
		return new QueryWithResult(query, result, label);
	}
}
