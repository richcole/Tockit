/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.events.filters;

import org.tockit.events.Event;

public class EventTypeFilter<T extends Event<?>> implements EventFilter<T> {
	private Class<? extends Event<?>> eventType;
	
	public EventTypeFilter(Class<? extends Event<?>> eventType) {
		this.eventType = eventType;
	}

	public boolean isMatch(T event) {
		return eventType.isAssignableFrom(event.getClass());
	}
	
	@Override
	public boolean equals(Object other) {
		if(! (other instanceof EventTypeFilter) ) {
			return false;
		}
		EventTypeFilter<?> otherFilter = (EventTypeFilter<?>) other;
		if(!otherFilter.eventType.equals(eventType)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return eventType.hashCode();
	}
    
    @Override
	public String toString() {
        return "with event instantiating " + this.eventType.getName();
    }
}
