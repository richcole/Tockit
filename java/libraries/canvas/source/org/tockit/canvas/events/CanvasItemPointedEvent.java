/*
 * Created by IntelliJ IDEA.
 * User: nataliya
 * Date: Aug 9, 2002
 * Time: 3:43:52 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package org.tockit.canvas.events;

import java.awt.geom.Point2D;

import org.tockit.canvas.CanvasItem;

public class CanvasItemPointedEvent extends CanvasItemMouseMovementEvent {
    public CanvasItemPointedEvent(CanvasItem item, int modifiers,
                                   Point2D canvasPosition, Point2D awtPosition) {
        super(item, modifiers, canvasPosition, awtPosition);
    }

}
