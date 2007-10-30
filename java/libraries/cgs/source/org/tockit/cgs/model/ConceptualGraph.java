/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

public class ConceptualGraph {
    private Element element = null;
    private KnowledgeBase knowledgeBase = null;

    public ConceptualGraph(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
        this.element = new Element("conceptualGraph");
        this.element.setAttribute("id", this.knowledgeBase.createNewGraphId());
        knowledgeBase.addGraph(this, true);
    }

    @SuppressWarnings("unchecked")
	public ConceptualGraph(KnowledgeBase knowledgeBase, Element element) {
        this.knowledgeBase = knowledgeBase;
        this.element = element;
        List<Element> nodes = this.element.getChildren("node");
        for (Iterator<Element> iterator = nodes.iterator(); iterator.hasNext();) {
            Element nodeElem = iterator.next();
            new Node(knowledgeBase, nodeElem);
        }
        List<Element> links = this.element.getChildren("link");
        for (Iterator<Element> iterator = links.iterator(); iterator.hasNext();) {
            Element linkElem = iterator.next();
            new Link(knowledgeBase, linkElem);
        }
        this.knowledgeBase.reserveGraphId(this.element.getAttributeValue("id"));
        knowledgeBase.addGraph(this, false);
    }

    public void addNode(Node node) {
        this.element.addContent(node.getElement());
    }

    public void addLink(Link link) {
        this.element.addContent(link.getElement());
    }

    @SuppressWarnings("unchecked")
	public Node[] getNodes() {
        List<Element> nodeChildren = this.element.getChildren("node");
        Node[] retVal = new Node[nodeChildren.size()];
        int pos = 0;
        for (Iterator<Element> iterator = nodeChildren.iterator(); iterator.hasNext();) {
            Element nodeElem = iterator.next();
            Node node = this.knowledgeBase.getNode(nodeElem.getAttributeValue("id"));
            retVal[pos] = node;
            pos++;
        }
        return retVal;
    }

    @SuppressWarnings("unchecked")
	public Link[] getLinks() {
        List<Element> linkChildren = this.element.getChildren("link");
        Link[] retVal = new Link[linkChildren.size()];
        int pos = 0;
        for (Iterator<Element> iterator = linkChildren.iterator(); iterator.hasNext();) {
            Element linkElem = iterator.next();
            Link link = this.knowledgeBase.getLink(linkElem.getAttributeValue("id"));
            retVal[pos] = link;
            pos++;
        }
        return retVal;
    }

    public Element getElement() {
        return this.element;
    }

    public String getId() {
        return this.element.getAttributeValue("id");
    }

    @SuppressWarnings("unchecked")
	public void remove(Node node) {
        List<Element> nodeChildren = this.element.getChildren("node");
        List<Element> toRemove = new ArrayList<Element>();
        for (Iterator<Element> iterator = nodeChildren.iterator(); iterator.hasNext();) {
            Element curElement = iterator.next();
            if(curElement.getAttributeValue("id").equals(node.getId())) {
                toRemove.add(curElement);
            }
        }
        for (Iterator<Element> it = toRemove.iterator(); it.hasNext();) {
            Element curElement = it.next();
            curElement.detach();
        }
        ///@todo recurse into descriptors
    }

    @SuppressWarnings("unchecked")
	public void remove(Link link) {
        List<Element> linkChildren = this.element.getChildren("link");
        List<Element> toRemove = new ArrayList<Element>();
        for (Iterator<Element> iterator = linkChildren.iterator(); iterator.hasNext();) {
            Element curElement = iterator.next();
            if(curElement.getAttributeValue("id").equals(link.getId())) {
                toRemove.add(curElement);
            }
        }
        for (Iterator<Element> it = toRemove.iterator(); it.hasNext();) {
            Element curElement = it.next();
            curElement.detach();
        }
        ///@todo recurse into descriptors
    }

    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }
}
