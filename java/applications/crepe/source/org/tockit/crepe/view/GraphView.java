/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view;

import org.tockit.canvas.Canvas;
import org.tockit.events.EventBroker;
import org.tockit.crepe.view.manipulators.ItemMovementManipulator;
import org.tockit.cgs.model.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Hashtable;

public class GraphView extends Canvas {
    public GraphView(EventBroker eventBroker) {
        super(eventBroker);
        getBackgroundItem().setPaint(Color.LIGHT_GRAY);
        new ItemMovementManipulator(this, eventBroker);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Color.lightGray);
        g2d.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        g2d.setPaint(Color.black);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // paint all items on canvas
        paintCanvas(g2d);
    }

    public void showGraph(ConceptualGraph graph) {
        this.clearCanvas();
         if(graph == null) {
            return;
        }

        Hashtable nodemap = new Hashtable();
        Node[] nodes = graph.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            NodeView nodeView = new NodeView(node);
            nodemap.put(node,nodeView);
        }

        Link[] links = graph.getLinks();
        for (int i = 0; i < links.length; i++) {
            Link link = links[i];
            LinkView linkView = new LinkView(link);
            Node[] references = link.getReferences();
            for (int j = 0; j < references.length; j++) {
                Node node = references[j];
                this.addCanvasItem(new LineView(linkView, (NodeView) nodemap.get(node), j + 1));
            }
            this.addCanvasItem(linkView);
        }

        for (Iterator iterator = nodemap.values().iterator(); iterator.hasNext();) {
            NodeView nodeView = (NodeView) iterator.next();
            this.addCanvasItem(nodeView);
        }

        repaint();
    }
}