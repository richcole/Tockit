/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package query;

import org.apache.lucene.search.Query;

public class QueryWithResult {

	private Query query;
	private HitReferencesSet resultSet;
	
	public QueryWithResult (Query query, HitReferencesSet resultSet) {
		this.query = query;
		this.resultSet = resultSet;
	}
	
	public Query getQuery() {
		return query;
	}

	public HitReferencesSet getResultSet() {
		return resultSet;
	}
	
	public String toString() {
		String str = "QueryWithResult [query:" + query + ", result: " + resultSet + " ]";
		return str;
	}

}
