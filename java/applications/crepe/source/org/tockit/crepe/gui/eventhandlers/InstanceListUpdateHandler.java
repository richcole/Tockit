/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.crepe.gui.eventhandlers;

import java.util.Collection;

import javax.swing.JList;

import org.tockit.cgs.model.KnowledgeBase;
import org.tockit.cgs.model.events.NewInstanceCreatedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventListener;

public class InstanceListUpdateHandler implements EventListener {
    private JList listView;

    public InstanceListUpdateHandler(JList listView, EventBroker eventBroker) {
        this.listView = listView;
        eventBroker.subscribe(this, NewInstanceCreatedEvent.class, KnowledgeBase.class);
    }

    public void processEvent(Event e) {
        NewInstanceCreatedEvent nice = (NewInstanceCreatedEvent) e;
        Collection instances = nice.getKnowledgeBase().getInstances();
        this.listView.setListData(instances.toArray());
    }
}
