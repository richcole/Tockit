/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model.events;

import org.tockit.cgs.model.Instance;
import org.tockit.cgs.model.KnowledgeBase;

public class NewInstanceCreatedEvent extends KnowledgeBaseChangeEvent {
    private Instance instance;
    public NewInstanceCreatedEvent(KnowledgeBase subject, Instance instance) {
        super(subject);
        this.instance = instance;
    }

    public Instance getInstance() {
        return instance;
    }
}
