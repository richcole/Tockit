/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.tockit.canvas.MovableCanvasItem;
import org.tockit.cgs.model.Instance;
import org.tockit.cgs.model.Node;
import org.tockit.cgs.model.Type;

public class NodeView extends MovableCanvasItem {
    private static final double defaultWidth = 130;
    private static final double defaultHeight = 40;
    private Rectangle2D rect = new Rectangle2D.Double(0,0,defaultWidth,defaultHeight);
    private Node node = null;

    public NodeView(Node node) {
        this.node = node;
    }

    public Node getNode() {
        return node;
    }

    public void draw(Graphics2D g) {
        FontMetrics fontMetrics = g.getFontMetrics();

        ///@todo add line break if text is too long, if that doesn't help, add dots at the end.
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

        int stringWidth = fontMetrics.stringWidth(text) + 10;
        double width = this.rect.getWidth();
        while(stringWidth > width) {
            width += defaultWidth/2;
        }
        rect.setRect(node.getX() - width/2, node.getY() - defaultHeight/2, width, defaultHeight);
        float xPos = (float)(this.rect.getX() + width/2 - stringWidth/2);
        float yPos = (float)(this.rect.getY() + defaultHeight/2 +
                             fontMetrics.getHeight()/2 - fontMetrics.getDescent());
        g.setPaint(Color.darkGray);
        g.fill(new Rectangle2D.Double(this.rect.getX() + 2, this.rect.getY() + 2, width, defaultHeight));
        g.setPaint(Color.white);
        g.fill(rect);
        g.setPaint(Color.black);
        g.draw(rect);
        g.drawString(text, xPos, yPos);
    }

    public boolean containsPoint(Point2D point) {
        return rect.contains(point);
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return rect;
    }

    public void setPosition(Point2D newPosition) {
        this.node.setPosition(newPosition.getX(), newPosition.getY());
    }

    public Point2D getPosition() {
        return new Point2D.Double(node.getX(), node.getY());
    }

    public String toString() {
        Node node = this.getNode();
        return "NodeView[" + node.getType().getName() + ":" + node.getReferent() + " (" + node.getId() +")]";
    }
}
