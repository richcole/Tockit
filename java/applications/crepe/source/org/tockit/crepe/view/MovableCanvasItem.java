/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view;

import org.tockit.canvas.CanvasItem;

import java.awt.geom.Point2D;

public abstract class MovableCanvasItem extends CanvasItem {
    public abstract void setPosition(Point2D newPosition);
    public abstract void moveBy(double xDiff, double yDiff);
    public abstract Point2D getPosition();
}
