/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view.manipulators;

import org.tockit.events.*;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.crepe.view.*;

import java.awt.geom.Point2D;

public class ItemMovementManipulator implements EventListener {
    private GraphView graphView;

    public ItemMovementManipulator(GraphView graphView, EventBroker eventBroker) {
        eventBroker.subscribe(this, CanvasItemDraggedEvent.class, MovableCanvasItem.class);
        this.graphView = graphView;
    }

    public void processEvent(Event e) {
        CanvasItemDraggedEvent dragEvent = (CanvasItemDraggedEvent) e;
        MovableCanvasItem view = (MovableCanvasItem) dragEvent.getSubject();
        Point2D fromPosition = dragEvent.getCanvasFromPosition();
        Point2D toPosition = dragEvent.getCanvasToPosition();
        view.moveBy(toPosition.getX() - fromPosition.getX(),
                    toPosition.getY() - fromPosition.getY());
        graphView.repaint();
    }
}
