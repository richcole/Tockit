/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package events;

import org.tockit.events.Event;

import query.util.QueryWithResultSet;


public class QueryFinishedEvent implements Event{
	QueryWithResultSet queryResult;
	
	public QueryFinishedEvent(QueryWithResultSet queryResult) {
		this.queryResult = queryResult;
	}

	public Object getSubject() {
		return queryResult;
	}
	
}
