/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view.manipulators;

import java.awt.event.InputEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.crepe.view.GraphView;
import org.tockit.crepe.view.LineView;
import org.tockit.crepe.view.LinkView;
import org.tockit.events.EventBroker;

public class LinkMoveINCManipulator extends LinkMoveManipulator {
    public LinkMoveINCManipulator(GraphView graphView, EventBroker eventBroker) {
        super(graphView, eventBroker);
    }

    protected void determineItemsToMove(CanvasItemDraggedEvent dragEvent, HashSet itemsToMove, Collection lineViews, LinkView linkView) {
        if ((dragEvent.getModifiers() & InputEvent.CTRL_DOWN_MASK) == 0 &&
                (dragEvent.getModifiers() & InputEvent.SHIFT_DOWN_MASK) == 0) {

            // if CTRL not pressed: add all directly connected nodes
            itemsToMove.add(linkView);
            for (Iterator iterator = lineViews.iterator(); iterator.hasNext();) {
                LineView lineView = (LineView) iterator.next();
                if (lineView.getConnectedLinkView() == linkView) {
                    itemsToMove.add(lineView.getConnectedNodeView());
                }
            }
            // if SHIFT not pressed: remove nodes connected to other links

            for (Iterator iterator = lineViews.iterator(); iterator.hasNext();) {
                LineView lineView = (LineView) iterator.next();
                if (lineView.getConnectedLinkView() != linkView) {
                    itemsToMove.remove(lineView.getConnectedNodeView());
                }
            }
        }
    }
}
