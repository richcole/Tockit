/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui.eventhandlers;

import org.tockit.events.*;
import org.tockit.events.Event;
import org.tockit.cgs.model.events.NewRelationCreatedEvent;
import org.tockit.cgs.model.*;
import org.tockit.crepe.gui.treeviews.RelationHierachyTreeNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;

public class RelationHierachyUpdateHandler implements EventListener {
    private JTree treeview;
    private int arity;

    public RelationHierachyUpdateHandler(JTree treeview, int arity, EventBroker eventBroker) {
        this.treeview = treeview;
        this.arity = arity;
        eventBroker.subscribe(this, NewRelationCreatedEvent.class, KnowledgeBase.class);
    }

    public void processEvent(Event e) {
        treeview.setModel(new DefaultTreeModel(new RelationHierachyTreeNode(Relation.getUniversal(arity), null)));
    }
}
