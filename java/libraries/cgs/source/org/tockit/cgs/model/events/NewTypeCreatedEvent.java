/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model.events;

import org.tockit.cgs.model.KnowledgeBase;
import org.tockit.cgs.model.Type;

public class NewTypeCreatedEvent extends KnowledgeBaseChangeEvent {
    private Type type;
    public NewTypeCreatedEvent(KnowledgeBase subject, Type type) {
        super(subject);
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
