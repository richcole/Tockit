package query;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

public class QueryEngine {
	Searcher searcher;
	String defaultQueryField;
	Analyzer analyzer;
	
	public QueryEngine (String indexLocation, String defaultQueryField, Analyzer analyzer) throws IOException {
		this.searcher = new IndexSearcher(indexLocation);
		this.defaultQueryField = defaultQueryField;
		this.analyzer = analyzer;
	}
	
	public HitReferencesSet executeQuery (String queryString) throws ParseException, IOException {
		Query query = QueryParser.parse(queryString, this.defaultQueryField, this.analyzer);
		System.out.println("Searching for: " + query.toString("contents"));
		
		HitReferencesSet result = new HitReferencesSetImplementation();
		Hits hits = searcher.search(query);
		for (int i = 0; i < hits.length(); i++) {
			Document doc = hits.doc(i);
			HitReference hitRef = new HitReference(doc, hits.score(i));
			result.add(hitRef);
		}
		return result;
	}
	
	public void finishQueries() throws IOException {
		this.searcher.close();
	}
	
	public void breakQueryIntoTerms (String queryString) throws ParseException {
		Query query = QueryParser.parse(queryString, this.defaultQueryField, this.analyzer);
		System.out.println("query = " + query.toString("contents") + ", class = " + query.getClass());
		List querySegments = new LinkedList();
		breakIntoTerms(querySegments, query);
	}
	
	private void breakIntoTerms (List querySegments, Query query) {
		if (query instanceof TermQuery) {
			System.out.println("found segment: " + query.toString("contents"));
			querySegments.add(query);
		}
		else if (query instanceof BooleanQuery) {
			processBolleanQuery(querySegments, (BooleanQuery) query);
		}
		else {
			System.err.println("don't know about this type of query yet: " + query.getClass());
		}
		
	}
	
	private void processTermQuery(List querySegments, TermQuery query) {
		querySegments.add(query);
	}
	
	private void processBolleanQuery (List querySegments, BooleanQuery query) {
		BooleanClause[] clauses = query.getClauses();
		for (int i = 0; i < clauses.length; i++) {
			BooleanClause clause = clauses[i];
			breakIntoTerms(querySegments, clause.query);
		}
	}

}
