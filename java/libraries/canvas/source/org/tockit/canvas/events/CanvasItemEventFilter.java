/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $id$
 */
package org.tockit.canvas.events;

import org.tockit.events.Event;
import org.tockit.events.filters.EventFilter;

public class CanvasItemEventFilter implements EventFilter {
	private int onMask;
	private int offMask;
	
	public CanvasItemEventFilter(int onMask, int offMask) {
		this.onMask = onMask;
		this.offMask = offMask;
	}

	public boolean isMatch(Event event) {
		if(!CanvasItemEvent.class.isAssignableFrom(event.getClass())) {
			return false;
		}
		CanvasItemEvent canvasEv = (CanvasItemEvent) event;
		if ((canvasEv.getModifiers() & (onMask | offMask)) != onMask) {
			return false;
		}
		return true;
	}
}
