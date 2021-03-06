/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.manipulators;

import java.awt.geom.Point2D;

import org.tockit.canvas.Canvas;
import org.tockit.canvas.MovableCanvasItem;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
import org.tockit.canvas.events.CanvasItemPickupEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

/**
 * @TODO find a way to get the type of the listener down to <MoveableCanvasItem>
 */
public class ItemMovementManipulator implements EventBrokerListener<Object> {
    protected Canvas canvas;

    public ItemMovementManipulator(Canvas canvas, EventBroker<Object> eventBroker) {
        this(canvas, MovableCanvasItem.class, eventBroker);
    }

    public ItemMovementManipulator(Canvas canvas, Class<? extends MovableCanvasItem> itemType, EventBroker<Object> eventBroker) {
        eventBroker.subscribe(this, CanvasItemDraggedEvent.class, itemType);
        this.canvas = canvas;
    }

    public void processEvent(Event<? extends Object> e) {
        if( e instanceof CanvasItemPickupEvent ) {
            dragStart((CanvasItemPickupEvent) e);
        } else if( e instanceof CanvasItemDroppedEvent ) {
            dragEnd((CanvasItemDroppedEvent) e);
        } else {
            moveItem((CanvasItemDraggedEvent) e);
        }
        canvas.repaint();
    }

    protected void moveItem(CanvasItemDraggedEvent dragEvent) {
        MovableCanvasItem item = (MovableCanvasItem) dragEvent.getSubject();
        Point2D fromPosition = dragEvent.getCanvasFromPosition();
        Point2D toPosition = dragEvent.getCanvasToPosition();
        item.moveBy(toPosition.getX() - fromPosition.getX(),
                    toPosition.getY() - fromPosition.getY());
    }

    protected void dragStart(CanvasItemPickupEvent dragEvent) {
        moveItem(dragEvent);
    }

    protected void dragEnd(CanvasItemDroppedEvent dragEvent) {
        moveItem(dragEvent);
    }
}
