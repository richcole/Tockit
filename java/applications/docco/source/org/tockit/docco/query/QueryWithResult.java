/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.query;

import java.util.Set;

import org.apache.lucene.search.Query;


public class QueryWithResult {

	private Query query;
	private Set resultSet;
	private String label;
	
	public QueryWithResult (Query query, Set resultSet, String label) {
		this.query = query;
		this.resultSet = resultSet;
		this.label = label;
	}
	
	public Query getQuery() {
		return query;
	}

	public Set getResultSet() {
		return resultSet;
	}
	
	public String getLabel() {
		return label;
	}

	public String toString() {
		String str = "QueryWithResult [query:" + query + ", result: " + resultSet + " ]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return str;
	}

}
