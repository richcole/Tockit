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
import org.tockit.canvas.manipulators.ItemMovementManipulator;
import org.tockit.cgs.model.*;
import org.tockit.crepe.view.manipulators.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class GraphView extends Canvas {
    private ConceptualGraph graphShown;
    private Hashtable nodemap = new Hashtable();
    private Hashtable linkmap = new Hashtable();
    private static final int LINK_LAYOUT_RADIUS = 100;

    public GraphView(EventBroker eventBroker) {
        super(eventBroker);
        getBackgroundItem().setPaint(getBackground());
        new NodeMoveManipulator(this, eventBroker);
        new LinkMoveManipulator(this, eventBroker);
        new NodeContextMenuHandler(this, eventBroker);
        new LinkContextMenuHandler(this, eventBroker);
//        new LoggingEventListener(eventBroker, CanvasItemDraggedEvent.class, Object.class, System.out);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(getBackground());
        g2d.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
        g2d.setPaint(Color.black);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // paint all items on canvas
        paintCanvas(g2d);
    }

    public void showGraph(ConceptualGraph graph) {
        this.clearCanvas();
        this.nodemap.clear();
        this.linkmap.clear();
        this.graphShown = graph;
        if(graph == null) {
            return;
        }
        fillCanvas();
        repaint();
    }

    private void fillCanvas() {
        Node[] nodes = graphShown.getNodes();
        List nodeViewsToAdd = new ArrayList();
        List newNodeViewsPlaced = new ArrayList();
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            if (!nodemap.containsKey(node)) {
                NodeView nodeView = new NodeView(node);
                nodemap.put(node,nodeView);
                nodeViewsToAdd.add(nodeView);
            }
        }

        Link[] links = graphShown.getLinks();
        for (int i = 0; i < links.length; i++) {
            Link link = links[i];
            if (!linkmap.containsKey(link)) {
                LinkView linkView = new LinkView(link);
                double xPos = this.getWidth()/2.0;
                double yPos = this.getHeight()/2.0;
                linkView.setPosition(new Point2D.Double(xPos, yPos));
                linkmap.put(link,linkView);
                Node[] references = link.getReferences();
                for (int j = 0; j < references.length; j++) {
                    Node node = references[j];
                    NodeView nodeView = (NodeView) nodemap.get(node);
                    double angle = 2*Math.PI * j / references.length;
                    double nodeX = xPos + LINK_LAYOUT_RADIUS * Math.sin(angle);
                    // y gets correction for shapes of vertices (wider than high). Does only work in very special cases, which we have :-)
                    double nodeY = yPos + LINK_LAYOUT_RADIUS * Math.cos(angle) -
                                   Math.abs(LINK_LAYOUT_RADIUS * Math.sin(angle) / 3);
                    nodeView.setPosition(new Point2D.Double(nodeX, nodeY));
                    newNodeViewsPlaced.add(nodeView);
                    this.addCanvasItem(new LineView(linkView, nodeView, j + 1));
                }
                this.addCanvasItem(linkView);
            }
        }

        for (Iterator iterator = nodeViewsToAdd.iterator(); iterator.hasNext();) {
            NodeView nodeView = (NodeView) iterator.next();
            if (!newNodeViewsPlaced.contains(nodeView)) {
                double nodeX = this.getWidth()/2.0;
                double nodeY = this.getHeight()/2.0;
                nodeView.setPosition(new Point2D.Double(nodeX, nodeY));
            }
            this.addCanvasItem(nodeView);
        }
    }

    public ConceptualGraph getGraphShown() {
        return graphShown;
    }

    public void updateContents() {
        fillCanvas();
        repaint();
    }
}