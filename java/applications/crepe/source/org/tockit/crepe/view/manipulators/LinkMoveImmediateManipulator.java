/*
 * Created by IntelliJ IDEA.
 * User: s3805812
 * Date: 18/09/2002
 * Time: 17:14:17
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.tockit.crepe.view.manipulators;

import org.tockit.crepe.view.GraphView;
import org.tockit.crepe.view.LinkView;
import org.tockit.crepe.view.LineView;
import org.tockit.events.EventBroker;
import org.tockit.canvas.events.CanvasItemDraggedEvent;

import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
import java.awt.event.InputEvent;

public class LinkMoveImmediateManipulator extends LinkMoveManipulator {
    public LinkMoveImmediateManipulator(GraphView graphView, EventBroker eventBroker) {
        super(graphView, eventBroker);
    }

    protected void determineItemsToMove(CanvasItemDraggedEvent dragEvent, HashSet itemsToMove, Collection lineViews, LinkView linkView) {
        if ((dragEvent.getModifiers() & InputEvent.CTRL_MASK) == 0 &&
                (dragEvent.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
            // if CTRL not pressed: add all directly connected nodes
            itemsToMove.add(linkView);
            findDirectlyConnectedNodeViews(lineViews, linkView, itemsToMove);
        }
    }
}
