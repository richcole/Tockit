package net.sourceforge.tockit.toscanaj.diagram;

import net.sourceforge.tockit.toscanaj.data.DiagramLine;
import net.sourceforge.tockit.toscanaj.canvas.CanvasItem;
import net.sourceforge.tockit.toscanaj.gui.ToscanajGraphics2D;

import java.awt.geom.Point2D;

/**
 * class DiagramLine draws a line between two points
 */

import java.awt.Graphics2D;

public class LineView extends CanvasItem {
    /**
     * Store the node model for this view
     */
    private DiagramLine diagramLine = null;


    /**
     * Creates DiagramLine object, initialising fromPoint and toPoint variables to required values
     * indicating positions of 2 nodes in a Diagram
    */
    public LineView(DiagramLine diagramLine) {
    this.diagramLine = diagramLine;
    }

    public void draw(ToscanajGraphics2D g) {
        ///@TODO Probably should throw a LineNotFoundException
        if(diagramLine != null) {
            g.drawLine(diagramLine.getFromPoint(), diagramLine.getToPoint());
        }
    }

    /**
     * Returns always false since we assume the line to have no width.
     */
    public boolean containsPoint(Point2D point) {
        return false;
    }
}