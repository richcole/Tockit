/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view.manipulators;

import org.tockit.canvas.manipulators.ItemMovementManipulator;
import org.tockit.canvas.*;
import org.tockit.canvas.Canvas;
import org.tockit.canvas.events.*;
import org.tockit.events.EventBroker;
import org.tockit.crepe.view.NodeView;
import org.tockit.crepe.view.LineView;
import org.tockit.cgs.model.Node;

import java.awt.geom.*;
import java.awt.*;
import java.util.Collection;
import java.util.Iterator;

public class NodeMoveManipulator extends ItemMovementManipulator {
    private class InvalidSymbol extends MovableCanvasItem {
        private Point2D position;
        public static final int RADIUS = 15;

        public void setPosition(Point2D newPosition) {
            this.position = newPosition;
        }

        public void draw(Graphics2D g) {
            g.setPaint(Color.red);
            g.fill(new Ellipse2D.Double(position.getX() - RADIUS/2, position.getY() - RADIUS/2, RADIUS, RADIUS));
        }

        public Point2D getPosition() {
            return position;
        }

        public boolean containsPoint(Point2D point) {
            return false;
        }

        public Rectangle2D getCanvasBounds(Graphics2D g) {
            return null;
        }
    }

    private InvalidSymbol symbol = new InvalidSymbol();
    private Point2D oldPos = null;

    public NodeMoveManipulator(Canvas canvas, EventBroker eventBroker) {
        super(canvas, NodeView.class, eventBroker);
    }

    protected void moveItem(CanvasItemDraggedEvent dragEvent) {
        super.moveItem(dragEvent);
        symbol.setPosition(((CanvasItem) (dragEvent.getSubject())).getPosition());
        if(dropValid(dragEvent)) {
            canvas.removeCanvasItem(symbol);
        } else {
            canvas.addCanvasItem(symbol);
        }
    }

    private boolean dropValid(CanvasItemDraggedEvent dragEvent) {
        Point2D canvasPosition = dragEvent.getCanvasToPosition();
        Collection items = canvas.getCanvasItemsAt(canvasPosition);
        for (Iterator iterator = items.iterator(); iterator.hasNext();) {
            Object canvasItem = (Object) iterator.next();
            if(canvasItem instanceof NodeView) {
                if(canvasItem != dragEvent.getSubject()) {
                    NodeView other = (NodeView) canvasItem;
                    NodeView we = (NodeView) dragEvent.getSubject();
                    Node otherNode = other.getNode();
                    Node ourNode = we.getNode();
                    if(! otherNode.mergePossible(ourNode)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected void dragEnd(CanvasItemDroppedEvent dragEvent) {
        super.dragEnd(dragEvent);
        NodeView nodeView = (NodeView)dragEvent.getSubject();
        if(dropValid(dragEvent)) {
            mergeNodes(dragEvent);
        }
        else {
            nodeView.setPosition(oldPos);
        }
        canvas.removeCanvasItem(symbol);
    }

    private void mergeNodes(CanvasItemDroppedEvent dragEvent) {
        NodeView nodeView = (NodeView)dragEvent.getSubject();
        NodeView otherNodeView = null;
        Node ourNode = nodeView.getNode();
        Node otherNode = null;
        Point2D canvasPosition = dragEvent.getCanvasToPosition();
        Collection items = canvas.getCanvasItemsAt(canvasPosition);
        for (Iterator iterator = items.iterator(); iterator.hasNext();) {
            Object canvasItem = (Object) iterator.next();
            if(canvasItem instanceof NodeView) {
                if(canvasItem != dragEvent.getSubject()) {
                    otherNodeView = (NodeView) canvasItem;
                    otherNode = otherNodeView.getNode();
                }
            }
        }
        if(otherNode != null) {
            otherNode.merge(ourNode);
            relink(nodeView, otherNodeView);
            ourNode.destroy();
            canvas.raiseItem(otherNodeView);
            canvas.removeCanvasItem(nodeView);
        }
    }

    private void relink(NodeView nodeView, NodeView otherNodeView) {
        for (Iterator iterator = canvas.getCanvasItemsByType(LineView.class).iterator(); iterator.hasNext();) {
            LineView lineView = (LineView) iterator.next();
            if(lineView.getConnectedNodeView() == nodeView) {
                lineView.setConnectedNodeView(otherNodeView);
            }
        }
    }

    protected void dragStart(CanvasItemPickupEvent dragEvent) {
        oldPos = ((CanvasItem)dragEvent.getSubject()).getPosition();
        super.dragStart(dragEvent);
    }
}
