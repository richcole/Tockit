/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model;

import org.jdom.Element;

public class Instance {
    private Element element = null;
    private KnowledgeBase knowledgeBase = null;

    public Instance(KnowledgeBase knowledgeBase, String identifier, Type type) {
        this.knowledgeBase = knowledgeBase;
        element = new Element("instance");
        element.setAttribute("identifier", identifier);
        element.setAttribute("type", type.getName());
        knowledgeBase.addInstance(this, true);
    }

    public Instance(KnowledgeBase knowledgeBase, Element element) {
        this.knowledgeBase = knowledgeBase;
        this.element = element;
        knowledgeBase.addInstance(this, false);
    }

    public String getIdentifier() {
        return this.element.getAttributeValue("identifier");
    }

    public void setIdentifier(String identifier) {
        this.element.setAttribute("identifier", identifier);
    }

    public Type getType() {
        return this.knowledgeBase.getType(this.element.getAttributeValue("type"));
    }

    public void setType(Type type) {
        this.element.setAttribute("type", type.getName());
    }

    public Element getElement() {
        return this.element;
    }

    public String toString() {
        return this.getType().getName() + ": " + this.getIdentifier();
    }

}
