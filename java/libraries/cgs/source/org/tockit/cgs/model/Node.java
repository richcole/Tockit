/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model;

import org.jdom.Element;

public class Node {
    private Element element = null;
    private KnowledgeBase knowledgeBase = null;

    public Node(KnowledgeBase knowledgeBase, Type type, Instance referent, ConceptualGraph descriptor) {
        this.knowledgeBase = knowledgeBase;
        element = new Element("node");
        element.setAttribute("id", knowledgeBase.createNewNodeId());
        element.setAttribute("type", type.getName());
        if(referent != null) {
            element.setAttribute("referent", referent.getIdentifier());
        }
        if(descriptor != null) {
            element.addContent(descriptor.getElement());
        }
        knowledgeBase.addNode(this);
    }

    public Node(KnowledgeBase knowledgeBase, Element element) {
        this.knowledgeBase = knowledgeBase;
        this.element = element;
        Element descriptorElement = element.getChild("conceptualGraph");
        if(descriptorElement != null) {
            new ConceptualGraph(knowledgeBase, descriptorElement);
        }
        this.knowledgeBase.reserveNodeId(this.element.getAttributeValue("id"));
        knowledgeBase.addNode(this);
    }

    public String getId() {
        return element.getAttributeValue("id");
    }

    public Type getType() {
        String typeId = element.getAttributeValue("type");
        return knowledgeBase.getType(typeId);
    }

    public void setType(Type type) {
        element.setAttribute("type", type.getName());
    }

    public Instance getReferent() {
        String instanceId = element.getAttributeValue("referent");
        if(instanceId != null) {
            return knowledgeBase.getInstance(instanceId);
        }
        else {
            return null;
        }
    }

    public void setReferent(Instance referent) {
        element.setAttribute("referent", referent.getIdentifier());
    }

    public ConceptualGraph getDescriptor() {
        String descriptorId = element.getAttributeValue("conceptualGraph");
        return knowledgeBase.getGraph(descriptorId);
    }

    public void setDescriptor(ConceptualGraph descriptor) {
        element.setAttribute("conceptualGraph", descriptor.getId());
    }

    Element getElement() {
        return this.element;
    }

    /**
     * Detaches the node from the graph it belongs to.
     *
     * A node is not allowed to be used in multiple graphs, so it has to be
     * detached before it is inserted somewhere else.
     */
    public void detach() {
        this.element.detach();
    }
}
