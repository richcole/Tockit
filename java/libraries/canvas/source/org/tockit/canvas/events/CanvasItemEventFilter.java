/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $id$
 */
package org.tockit.canvas.events;

import org.tockit.events.filters.EventFilter;

public class CanvasItemEventFilter implements EventFilter<CanvasItemEvent> {
	private int onMask;
	private int offMask;
	
	public CanvasItemEventFilter(int onMask, int offMask) {
		this.onMask = onMask;
		this.offMask = offMask;
	}

	public boolean isMatch(CanvasItemEvent canvasEv) {
		if ((canvasEv.getModifiers() & (onMask | offMask)) != onMask) {
			return false;
		}
		return true;
	}
}
