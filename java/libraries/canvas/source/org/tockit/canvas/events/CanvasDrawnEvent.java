/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.events;

import org.tockit.canvas.Canvas;
import org.tockit.events.Event;

/**
 * This event is send after a canvas is completely redrawn.
 * 
 * This is useful for animations and user feedback.
 */
public class CanvasDrawnEvent implements Event<Canvas> {
    private Canvas subject;

    public CanvasDrawnEvent(Canvas subject) {
        this.subject = subject;
    }

    public Canvas getSubject() {
        return subject;
    }
    
    /**
     * Same effect as calling getSubject().
     */
    @Deprecated
    public Canvas getCanvas() {
    	return subject;
    }
}
