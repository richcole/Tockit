/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view.manipulators;

import org.tockit.canvas.manipulators.ItemMovementManipulator;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.events.EventBroker;
import org.tockit.crepe.view.*;

import java.util.*;
import java.awt.geom.Point2D;
import java.awt.event.InputEvent;

abstract public class LinkMoveManipulator extends ItemMovementManipulator {
    public LinkMoveManipulator(GraphView graphView, EventBroker eventBroker) {
        super(graphView, LinkView.class, eventBroker);
    }

    protected void moveItem(CanvasItemDraggedEvent dragEvent) {
        LinkView linkView = (LinkView) dragEvent.getSubject();
        GraphView graphView = (GraphView) this.canvas;
        Collection lineViews = graphView.getCanvasItemsByType(LineView.class);
        HashSet itemsToMove = new HashSet();
        Point2D fromPosition = dragEvent.getCanvasFromPosition();
        Point2D toPosition = dragEvent.getCanvasToPosition();
        double xDiff = toPosition.getX() - fromPosition.getX();
        double yDiff = toPosition.getY() - fromPosition.getY();
        determineItemsToMove(dragEvent, itemsToMove, lineViews, linkView);
        for (Iterator iterator = itemsToMove.iterator(); iterator.hasNext();) {
            Object o = (Object) iterator.next();
            if (o instanceof LinkView) {
                LinkView lv = (LinkView) o;
                lv.moveBy(xDiff, yDiff);
            }
            if (o instanceof NodeView) {
                NodeView nv = (NodeView) o;
                nv.moveBy(xDiff, yDiff);
            }
        }
    }

    abstract protected void determineItemsToMove(CanvasItemDraggedEvent dragEvent, HashSet itemsToMove, Collection lineViews, LinkView linkView);

    protected void findConnectedViewsRecursive(HashSet itemsToMove, Collection lineViews) {
        for (Iterator iterator = lineViews.iterator(); iterator.hasNext();) {
            LineView lineView = (LineView) iterator.next();
            LinkView connectedLinkView = lineView.getConnectedLinkView();
            NodeView connectedNodeView = lineView.getConnectedNodeView();
            if (itemsToMove.contains(connectedLinkView) &&
                    !itemsToMove.contains(connectedNodeView)) {
                itemsToMove.add(connectedNodeView);
                findConnectedViewsRecursive(itemsToMove, lineViews);
            }
            if (!itemsToMove.contains(connectedLinkView) &&
                    itemsToMove.contains(connectedNodeView)) {
                itemsToMove.add(connectedLinkView);
                findConnectedViewsRecursive(itemsToMove, lineViews);
            }
        }
    }

    protected void findDirectlyConnectedNodeViews(Collection lineViews, LinkView linkView, HashSet itemsToMove) {
        for (Iterator iterator = lineViews.iterator(); iterator.hasNext();) {
            LineView lineView = (LineView) iterator.next();
            if (lineView.getConnectedLinkView() == linkView) {
                itemsToMove.add(lineView.getConnectedNodeView());
            }
        }
    }
}
