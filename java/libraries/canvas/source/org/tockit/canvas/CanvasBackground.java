/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Implements a virtual canvas item that represents the background.
 *
 * This class maps the background of the canvas onto a CanvasItem,
 * thus unifying background events with the rest of the model.
 */
public class CanvasBackground extends CanvasItem {
    /**
     * The parent canvas.
     */
    private Canvas canvas;

    /**
     * The paint used to fill the background.
     */
    private Paint paint = null;

	/**
	 * The paint for the grid lines.
	 */
    private Paint gridPaint;

    /**
     * Creates a new background object.
     *
     * The background will not paint itself until a paint is given
     * via setPaint(Paint).
     */
    public CanvasBackground(Canvas canvas) {
    	this.canvas = canvas;
    }

	/**
	 * Draws the background filling and grid if requested.
	 */
    public void draw(Graphics2D g) {
        Rectangle clipBounds = g.getClipBounds();
        if (paint != null && clipBounds != null) {
            Paint oldPaint = g.getPaint();
            g.setPaint(paint);
            g.fill(clipBounds);

            g.setPaint(this.gridPaint);
            if( this.canvas.hasGridEnabled() ) {
                double cellWidth = this.canvas.getGridCellWidth();
                double cellHeight = this.canvas.getGridCellHeight();
                for(double xPos = 0; xPos < clipBounds.getMaxX(); xPos += cellWidth) {
                    g.draw(new Line2D.Double(xPos,clipBounds.getMinY(),xPos,clipBounds.getMaxY()));
                }
                for(double xPos = -cellWidth; xPos > clipBounds.getMinX(); xPos -= cellWidth) {
                    g.draw(new Line2D.Double(xPos,clipBounds.getMinY(),xPos,clipBounds.getMaxY()));
                }
                for(double yPos = 0; yPos < clipBounds.getMaxY(); yPos += cellHeight) {
                    g.draw(new Line2D.Double(clipBounds.getMinX(), yPos,clipBounds.getMaxX(),yPos));
                }
                for(double yPos = -cellHeight; yPos > clipBounds.getMinY(); yPos -= cellHeight) {
                    g.draw(new Line2D.Double(clipBounds.getMinX(), yPos,clipBounds.getMaxX(),yPos));
                }
            }
            g.setPaint(oldPaint);
        }
    }

    /**
     * This returns always true.
     *
     * The background is assumed to cover the whole canvas, so it
     * is always hit.
     */
    public boolean containsPoint(Point2D point) {
        return true;
    }

    /**
     * Returns an empty rectangle.
     *
     * The background has no extension itself. The rectangle will be
     * placed on the origin of the coordinate system, which means the
     * origin will always be on the canvas.
     */
    public Rectangle2D getCanvasBounds(Graphics2D g) {
        return new Rectangle2D.Double(0, 0, 0, 0);
    }

    /**
     * Changes the content of the background.
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
        if(this.gridPaint == null) {
        	this.gridPaint = Color.GRAY;
        }
    }
    
    /**
     * Sets the paint used for drawing the grid.
     */
    public void setGridPaint(Paint gridPaint) {
    	this.gridPaint = gridPaint;
    }

    /**
     * Returns the paint used for the background.
     */
    public Paint getPaint() {
        return paint;
    }

    public Point2D getPosition() {
        return new Point2D.Double(0,0);
    }

    public boolean hasAutoRaise() {
        return false;
    }
}
