/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class QueryDecomposer {
	private String defaultQueryField;
	private Analyzer analyzer;
	
	public QueryDecomposer (String defaultQueryField, Analyzer analyzer) {
		this.defaultQueryField = defaultQueryField;
		this.analyzer = analyzer;
	}

	public List breakQueryIntoTerms (String queryString) throws ParseException {
		Query query = QueryParser.parse(queryString, this.defaultQueryField, new StandardAnalyzer());
        if (query instanceof BooleanQuery) {
        	return processBooleanQuery((BooleanQuery) query);
        }
        else {
        	ArrayList result = new ArrayList();
        	result.add(query);
            return result;
        }
	}	
	
	private List processBooleanQuery (BooleanQuery query) {
		ArrayList result = new ArrayList();
		BooleanClause[] clauses = query.getClauses();
		for (int i = 0; i < clauses.length; i++) {
			BooleanQuery newQuery = new BooleanQuery();
			newQuery.add(clauses[i]); 
			result.add(newQuery);	
		}
		return result;
	}
}
