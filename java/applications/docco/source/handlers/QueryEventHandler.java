/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package handlers;
import java.io.IOException;

import org.apache.lucene.queryParser.ParseException;
import org.tockit.events.Event;
import org.tockit.events.EventBrokerListener;

import query.QueryEngine;
import query.QueryWithResult;
 
 
public class QueryEventHandler implements EventBrokerListener {
	
	private QueryEngine queryEngine;
	
	public QueryEventHandler (QueryEngine queryEngine) {
		this.queryEngine = queryEngine;	
	}

	public void processEvent(Event event) {
		String queryString = (String) event.getSubject();
		try {
			QueryWithResult qwr = this.queryEngine.executeQuery(queryString);
			System.out.println(qwr);
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
