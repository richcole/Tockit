/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.events.filters;

import org.tockit.events.Event;

public class EventTypeFilter implements EventFilter {
	private Class<? extends Event<?>> eventType;
	
	public EventTypeFilter(Class<? extends Event<?>> eventType) {
		this.eventType = eventType;
	}

	public boolean isMatch(Event event) {
		return eventType.isAssignableFrom(event.getClass());
	}
	
	public boolean equals(Object other) {
		if(! (other instanceof EventTypeFilter) ) {
			return false;
		}
		EventTypeFilter otherFilter = (EventTypeFilter) other;
		if(!otherFilter.eventType.equals(eventType)) {
			return false;
		}
		return true;
	}
	
	public int hashCode() {
		return eventType.hashCode();
	}
    
    public String toString() {
        return "with event instantiating " + this.eventType.getName();
    }
}
