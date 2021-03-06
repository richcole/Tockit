/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui.eventhandlers;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

import org.tockit.cgs.model.KnowledgeBase;
import org.tockit.cgs.model.Type;
import org.tockit.cgs.model.events.NewTypeCreatedEvent;
import org.tockit.crepe.gui.treeviews.TypeHierachyTreeNode;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class TypeHierachyUpdateHandler implements EventBrokerListener {
    private JTree treeview;

    public TypeHierachyUpdateHandler(JTree treeview, EventBroker eventBroker) {
        this.treeview = treeview;
        eventBroker.subscribe(this, NewTypeCreatedEvent.class, KnowledgeBase.class);
    }

    public void processEvent(Event e) {
        treeview.setModel(new DefaultTreeModel(new TypeHierachyTreeNode(Type.UNIVERSAL, null)));
    }
}
