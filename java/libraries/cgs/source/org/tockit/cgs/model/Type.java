/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model;

import org.jdom.Element;

import java.util.*;

public class Type {
    private Element element = null;
    private KnowledgeBase knowledgeBase = null;

    public Type(KnowledgeBase knowledgeBase, String name) {
        this.knowledgeBase = knowledgeBase;
        element = new Element("type");
        element.setAttribute("name", name);
        knowledgeBase.addType(this, true);
    }

    public Type(KnowledgeBase knowledgeBase, Element element) {
        this.knowledgeBase = knowledgeBase;
        this.element = element;
        knowledgeBase.addType(this, false);
    }

    public String getName() {
        return this.element.getAttributeValue("name");
    }

    public void setName(String name) {
        this.element.setAttribute("name", name);
    }

    public Type[] getSupertypes() {
        List supertypeChildren = this.element.getChildren("supertype");
        Type[] retVal = new Type[supertypeChildren.size()];
        int pos = 0;
        for (Iterator iterator = supertypeChildren.iterator(); iterator.hasNext();) {
            Element supertypeElem = (Element) iterator.next();
            Type type = this.knowledgeBase.getType(supertypeElem.getTextNormalize());
            retVal[pos] = type;
            pos++;
        }
        return retVal;
    }

    public void addSupertype(Type other) {
        Element supertypeElem = new Element("supertype");
        supertypeElem.addContent(other.getName());
        this.element.addContent(supertypeElem);
    }

    public Element getElement() {
        return this.element;
    }
}
