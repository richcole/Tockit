/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $id$
 */
package org.tockit.events.filters;

import java.awt.event.InputEvent;

import org.tockit.events.Event;

public class AWTInputEventFilter implements EventFilter {
	private int onMask;
	private int offMask;
	
	public AWTInputEventFilter(int onMask, int offMask) {
		this.onMask = onMask;
		this.offMask = offMask;
	}

	public boolean isMatch(Event event) {
		if(!event.getClass().isAssignableFrom(InputEvent.class)) {
			return false;
		}
		InputEvent inputEv = (InputEvent) event;
		if ((inputEv.getModifiersEx() & (onMask | offMask)) != onMask) {
			return false;
		}
		return true;
	}
}
