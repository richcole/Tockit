/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model;

import org.jdom.Element;

import java.util.List;
import java.util.Iterator;

public class Link {
    private Element element = null;
    private KnowledgeBase knowledgeBase = null;

    public Link(KnowledgeBase knowledgeBase, Relation type, Node[] references) {
        this.knowledgeBase = knowledgeBase;
        element = new Element("link");
        element.setAttribute("id", knowledgeBase.createNewLinkId());
        element.setAttribute("relation", type.getName());
        for (int i = 0; i < references.length; i++) {
            Node reference = references[i];
            Element refElem = new Element("reference");
            refElem.addContent(reference.getId());
            this.element.addContent(refElem);
        }
        knowledgeBase.addLink(this);
    }

    public Link(KnowledgeBase knowledgeBase, Element element) {
        this.knowledgeBase = knowledgeBase;
        this.element = element;
        this.knowledgeBase.reserveLinkId(this.element.getAttributeValue("id"));
        knowledgeBase.addLink(this);
    }

    public Node[] getReferences() {
        List referenceChildren = this.element.getChildren("reference");
        Node[] retVal = new Node[referenceChildren.size()];
        int pos = 0;
        for (Iterator iterator = referenceChildren.iterator(); iterator.hasNext();) {
            Element referenceElem = (Element) iterator.next();
            Node node = this.knowledgeBase.getNode(referenceElem.getTextNormalize());
            retVal[pos] = node;
            pos++;
        }
        return retVal;
    }

    public Relation getType() {
        return this.knowledgeBase.getRelation(this.element.getAttributeValue("relation"));
    }

    public void setType(Relation type) {
        this.element.setAttribute("type", type.getName());
    }

    public String getId() {
        return this.element.getAttributeValue("id");
    }

    Element getElement() {
        return this.element;
    }
}
