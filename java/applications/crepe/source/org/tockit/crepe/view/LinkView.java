/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view;

import org.tockit.cgs.model.Link;

import java.awt.*;
import java.awt.geom.*;

public class LinkView extends MovableCanvasItem {
    private Ellipse2D ellipse = new Ellipse2D.Double(0,0,100,40);
    private Link link = null;

    public LinkView(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }

    public void draw(Graphics2D g) {
        g.setPaint(Color.white);
        g.fill(ellipse);
        g.setPaint(Color.black);
        g.draw(ellipse);

        /// @todo use font metrics to center
        g.drawString(link.getType().getName(),
                     (float)ellipse.getX() + 20, (float)ellipse.getY() + 20);
    }

    public boolean containsPoint(Point2D point) {
        return ellipse.contains(point);
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return ellipse.getBounds2D();
    }

    public void setPosition(Point2D newPosition) {
        this.ellipse.setFrame(newPosition.getX(), newPosition.getY(), ellipse.getWidth(), ellipse.getHeight());
    }

    public void moveBy(double xDiff, double yDiff) {
        this.ellipse.setFrame(ellipse.getX() + xDiff, ellipse.getY() + yDiff, ellipse.getWidth(), ellipse.getHeight());
    }

    public Point2D getPosition() {
        return new Point2D.Double(ellipse.getX() + ellipse.getWidth()/2, ellipse.getY() + ellipse.getHeight()/2);
    }
}
