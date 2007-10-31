/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $id$
 */
package org.tockit.events.filters;

import org.tockit.events.Event;

/**
 * Models a filter that filters a certain type of events.
 * 
 * @param <T> The event type that this filter can be applied to.
 */
public interface EventFilter<T extends Event<?>> {
	boolean isMatch(T event);
}
