package net.sourceforge.tockit.toscanaj.diagram;

import net.sourceforge.tockit.toscanaj.canvas.CanvasItem;
import net.sourceforge.tockit.toscanaj.data.DiagramNode;
import net.sourceforge.tockit.toscanaj.gui.ToscanajGraphics2D;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * class DiagramNode holds details on node position and size
 */

public class NodeView extends CanvasItem {

    /**
     * Store the node model for this view
     */
    private DiagramNode diagramNode = null;

    /**
     * Construct a nodeView for a Node
     */
    public NodeView(DiagramNode diagramNode){
        this.diagramNode = diagramNode;
    }

    public void draw(ToscanajGraphics2D g) {
        g.drawEllipse2D(diagramNode.getPoint(), diagramNode.getRadius());
    }

    /**
     * Returns always false at the moment.
     *
     * @TODO: implement correct behaviour.
     */
    public boolean containsPoint(Point2D point) {
        return false;
    }
}