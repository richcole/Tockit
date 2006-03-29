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

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.tockit.docco.GlobalConstants;

public class QueryDecomposer {
	public class LabeledQuery {
		private String label;
		private Query query;
		private LabeledQuery(String label, Query query) {
			this.label = label;
			this.query = query; 
		}
		public String getLabel() {
			return label;
		}
		public Query getQuery() {
			return query;
		}
	}
	
	private QueryParser parser;
	
	public QueryDecomposer(String defaultQueryField) {
		this.parser = new QueryParser(defaultQueryField, new StandardAnalyzer());
	}

	public List breakQueryIntoTerms (String queryString) throws ParseException {
		try {
			Query query = this.parser.parse(queryString);
			if (query instanceof BooleanQuery) {
				return processBooleanQuery((BooleanQuery) query);
			}
			else {
				ArrayList result = new ArrayList();
				result.add(new LabeledQuery(queryString, query));
				return result;
			}
		} catch (ParseException pexc) {
			ErrorDialog.showError(null, pexc, "Query could not be parsed");
		}
		return new ArrayList();
	}	
	
	private List processBooleanQuery (BooleanQuery query) throws ParseException {
		ArrayList result = new ArrayList();
		BooleanClause[] clauses = query.getClauses();
		for (int i = 0; i < clauses.length; i++) {
			BooleanClause curClause = clauses[i];
			BooleanQuery newQuery = new BooleanQuery();
			newQuery.add(curClause); 
			if(curClause.isProhibited()) {
				boolean havePositive = false;
				for (int j = 0; j < clauses.length; j++) {
					BooleanClause clause = clauses[j];
					if(!clause.isProhibited()) {
						// avoid getting required clauses into the negated ones -- otherwise we get
						// new implications
						BooleanClause newClause = new BooleanClause(clause.getQuery(), BooleanClause.Occur.SHOULD);
						newQuery.add(newClause);
						havePositive = true;					
					}
				}
				if(!havePositive) {
					throw new ParseException("Query does not contain positive clause.");
				}
			}
			// TODO this next bit handles only the first clause in a conjunction, we need something more complex here
			String label = curClause.getQuery().toString();
			if(label.startsWith(GlobalConstants.FIELD_QUERY_BODY + ":")) {
				label = label.substring(GlobalConstants.FIELD_QUERY_BODY.length() + 1);				
			}
			// mark prohibited ones, we don't care about marking required ones since it
			// doesn't make a difference for us 
			if(curClause.isProhibited()) {
				label = "-" + label;
			}
			result.add(new LabeledQuery(label, newQuery));
		}
		return result;
	}
}
