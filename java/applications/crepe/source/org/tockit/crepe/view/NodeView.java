/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view;

import org.tockit.cgs.model.*;
import org.tockit.canvas.MovableCanvasItem;

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
        FontMetrics fontMetrics = g.getFontMetrics();

        g.setPaint(Color.darkGray);
        g.fill(new Rectangle2D.Double(rect.getX() + 2, rect.getY() + 2, rect.getWidth(), rect.getHeight()));
        g.setPaint(Color.white);
        g.fill(rect);
        g.setPaint(Color.black);
        g.draw(rect);

        Instance referent = node.getReferent();
        Type type = node.getType();
        String text;
        if( type == Type.UNIVERSAL ) {
            text = "[universal]";
        }
        else if( type == Type.ABSURD ) {
            text = "[absurd]";
        }
        else {
            text = type.getName();
        }
        if (referent != null) {
            text += ": " + referent.getIdentifier();
        }
        float xPos = (float)(rect.getX() + rect.getWidth()/2 - fontMetrics.stringWidth(text)/2);
        float yPos = (float)(rect.getY() + rect.getHeight()/2 + fontMetrics.getHeight()/2 - fontMetrics.getDescent());
        g.drawString(text, xPos, yPos);
    }

    public boolean containsPoint(Point2D point) {
        return rect.contains(point);
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return rect;
    }

    public void setPosition(Point2D newPosition) {
        this.rect.setRect(newPosition.getX() - rect.getWidth()/2,
                          newPosition.getY() - rect.getHeight()/2,
                          rect.getWidth(), rect.getHeight());
    }

    public Point2D getPosition() {
        return new Point2D.Double(rect.getX() + rect.getWidth()/2, rect.getY() + rect.getHeight()/2);
    }
}
