/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $id$
 */
package org.tockit.events.filters;

import org.tockit.events.Event;

public class SubjectTypeFilter<S, T extends Event<S>> implements EventFilter<T> {
	private Class<? extends S> subjectType;

	public SubjectTypeFilter(Class<? extends S> subjectType) {
		this.subjectType = subjectType;
	}

	public boolean isMatch(T event) {
		return subjectType.isAssignableFrom(event.getSubject().getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object other) {
		if(! (other instanceof SubjectTypeFilter) ) {
			return false;
		}
		SubjectTypeFilter<S,T> otherFilter = (SubjectTypeFilter<S,T>) other;
		if(!otherFilter.subjectType.equals(subjectType)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return subjectType.hashCode();
	}
    
    @Override
	public String toString() {
        return "with subject instantiating " + this.subjectType.getName();
    }
}
