/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.view.manipulators;

import org.tockit.events.*;
import org.tockit.events.EventListener;
import org.tockit.canvas.events.CanvasItemEventWithPosition;
import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.Canvas;
import org.tockit.cgs.model.*;
import org.tockit.crepe.view.*;
import org.tockit.crepe.gui.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.*;

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
        /// @todo restricted type has to match instance!
        createRestrictTypeMenu(popupMenu, node, node.getType());
        if(node.getReferent() == null) {
            createInstantiateMenu(popupMenu, node);
        }
        Collection lineViews = canvas.getCanvasItemsByType(LineView.class);
        final Set connectedLineViews = new HashSet();
        for (Iterator iterator = lineViews.iterator(); iterator.hasNext();) {
            LineView lineView = (LineView) iterator.next();
            if(lineView.getConnectedNodeView() == nodeView) {
                connectedLineViews.add(lineView);
            }
        }
        JMenuItem menuItem;
        if (connectedLineViews.size() == 0) {
            menuItem = new JMenuItem("Remove");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    nodeView.getNode().destroy();
                    canvas.removeCanvasItem(nodeView);
                    canvas.repaint();
                }
            });
            popupMenu.add(menuItem);
        }
        if (connectedLineViews.size() > 1) {
            menuItem = new JMenuItem("Duplicate");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Iterator it = connectedLineViews.iterator();
                    it.next();
                    double x = nodeView.getPosition().getX();
                    double y = nodeView.getPosition().getY();
                    while(it.hasNext()) {
                        LineView lineView = (LineView) it.next();
                        LinkView linkView = lineView.getConnectedLinkView();
                        Link link = linkView.getLink();
                        Node newNode = new Node(nodeView.getNode());
                        NodeView newNodeView = new NodeView(newNode);
                        x+=20;
                        newNodeView.setPosition(new Point2D.Double(x,y));
                        canvas.addCanvasItem(newNodeView);
                        lineView.setConnectedNodeView(newNodeView);
                        Node[] refs = link.getReferences();
                        for (int i = 0; i < refs.length; i++) {
                            Node node = refs[i];
                            if(node == nodeView.getNode()) {
                                refs[i] = newNode;
                            }
                        }
                        link.setReferences(refs);
                    }
                    canvas.repaint();
                }
            });
            popupMenu.add(menuItem);
        }
        popupMenu.show(this.canvas, (int) screenPosition.getX(), (int) screenPosition.getY());
    }

    private void createInstantiateMenu(JPopupMenu popupMenu, final Node node) {
        JMenuItem menuItem;
        JMenu instantiateMenu = new JMenu("Instantiate");
        menuItem = new JMenuItem("New instance");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newId;
                KnowledgeBase knowledgeBase = node.getKnowledgeBase();
                do {
                    newId = InstanceCreator.createNewInstanceName(canvas);
                    if(newId == null) {
                        return;
                    }
                } while (knowledgeBase.getInstance(newId) != null);
                Instance newInstance = new Instance(knowledgeBase, newId, node.getType());
                node.setReferent(newInstance);
                canvas.repaint();
            }
        });
        instantiateMenu.add(menuItem);
        menuItem = new JMenuItem("Existing instance");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Instance instance = InstanceChooser.chooseInstance(canvas, node.getKnowledgeBase(), node.getType());
                node.setReferent(instance);
                canvas.repaint();
            }
        });
        instantiateMenu.add(menuItem);
        popupMenu.add(instantiateMenu);
    }

    private void createRestrictTypeMenu(JPopupMenu parentMenu, final Node node, final Type type) {
        JMenuItem menuItem;
        JMenu restrictTypeMenu = new JMenu("Restrict Type");
        menuItem = new JMenuItem("New type");
        menuItem.addActionListener(new CreateNewTypeListener(node, type));
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
        menuItem.addActionListener((new CreateNewTypeListener(node, type)));
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

    private class CreateNewTypeListener implements ActionListener {
        private final Node node;
        private final Type type;

        public void actionPerformed(ActionEvent e) {
            String newName;
            KnowledgeBase knowledgeBase = node.getKnowledgeBase();
            do {
                newName = TypeCreator.createNewTypeName(canvas);
                if(newName == null) {
                    return;
                }
            } while (knowledgeBase.getType(newName) != null);
            Type newType = new Type(knowledgeBase, newName);
            if(type != Type.UNIVERSAL) {
                newType.addDirectSupertype(type);
            }
            node.setType(newType);
            canvas.repaint();
        }

        public CreateNewTypeListener(Node node, Type type) {
            this.node = node;
            this.type = type;
        }
    }
}