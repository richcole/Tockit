/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view;

import org.tockit.cgs.model.Node;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class NodeView extends MovableCanvasItem {
    private Rectangle2D rect = new Rectangle2D.Double(0,0,130,40);
    private Node node = null;

    public NodeView(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public void draw(Graphics2D g) {
        g.setPaint(Color.white);
        g.fill(rect);
        g.setPaint(Color.black);
        g.draw(rect);

        /// @todo use font metrics to center
        g.drawString(node.getType().getName() + ": " + node.getReferent().getIdentifier(),
                     (float)rect.getX() + 5, (float)rect.getY() + 20);
    }

    public boolean containsPoint(Point2D point) {
        return rect.contains(point);
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return rect;
    }

    public void setPosition(Point2D newPosition) {
        this.rect.setRect(newPosition.getX(), newPosition.getY(), rect.getWidth(), rect.getHeight());
    }

    public void moveBy(double xDiff, double yDiff) {
        this.rect.setRect(rect.getX() + xDiff, rect.getY() + yDiff, rect.getWidth(), rect.getHeight());
    }

    public Point2D getPosition() {
        return new Point2D.Double(rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2);
    }
}
