/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.events;

import java.awt.geom.Point2D;

import org.tockit.canvas.CanvasItem;

public class CanvasItemMouseMovementEvent extends CanvasItemEventWithPosition {
    public CanvasItemMouseMovementEvent(CanvasItem item, int modifiers,
                                   Point2D canvasPosition, Point2D awtPosition) {
        super(item, modifiers, canvasPosition, awtPosition);
    }
}
