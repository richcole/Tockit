/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view;

import org.tockit.canvas.CanvasItem;

import java.awt.*;
import java.awt.geom.*;

public class LineView extends CanvasItem {
    private LinkView linkView = null;
    private NodeView nodeView = null;
    private int number;

    public LineView(LinkView linkView, NodeView nodeView, int number) {
        super();
        this.linkView = linkView;
        this.nodeView = nodeView;
        this.number = number;
    }

    public void draw(Graphics2D g) {
        g.setPaint(Color.black);
        Point2D nodePos = nodeView.getPosition();
        Point2D linkPos = linkView.getPosition();
        g.draw(new Line2D.Double(nodePos, linkPos));
        double xDiff = nodePos.getX() - linkPos.getX();
        double yDiff = nodePos.getY() - linkPos.getY();
        double lengthSq = xDiff * xDiff + yDiff * yDiff;
        float diff = 10;
        if(xDiff != 0) {
            diff = (float)(10 * yDiff*yDiff/lengthSq);
        }
        if ((xDiff > 0) == (yDiff > 0)) {
            g.drawString(String.valueOf(this.number),
                         (float)(nodePos.getX() + linkPos.getX())/2 + diff,
                         (float)(nodePos.getY() + linkPos.getY())/2);
        } else {
            g.drawString(String.valueOf(this.number),
                         (float)(nodePos.getX() + linkPos.getX())/2 - diff,
                         (float)(nodePos.getY() + linkPos.getY())/2);
        }
    }

    public boolean containsPoint(Point2D point) {
        return false;
    }

    public Rectangle2D getCanvasBounds(Graphics2D g) {
        Point2D nodePos = nodeView.getPosition();
        Rectangle2D rect1 = new Rectangle2D.Double(nodePos.getX(), nodePos.getY(), 1, 1);
        Point2D linkPos = linkView.getPosition();
        Rectangle2D rect2 = new Rectangle2D.Double(linkPos.getX(), linkPos.getY(), 1, 1);
        return rect1.createUnion(rect2);
    }

    public boolean hasAutoRaise() {
        return false;
    }
}
