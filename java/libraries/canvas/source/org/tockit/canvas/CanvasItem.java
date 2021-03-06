/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Abstract class to draw 2D graph items.
 *
 * CanvasItems can be put on a Canvas where they will have a z-order and
 * can be moved.
 * 
 * If getPosition() returns null the item is considered to be hidden.
 */
public abstract class CanvasItem {
    /**
     * Draw method called to draw canvas item
     */
    public abstract void draw(Graphics2D g);

    /**
     * Returns true when the given point is on the item.
     */
    public abstract boolean containsPoint(Point2D point);

    public abstract Point2D getPosition();

    /**
     * Returns the rectangular bounds of the canvas item.
     * 
     * @todo should be in canvas coordinates and should not need the Graphics2D
     * 		 parameter. This way the canvas could do more caching and the
     *       objects can work in canvas coordinates all the time. OTOH the fonts
     *       might cause problems.
     */
    public abstract Rectangle2D getCanvasBounds(Graphics2D g);

    /**
     * Returns true if the item should be raised on clicks.
     *
     * This is true per default, if a subclass should not be raised this method
     * can be overwritten returning false and Canvas will not raise it automatically.
     */
    public boolean hasAutoRaise() {
        return true;
    }
}