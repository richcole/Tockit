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
import org.tockit.cgs.model.Relation;
import org.tockit.cgs.model.events.NewRelationCreatedEvent;
import org.tockit.crepe.gui.treeviews.RelationHierachyTreeNode;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

public class RelationHierachyUpdateHandler implements EventBrokerListener {
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
