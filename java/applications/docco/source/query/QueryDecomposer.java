/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package query;

import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class QueryDecomposer {
	private String defaultQueryField;
	private Analyzer analyzer;
	List querySegments = new LinkedList();
	
	public QueryDecomposer (String defaultQueryField, Analyzer analyzer) {
		this.defaultQueryField = defaultQueryField;
		this.analyzer = analyzer;
	}
	
	public List breakQueryIntoTerms (String queryString) throws ParseException {
		this.querySegments = new LinkedList();
		Query query = QueryParser.parse(queryString, this.defaultQueryField, new StandardAnalyzer());
		SimpleQueryReference queryRef = new SimpleQueryReference(query);
		breakIntoTerms(queryRef);
		return this.querySegments;
	}
	
	private void breakIntoTerms (SimpleQueryReference queryRef) {
		Query query = queryRef.getQuery();
		if (query instanceof TermQuery) {
			this.querySegments.add(queryRef);
		}
		else if (query instanceof BooleanQuery) {
			processBolleanQuery(queryRef);
		}
		else if (query instanceof PhraseQuery) {
			this.querySegments.add(queryRef);
		}
		else {
			System.err.println("don't know about this type of query yet: " + queryRef.getClass());
		}
	}
	
	private void processBolleanQuery (SimpleQueryReference queryRef) {
		BooleanQuery query = (BooleanQuery) queryRef.getQuery();
		BooleanClause[] clauses = query.getClauses();
		for (int i = 0; i < clauses.length; i++) {
			BooleanClause clause = clauses[i];
			SimpleQueryReference curQueryRef;
			if  ( (!clause.required) && (!clause.prohibited) ) {
				curQueryRef = new SimpleQueryReference(clause.query);	
			}
			else {
				curQueryRef = new SimpleQueryReference(clause.query, clause.required, clause.prohibited);
			}
			breakIntoTerms(curQueryRef);
		}
	}
}
