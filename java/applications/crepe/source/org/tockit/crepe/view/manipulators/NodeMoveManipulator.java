/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view.manipulators;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.tockit.canvas.Canvas;
import org.tockit.canvas.CanvasItem;
import org.tockit.canvas.MovableCanvasItem;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.canvas.events.CanvasItemDroppedEvent;
import org.tockit.canvas.events.CanvasItemPickupEvent;
import org.tockit.canvas.manipulators.ItemMovementManipulator;
import org.tockit.cgs.model.Link;
import org.tockit.cgs.model.Node;
import org.tockit.crepe.view.LineView;
import org.tockit.crepe.view.NodeView;
import org.tockit.events.EventBroker;

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

    private class MergeSymbol extends MovableCanvasItem {
        private Point2D position;
        public static final int RADIUS = 15;

        public void setPosition(Point2D newPosition) {
            this.position = newPosition;
        }

        public void draw(Graphics2D g) {
            g.setPaint(Color.green);
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

    private InvalidSymbol invalidSymbol = new InvalidSymbol();
    private MergeSymbol mergeSymbol = new MergeSymbol();
    private Point2D oldPos = null;

    public NodeMoveManipulator(Canvas canvas, EventBroker eventBroker) {
        super(canvas, NodeView.class, eventBroker);
    }

    protected void moveItem(CanvasItemDraggedEvent dragEvent) {
        super.moveItem(dragEvent);
        Point2D position = ((CanvasItem) (dragEvent.getSubject())).getPosition();
        invalidSymbol.setPosition(position);
        mergeSymbol.setPosition(position);
        if(dropValid(dragEvent)) {
            canvas.removeCanvasItem(invalidSymbol);
            if(!getOtherNodeViewsAtPosition(dragEvent).isEmpty()) {
                canvas.addCanvasItem(mergeSymbol);
            } else {
                canvas.removeCanvasItem(mergeSymbol);
            }
        } else {
            canvas.removeCanvasItem(mergeSymbol);
            canvas.addCanvasItem(invalidSymbol);
        }
    }

    private boolean dropValid(CanvasItemDraggedEvent dragEvent) {
        Point2D canvasPosition = dragEvent.getCanvasToPosition();
        Collection items = canvas.getCanvasItemsAt(canvasPosition);
        for (Iterator iterator = items.iterator(); iterator.hasNext();) {
            Object canvasItem = iterator.next();
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
        canvas.removeCanvasItem(invalidSymbol);
        canvas.removeCanvasItem(mergeSymbol);
    }

    private void mergeNodes(CanvasItemDroppedEvent dragEvent) {
        Collection otherNodeViewsAtPosition = getOtherNodeViewsAtPosition(dragEvent);
        if(otherNodeViewsAtPosition.isEmpty()) {
            //nothing to merge
            return;
        }
        NodeView nodeView = (NodeView)dragEvent.getSubject();
        Node ourNode = nodeView.getNode();
        Node otherNode = null;
        NodeView otherNodeView = (NodeView) otherNodeViewsAtPosition.iterator().next();
        if(otherNodeView != null) {
            otherNode = otherNodeView.getNode();
            otherNode.merge(ourNode);
            relink(nodeView, otherNodeView);
            ourNode.destroy();
            canvas.raiseItem(otherNodeView);
            canvas.removeCanvasItem(nodeView);
        }
    }

    private Collection getOtherNodeViewsAtPosition(CanvasItemDraggedEvent dragEvent) {
        Set retVal = new HashSet();
        Point2D canvasPosition = dragEvent.getCanvasToPosition();
        Collection items = canvas.getCanvasItemsAt(canvasPosition);
        for (Iterator iterator = items.iterator(); iterator.hasNext();) {
            Object canvasItem = iterator.next();
            if(canvasItem instanceof NodeView) {
                if(canvasItem != dragEvent.getSubject()) {
                    retVal.add(canvasItem);
                }
            }
        }
        return retVal;
    }

    private void relink(NodeView nodeView, NodeView otherNodeView) {
        for (Iterator iterator = canvas.getCanvasItemsByType(LineView.class).iterator(); iterator.hasNext();) {
            LineView lineView = (LineView) iterator.next();
            if(lineView.getConnectedNodeView() == nodeView) {
                lineView.setConnectedNodeView(otherNodeView);
                Link link = lineView.getConnectedLinkView().getLink();
                Node[] refs = link.getReferences();
                for (int i = 0; i < refs.length; i++) {
                    Node node = refs[i];
                    if(node == nodeView.getNode()) {
                        refs[i] = otherNodeView.getNode();
                    }
                }
                link.setReferences(refs);
            }
        }
    }

    protected void dragStart(CanvasItemPickupEvent dragEvent) {
        oldPos = ((CanvasItem)dragEvent.getSubject()).getPosition();
        super.dragStart(dragEvent);
    }
}
