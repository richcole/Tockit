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
import org.tockit.cgs.model.*;
import org.tockit.crepe.view.NodeView;
import org.tockit.crepe.view.LinkView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

public class LinkContextMenuHandler implements EventListener {
    private Canvas canvas;

    public LinkContextMenuHandler(Canvas canvas, EventBroker eventBroker) {
        this.canvas = canvas;
        eventBroker.subscribe(this, CanvasItemContextMenuRequestEvent.class, LinkView.class);
    }

    public void processEvent(Event e) {
        CanvasItemEventWithPosition itemEvent = null;
        try {
            itemEvent = (CanvasItemEventWithPosition) e;
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to CanvasItemEventWithPositions only");
        }
        LinkView linkView = null;
        try {
            linkView = (LinkView) itemEvent.getItem();
        } catch (ClassCastException e1) {
            throw new RuntimeException(getClass().getName() +
                    " has to be subscribed to events from LinkViews only");
        }
        openPopupMenu(linkView, itemEvent.getAWTPosition());
    }

    public void openPopupMenu(final LinkView linkView, Point2D screenPosition) {
        // create the menu
        JPopupMenu popupMenu = new JPopupMenu();
        Link link = linkView.getLink();
        if(link.getType().getDirectSubtypes().length != 0) {
            createRestrictTypeMenu(popupMenu, link, link.getType());
        }
        JMenuItem menuItem = new JMenuItem("Remove");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ///@todo remove link and lines
            }
        });
        popupMenu.add(menuItem);
        popupMenu.show(this.canvas, (int) screenPosition.getX(), (int) screenPosition.getY());
    }

    private void createRestrictTypeMenu(JPopupMenu parentMenu, final Link link, final Relation type) {
        JMenu restrictTypeMenu = new JMenu("Restrict Type");
        Relation[] directSubtypes = type.getDirectSubtypes();
        if(directSubtypes.length != 0) {
            restrictTypeMenu.addSeparator();
        }
        for (int i = 0; i < directSubtypes.length; i++) {
            Relation directSubtype = directSubtypes[i];
            JMenu subMenu = new JMenu(directSubtype.getName());
            createRestrictTypeSubMenu(subMenu, link, directSubtype);
            restrictTypeMenu.add(subMenu);
        }
        parentMenu.add(restrictTypeMenu);
    }

    private void createRestrictTypeSubMenu(JMenu parentMenu, final Link link, final Relation type) {
        JMenuItem menuItem;
        menuItem = new JMenuItem("Use this type");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                link.setType(type);
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
        Relation[] directSubtypes = type.getDirectSubtypes();
        if(directSubtypes.length != 0) {
            parentMenu.addSeparator();
        }
        for (int i = 0; i < directSubtypes.length; i++) {
            Relation directSubtype = directSubtypes[i];
            JMenu subMenu = new JMenu(directSubtype.getName());
            createRestrictTypeSubMenu(subMenu, link, directSubtype);
            parentMenu.add(subMenu);
        }
    }
}
