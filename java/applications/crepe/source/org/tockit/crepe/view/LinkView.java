/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view;

import org.tockit.cgs.model.Link;
import org.tockit.canvas.MovableCanvasItem;

import java.awt.*;
import java.awt.geom.*;

public class LinkView extends MovableCanvasItem {
    private Ellipse2D ellipse = new Ellipse2D.Double(0,0,130,40);
    private Link link = null;

    public LinkView(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }

    public void draw(Graphics2D g) {
        FontMetrics fontMetrics = g.getFontMetrics();

        double height = ellipse.getHeight();
        double width = ellipse.getWidth();
        this.ellipse.setFrame(link.getX() - width/2, link.getY() - height/2, width, height);

        g.setPaint(Color.darkGray);
        g.fill(new Ellipse2D.Double(ellipse.getX() + 2, ellipse.getY() + 2, width, height));
        g.setPaint(Color.white);
        g.fill(ellipse);
        g.setPaint(Color.black);
        g.draw(ellipse);

        String text = link.getType().getName();
        float xPos = (float)(ellipse.getX() + ellipse.getWidth()/2 - fontMetrics.stringWidth(text)/2);
        float yPos = (float)(ellipse.getY() + ellipse.getHeight()/2 + fontMetrics.getHeight()/2 - fontMetrics.getDescent());
        g.drawString(text, xPos, yPos);
    }

    public boolean containsPoint(Point2D point) {
        return ellipse.contains(point);
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return ellipse.getBounds2D();
    }

    public void setPosition(Point2D newPosition) {
        this.link.setPosition(newPosition.getX(), newPosition.getY());
    }

    public Point2D getPosition() {
        return new Point2D.Double(link.getX(), link.getY());
    }

    public String toString() {
        Link link = this.getLink();
        return "LinkView[" + link.getType().getName() + " (" + link.getId() +")]";
    }
}
