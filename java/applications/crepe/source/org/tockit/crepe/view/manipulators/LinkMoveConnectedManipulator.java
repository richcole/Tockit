/*
 * Created by IntelliJ IDEA.
 * User: s3805812
 * Date: 18/09/2002
 * Time: 17:48:06
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.tockit.crepe.view.manipulators;

import java.awt.event.InputEvent;
import java.util.Collection;
import java.util.HashSet;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.crepe.view.GraphView;
import org.tockit.crepe.view.LinkView;
import org.tockit.events.EventBroker;

public class LinkMoveConnectedManipulator extends LinkMoveManipulator {
    public LinkMoveConnectedManipulator(GraphView graphView, EventBroker eventBroker) {
        super(graphView, eventBroker);
    }

    protected void determineItemsToMove(CanvasItemDraggedEvent dragEvent, HashSet itemsToMove, Collection lineViews, LinkView linkView) {

        if ((dragEvent.getModifiers() & InputEvent.CTRL_MASK) != 0 &&
                (dragEvent.getModifiers() & InputEvent.SHIFT_MASK) == 0) {
            // if CTRL pressed: find all nodes/links connected
            itemsToMove.add(linkView);
            findConnectedViewsRecursive(itemsToMove, lineViews);
        }
    }
}
