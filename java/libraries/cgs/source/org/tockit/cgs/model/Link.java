/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

public class Link {
    private Element element = null;
    private KnowledgeBase knowledgeBase = null;

    public Link(KnowledgeBase knowledgeBase, Relation type, Node[] references) {
        if (type == null) {
            throw new RuntimeException("A link has to have a relation type specified.");
        }
        this.knowledgeBase = knowledgeBase;
        element = new Element("link");
        element.setAttribute("id", knowledgeBase.createNewLinkId());
        if(type != Relation.getUniversal(references.length)) {
            element.setAttribute("relation", type.getName());
        }
        setReferences(references);
        knowledgeBase.addLink(this);
    }

    public void setReferences(Node[] references) {
        this.element.removeChildren("reference");
        for (int i = 0; i < references.length; i++) {
            Node reference = references[i];
            Element refElem = new Element("reference");
            refElem.addContent(reference.getId());
            this.element.addContent(refElem);
        }
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
        String relationValue = this.element.getAttributeValue("relation");
        if (relationValue == null) {
            return Relation.getUniversal(this.getReferences().length);
        } else {
            return this.knowledgeBase.getRelation(relationValue);
        }
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

    public void destroy() {
        this.knowledgeBase.remove(this);
    }


    public double getX() {
        String xAtt = this.element.getAttributeValue("x");
        if (xAtt == null) {
            return 0;
        }
        return Double.parseDouble(xAtt);
    }

    public double getY() {
        String yAtt = this.element.getAttributeValue("y");
        if (yAtt == null) {
            return 0;
        }
        return Double.parseDouble(yAtt);
    }

    public void setPosition(double x, double y) {
        this.element.setAttribute("x", String.valueOf(x));
        this.element.setAttribute("y", String.valueOf(y));
    }

    public boolean hasPosition() {
        return this.element.getAttributeValue("x") != null;
    }
}
