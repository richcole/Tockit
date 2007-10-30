/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model.events;

import org.tockit.cgs.model.KnowledgeBase;
import org.tockit.events.Event;

public class KnowledgeBaseChangeEvent implements Event<KnowledgeBase> {
    private KnowledgeBase knowledgeBase;

    public KnowledgeBaseChangeEvent(KnowledgeBase subject) {
        this.knowledgeBase = subject;
    }

    /**
     * @deprecated use getSubject() instead.
     */
    @Deprecated
	public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public KnowledgeBase getSubject() {
        return knowledgeBase;
    }
}
