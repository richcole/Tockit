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

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.crepe.view.GraphView;
import org.tockit.crepe.view.LinkView;
import org.tockit.events.EventBroker;

public class LinkMoveImmediateManipulator extends LinkMoveManipulator {
    public LinkMoveImmediateManipulator(GraphView graphView, EventBroker eventBroker) {
        super(graphView, eventBroker);
    }

    protected void determineItemsToMove(CanvasItemDraggedEvent dragEvent, HashSet itemsToMove, Collection lineViews, LinkView linkView) {
        if ((dragEvent.getModifiers() & InputEvent.CTRL_DOWN_MASK) == 0 &&
                (dragEvent.getModifiers() & InputEvent.SHIFT_DOWN_MASK) != 0) {
            // if CTRL not pressed: add all directly connected nodes
            itemsToMove.add(linkView);
            findDirectlyConnectedNodeViews(lineViews, linkView, itemsToMove);
        }
    }
}
