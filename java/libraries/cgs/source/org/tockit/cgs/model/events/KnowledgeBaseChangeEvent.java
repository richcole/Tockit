/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model.events;

import org.tockit.events.Event;
import org.tockit.cgs.model.KnowledgeBase;

public class KnowledgeBaseChangeEvent implements Event {
    private KnowledgeBase knowledgeBase;

    public KnowledgeBaseChangeEvent(KnowledgeBase subject) {
        this.knowledgeBase = subject;
    }

    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public Object getSubject() {
        return knowledgeBase;
    }
}
