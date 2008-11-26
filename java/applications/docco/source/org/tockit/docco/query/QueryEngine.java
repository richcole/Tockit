/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package org.tockit.docco.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.tockit.docco.index.Index;


public class QueryEngine {
    private final Index[] indexes;
	private final QueryDecomposer queryDecomposer;
    private final String defaultQueryField;
	
	public QueryEngine (Index[] indexes, String defaultQueryField) {
		// we store only the locations, not the searcher since the searcher has to be
		// recreated if the index is updated (which happens from the other thread)
		this.indexes = indexes;
        this.defaultQueryField = defaultQueryField;
		this.queryDecomposer = new QueryDecomposer(defaultQueryField);
	}
	
	public QueryWithResult[] executeQueryUsingDecomposer (String queryString) throws ParseException, IOException {
		List queryTermsCombinations = this.queryDecomposer.breakQueryIntoTerms(queryString);
		QueryWithResult[] queryResults = new QueryWithResult[queryTermsCombinations.size()];
        boolean firstRun = true;
		for (int i = 0; i < this.indexes.length; i++) {
            Searcher searcher = new IndexSearcher(this.indexes[i].getIndexLocation().getPath());
            Iterator it = queryTermsCombinations.iterator();
            int pos = 0;
            while (it.hasNext()) {
                QueryDecomposer.LabeledQuery cur = (QueryDecomposer.LabeledQuery) it.next();
                QueryParser parser = new QueryParser(this.defaultQueryField, this.indexes[i].getAnalyzer());
                Query adjustedQuery = parser.parse(cur.getQuery().toString(this.defaultQueryField));
                QueryWithResult qwr = executeQuery(searcher, adjustedQuery, cur.getLabel());
                if(firstRun) {
                    queryResults[pos] = qwr;
                } else {
                    QueryWithResult old = queryResults[pos];
                    old.getResultSet().addAll(qwr.getResultSet());
                }
                pos++;
            }
            firstRun = false;
            searcher.close();
        }
		return queryResults;
	}
	
	private QueryWithResult executeQuery (final Searcher searcher, Query query, String label) throws IOException {
		final Set result = new HashSet();
		final List exceptions = new ArrayList();
		// TODO deal with the exceptions somehow
		searcher.search(query, new HitCollector(){
            public void collect(int docId, float score) {
                try {
                    Document doc = searcher.doc(docId);
                    HitReference hitRef = new HitReference(doc, score);
                    result.add(hitRef);
                } catch (CorruptIndexException e) {
                    exceptions.add(e);
                    e.printStackTrace();
                } catch (IOException e) {
                    exceptions.add(e);
                    e.printStackTrace();
                }
            }});
		return new QueryWithResult(query, result, label);
	}
}
