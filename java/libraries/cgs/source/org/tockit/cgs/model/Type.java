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

/**
 * In any place where references to this type are used, a null reference is supposed to refer to the top (universal)
 * type of the hierarchy. Types themself are supposed to be below this top type unless they have supertypes defined.
 */
public class Type {
    private static class ImplicitType extends Type {
        public ImplicitType() {
        }

        public String getName() {
            return "";
        }

        public Type[] getDirectSupertypes() {
            return new Type[0];
        }

        public Element getElement() {
            return null;
        }
    }

    public static final Type UNIVERSAL = new ImplicitType();
    public static final Type ABSURD = new ImplicitType();

    private Element element = null;
    private KnowledgeBase knowledgeBase = null;

    private Type() {
    }

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

    public Type[] getDirectSupertypes() {
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

    public void addDirectSupertype(Type other) {
        Element supertypeElem = new Element("supertype");
        supertypeElem.addContent(other.getName());
        this.element.addContent(supertypeElem);
    }

    public Element getElement() {
        return this.element;
    }

    public boolean hasSupertype(Type otherType) {
        if(this == otherType) {
            return true;
        }
        if(otherType == Type.UNIVERSAL) {
            return true;
        }
        Type[] supertypes = getDirectSupertypes();
        for (int i = 0; i < supertypes.length; i++) {
            Type supertype = supertypes[i];
            if(supertype.hasSupertype(otherType)) {
                return true;
            }
        }
        return false;
    }
}
