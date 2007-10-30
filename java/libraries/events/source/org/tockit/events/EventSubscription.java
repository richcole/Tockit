/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.events;

import org.tockit.events.filters.EventFilter;

/**
 * A subscription class for the event broker.
 *
 * This class is used by the EventBroker class to remember which listener
 * is interested in which types of events.
 */
class EventSubscription<T> {
    /**
     * The listener interested in events.
     */
    private EventBrokerListener<T> listener;
    
    private EventFilter[] eventFilters;

    /**
     * Creates a new subscription object with the given parameters.
     */
    public EventSubscription(EventBrokerListener<T> listener, EventFilter[] eventFilters) {
        this.listener = listener;
        this.eventFilters = eventFilters;
    }

    /**
     * Returns the listener that shall receive the events.
     */
    public EventBrokerListener<T> getListener() {
        return listener;
    }
    
    public boolean matchesEvent(Event<T> event) {
    	for (int i = 0; i < this.eventFilters.length; i++) {
			EventFilter<Event<T>> filter = this.eventFilters[i];
			if(!filter.isMatch(event)) {
				return false;
			}
		}
		return true;
    }
    
    @Override
	public boolean equals(Object other) {
    	if(! (other instanceof EventSubscription)) {
    		return false;
    	}
    	EventSubscription<?> otherSub = (EventSubscription<?>) other;
    	if( otherSub.listener != this.listener) {
    		return false;
    	}
    	if( otherSub.eventFilters.length != this.eventFilters.length ) {
			return false;
		}
		for (int i = 0; i < eventFilters.length; i++) {
			if( ! otherSub.eventFilters[i].equals(eventFilters[i]) ) {
				return false;
			}			
		}
    	return true;
    }
    
    @Override
	public int hashCode() {
    	return (int)(((long)listener.hashCode() + (long)eventFilters.hashCode()) % Integer.MAX_VALUE);
    }
    
    /**
     * Returns a string with debugging information.
     */
    @Override
	public String toString() {
        StringBuffer retVal = new StringBuffer("Subscription for events: ");
        for (int i = 0; i < this.eventFilters.length; i++) {
            EventFilter filter = this.eventFilters[i];
            retVal.append(filter.toString());
            if (i < this.eventFilters.length) {
                retVal.append("; ");
            }
        }
        return retVal.toString();
    }
}
