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

public class Relation {
    private Element element = null;
    private KnowledgeBase knowledgeBase = null;
    private static Vector universal = new Vector();
    private static KnowledgeBase defaultKB = null;

    private static class UniversalRelation extends Relation {
        public UniversalRelation(KnowledgeBase knowledgeBase, String name, int arity) {
            super(knowledgeBase, name, new Type[arity]);
        }

        public Relation[] getDirectSupertypes() {
            return new Relation[0];
        }
    }

    public static void setDefaultKnowledgeBase(KnowledgeBase knowledgeBase) {
        defaultKB = knowledgeBase;
    }

    public Relation(KnowledgeBase knowledgeBase, Element element) {
        this.knowledgeBase = knowledgeBase;
        this.element = element;
        this.knowledgeBase.addRelation(this, false);
    }

    public static Relation getUniversal(int arity) {
        while(universal.size() < arity) {
            int newArity = universal.size() + 1;
            Relation newUniversal = new UniversalRelation(defaultKB, "[universal(" + String.valueOf(newArity) + ")]", newArity);
            universal.setSize(arity);
            universal.set(newArity - 1, newUniversal);
        }
        return (Relation) universal.get(arity - 1);
    }

    public Relation(KnowledgeBase knowledgeBase, String name, Type[] signature) {
        this.knowledgeBase = knowledgeBase;
        element = new Element("relation");
        this.element.setAttribute("name", name);
        for (int i = 0; i < signature.length; i++) {
            Type type = signature[i];
            Element typeElem = new Element("type");
            if (type != null) {
                typeElem.setAttribute("name", type.getName());
            }
            this.element.addContent(typeElem);
        }
        this.knowledgeBase.addRelation(this, true);
    }

    public String getName() {
        return this.element.getAttributeValue("name");
    }

    public void setName(String name) {
        this.element.setAttribute("name", name);
    }

    public Type[] getSignature() {
        List signatureChildren = this.element.getChildren("type");
        Type[] retVal = new Type[signatureChildren.size()];
        int pos = 0;
        for (Iterator iterator = signatureChildren.iterator(); iterator.hasNext();) {
            Element typeElem = (Element) iterator.next();
            Type type = this.knowledgeBase.getType(typeElem.getAttributeValue("name"));
            retVal[pos] = type;
            pos++;
        }
        return retVal;
    }

    public Relation[] getDirectSupertypes() {
        List superrelationChildren = this.element.getChildren("superrelation");
        Relation[] retVal = new Relation[superrelationChildren.size()];
        int pos = 0;
        for (Iterator iterator = superrelationChildren.iterator(); iterator.hasNext();) {
            Element superrelationElem = (Element) iterator.next();
            Relation type = this.knowledgeBase.getRelation(superrelationElem.getTextNormalize());
            retVal[pos] = type;
            pos++;
        }
        return retVal;
    }

    public void addDirectSupertype(Relation other) {
        Element superrelElem = new Element("superrelation");
        superrelElem.addContent(other.getName());
        this.element.addContent(superrelElem);
    }

    Element getElement() {
        return this.element;
    }

    public int getArity() {
        List signatureChildren = this.element.getChildren("type");
        return signatureChildren.size();
    }

    public Relation[] getDirectSubtypes() {
        Collection subtypes = this.knowledgeBase.getDirectSubtypes(this);
        Relation[] retVal = new Relation[subtypes.size()];
        subtypes.toArray(retVal);
        return retVal;
    }

}
