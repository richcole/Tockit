/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package org.tockit.docco.handlers;
import java.io.IOException;

import org.apache.lucene.queryParser.ParseException;
import org.tockit.docco.events.QueryEvent;
import org.tockit.docco.events.QueryFinishedEvent;
import org.tockit.docco.query.QueryEngine;
import org.tockit.docco.query.QueryWithResult;
import org.tockit.docco.query.util.QueryWithResultSet;
import org.tockit.docco.query.util.QueryWithResultSetImplementation;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;


 
/**
 * @todo check if we want to drop the events in favour of straight calls
 */
public class QueryEventHandler implements EventBrokerListener {
	
	private QueryEngine queryEngine;
	private EventBroker eventBroker;
	
	public QueryEventHandler (EventBroker eventBroker, QueryEngine queryEngine) {
		this.eventBroker = eventBroker;
		this.queryEngine = queryEngine;	
	}

	public void processEvent(Event event) {
		QueryEvent queryEvent = (QueryEvent) event;
		String queryString = (String) event.getSubject();
		QueryWithResultSet queryResult = new QueryWithResultSetImplementation();
		try {
			if (queryEvent.useDecomposer()) {
				queryResult = queryEngine.executeQueryUsingDecomposer(queryString);
			}
			else {
				QueryWithResult qwr = this.queryEngine.executeQuery(queryString);
				queryResult.add(qwr);
			}
			this.eventBroker.processEvent(new QueryFinishedEvent(queryResult));
		}
		catch (ParseException e) {
			/// @todo what to do with exception?
			e.printStackTrace();
		}
		catch (IOException e) {
			/// @todo what to do with exception?
			e.printStackTrace();
		}
	}

}
