/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui.eventhandlers;

import org.tockit.events.*;
import org.tockit.cgs.model.events.NewTypeCreatedEvent;
import org.tockit.cgs.model.KnowledgeBase;
import org.tockit.cgs.model.Type;
import org.tockit.crepe.gui.treeviews.TypeHierachyTreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

public class TypeHierachyUpdateHandler implements EventListener {
    private JTree treeview;

    public TypeHierachyUpdateHandler(JTree treeview, EventBroker eventBroker) {
        this.treeview = treeview;
        eventBroker.subscribe(this, NewTypeCreatedEvent.class, KnowledgeBase.class);
    }

    public void processEvent(Event e) {
        treeview.setModel(new DefaultTreeModel(new TypeHierachyTreeNode(Type.UNIVERSAL, null)));
    }
}
