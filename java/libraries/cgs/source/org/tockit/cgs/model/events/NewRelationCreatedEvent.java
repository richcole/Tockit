/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model.events;

import org.tockit.cgs.model.*;

public class NewRelationCreatedEvent extends KnowledgeBaseChangeEvent {
    private Relation relation;
    public NewRelationCreatedEvent(KnowledgeBase subject, Relation relation) {
        super(subject);
        this.relation = relation;
    }

    public Relation getRelation() {
        return relation;
    }
}
