/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cgs.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * In any place where references to this type are used, a null reference is supposed to refer to the top (universal)
 * type of the hierarchy. Types themself are supposed to be below this top type unless they have supertypes defined.
 */
public class Type {
    private static class ImplicitType extends Type {
        private String name;
        public ImplicitType(KnowledgeBase knowledgeBase, String name) {
            super(knowledgeBase);
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Type[] getDirectSupertypes() {
            return new Type[0];
        }

        public Element getElement() {
            return null;
        }
    }

    public static Type UNIVERSAL = null;
    public static Type ABSURD = null;

    private Element element = null;
    private KnowledgeBase knowledgeBase = null;

    /**
     * @todo evil hack -- do something better
     */
    public static void setDefaultKnowledgeBase(KnowledgeBase knowledgeBase) {
        Type.UNIVERSAL = new ImplicitType(knowledgeBase, "[universal]");
        Type.ABSURD = new ImplicitType(knowledgeBase, "[absurd]");
    }

    private Type(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public Type(KnowledgeBase knowledgeBase, String name) {
        this(knowledgeBase, name, null);
    }

    public Type(KnowledgeBase knowledgeBase, String name, Type[] directSupertypes) {
        this.knowledgeBase = knowledgeBase;
        element = new Element("type");
        element.setAttribute("name", name);
        if(directSupertypes != null) {
            for (int i = 0; i < directSupertypes.length; i++) {
                Type supertype = directSupertypes[i];
                this.addDirectSupertype(supertype);
            }
        }
        knowledgeBase.addType(this, true);
    }

    public Type(KnowledgeBase knowledgeBase, Element element) {
        this.knowledgeBase = knowledgeBase;
        this.element = element;
        knowledgeBase.addType(this, false);
    }

    public static Type getUniversal() {
        return UNIVERSAL;
    }

    public static Type getAbsurd() {
        return ABSURD;
    }

    public String getName() {
        return this.element.getAttributeValue("name");
    }

    public void setName(String name) {
        this.element.setAttribute("name", name);
    }

    public Type[] getDirectSupertypes() {
        List supertypeChildren = this.element.getChildren("supertype");
        if(supertypeChildren.size() == 0) {
            return new Type[]{Type.UNIVERSAL};
        }
        Type[] retVal = new Type[supertypeChildren.size()];
        int pos = 0;
        for (Iterator iterator = supertypeChildren.iterator(); iterator.hasNext();) {
            Element supertypeElem = (Element) iterator.next();
            Type type = this.knowledgeBase.getType(supertypeElem.getAttributeValue("name"));
            retVal[pos] = type;
            pos++;
        }
        return retVal;
    }

    public void addDirectSupertype(Type other) {
        if( (other != null) && (other != Type.UNIVERSAL) ) {
            Element supertypeElem = new Element("supertype");
            supertypeElem.setAttribute("name",other.getName());
            this.element.addContent(supertypeElem);
        }
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

    public Type[] getDirectSubtypes() {
        Collection subtypes = this.knowledgeBase.getDirectSubtypes(this);
        Type[] retVal = new Type[subtypes.size()];
        subtypes.toArray(retVal);
        return retVal;
    }
}
