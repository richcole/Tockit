/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package query;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;

public class QueryEngine {
	Searcher searcher;
	String defaultQueryField;
	Analyzer analyzer;
	
	public QueryEngine (String indexLocation, String defaultQueryField, Analyzer analyzer) throws IOException {
		this.searcher = new IndexSearcher(indexLocation);
		this.defaultQueryField = defaultQueryField;
		this.analyzer = analyzer;
	}
	
	public QueryWithResult executeQuery (String queryString) throws ParseException, IOException {
		Query query = QueryParser.parse(queryString, this.defaultQueryField, this.analyzer);
		return new QueryWithResult(query, executeQuery(query));
	}
	
	private HitReferencesSet executeQuery (Query query) throws ParseException, IOException {
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
	

}
