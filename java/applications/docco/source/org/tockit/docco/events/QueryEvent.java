/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.events;

import org.tockit.events.Event;

public class QueryEvent implements Event{
	private String queryString;
	private boolean useDecomposer;
	
	public QueryEvent(String queryString, boolean useDecomposer) {
		this.queryString = queryString;
		this.useDecomposer = useDecomposer;
	}

	public Object getSubject() {
		return queryString;
	}
	
	public boolean useDecomposer() {
		return this.useDecomposer;
	}
}
