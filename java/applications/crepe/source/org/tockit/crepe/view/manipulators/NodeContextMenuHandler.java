/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view.manipulators;

import org.tockit.events.*;
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.Canvas;
import org.tockit.cgs.model.Node;
import org.tockit.cgs.model.Type;
import org.tockit.crepe.view.NodeView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

public class NodeContextMenuHandler implements EventListener {
    private Canvas canvas;

    public NodeContextMenuHandler(Canvas canvas, EventBroker eventBroker) {
        this.canvas = canvas;
        eventBroker.subscribe(this, CanvasItemContextMenuRequestEvent.class, NodeView.class);
    }

    public void processEvent(Event e) {
        CanvasItemEventWithPosition itemEvent = null;
        try {
            itemEvent = (CanvasItemEventWithPosition) e;
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to CanvasItemEventWithPositions only");
        }
        NodeView nodeView = null;
        try {
            nodeView = (NodeView) itemEvent.getItem();
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to events from NodeViews only");
        }
        openPopupMenu(nodeView, itemEvent.getAWTPosition());
    }

    public void openPopupMenu(final NodeView nodeView, Point2D screenPosition) {
        // create the menu
        JPopupMenu popupMenu = new JPopupMenu();
        Node node = nodeView.getNode();
        createRestrictTypeMenu(popupMenu, node, node.getType());
        createInstantiateMenu(popupMenu, node);
        JMenuItem menuItem = new JMenuItem("Remove");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ///@todo check if there are no links pointing to node, then remove
            }
        });
        popupMenu.add(menuItem);
        popupMenu.show(this.canvas, (int) screenPosition.getX(), (int) screenPosition.getY());
    }

    private void createInstantiateMenu(JPopupMenu popupMenu, final Node node) {
        JMenuItem menuItem;
        JMenu instantiateMenu = new JMenu("Instantiate");
        menuItem = new JMenuItem("New instance");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /// @todo open dialog to enter name, check that it does not exist yet, put on node
            }
        });
        instantiateMenu.add(menuItem);
        menuItem = new JMenuItem("Existing instance");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /// @todo open dialog to choose instance, put on node
            }
        });
        instantiateMenu.add(menuItem);
        popupMenu.add(instantiateMenu);
    }

    private void createRestrictTypeMenu(JPopupMenu parentMenu, final Node node, final Type type) {
        JMenuItem menuItem;
        JMenu restrictTypeMenu = new JMenu("Restrict Type");
        menuItem = new JMenuItem("New type");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ///@todo open dialog to enter type name, check it is unique, add it as subtype of type and as type of node
            }
        });
        restrictTypeMenu.add(menuItem);
        Type[] directSubtypes = type.getDirectSubtypes();
        if(directSubtypes.length != 0) {
            restrictTypeMenu.addSeparator();
        }
        for (int i = 0; i < directSubtypes.length; i++) {
            Type directSubtype = directSubtypes[i];
            JMenu subMenu = new JMenu(directSubtype.getName());
            createRestrictTypeSubMenu(subMenu, node, directSubtype);
            restrictTypeMenu.add(subMenu);
        }
        parentMenu.add(restrictTypeMenu);
    }

    private void createRestrictTypeSubMenu(JMenu parentMenu, final Node node, final Type type) {
        JMenuItem menuItem;
        menuItem = new JMenuItem("Use this type");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                node.setType(type);
                canvas.repaint();
            }
        });
        parentMenu.add(menuItem);
        menuItem = new JMenuItem("New type");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ///@todo open dialog to enter type name, check it is unique, add it as subtype of type and as type of node
            }
        });
        parentMenu.add(menuItem);
        Type[] directSubtypes = type.getDirectSubtypes();
        if(directSubtypes.length != 0) {
            parentMenu.addSeparator();
        }
        for (int i = 0; i < directSubtypes.length; i++) {
            Type directSubtype = directSubtypes[i];
            JMenu subMenu = new JMenu(directSubtype.getName());
            createRestrictTypeSubMenu(subMenu, node, directSubtype);
            parentMenu.add(subMenu);
        }
    }
}
