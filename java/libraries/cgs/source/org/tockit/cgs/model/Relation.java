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
import java.util.Vector;

import org.jdom.Element;

public class Relation {
    private Element element = null;
    private KnowledgeBase knowledgeBase = null;
    private static Vector<Relation> universal = new Vector<Relation>();
    private static KnowledgeBase defaultKB = null;

    private static class UniversalRelation extends Relation {
        private String name;
        int arity;
        public UniversalRelation(KnowledgeBase knowledgeBase, String name, int arity) {
            super(knowledgeBase);
            this.name = name;
            this.arity = arity;
        }

        @Override
		public Relation[] getDirectSupertypes() {
            return new Relation[0];
        }

        @Override
		public String getName() {
            return this.name;
        }

        @Override
		public void setName(String name) {
            this.name = name;
        }

        @Override
		public Type[] getSignature() {
            Type[] retVal = new Type[arity];
            for (int i = 0; i < retVal.length; i++) {
                retVal[i] = Type.UNIVERSAL;
            }
            return retVal;
        }

        @Override
		public int getArity() {
            return this.arity;
        }
    }

    public static void setDefaultKnowledgeBase(KnowledgeBase knowledgeBase) {
        defaultKB = knowledgeBase;
        universal.clear();
    }

    protected Relation(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public Relation(KnowledgeBase knowledgeBase, Element element) {
        this(knowledgeBase);
        this.element = element;
        this.knowledgeBase.addRelation(this, false);
    }

    public static Relation getUniversal(int arity) {
        while(universal.size() < arity) {
            int newArity = universal.size() + 1;
            Relation newUniversal = new UniversalRelation(defaultKB, "[universal(" + String.valueOf(newArity) + ")]", newArity);
            universal.setSize(newArity);
            universal.set(newArity - 1, newUniversal);
        }
        return universal.get(arity - 1);
    }

    public Relation(KnowledgeBase knowledgeBase, String name, Type[] signature) {
        this(knowledgeBase);
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

	@SuppressWarnings("unchecked")
	public Type[] getSignature() {
        List<Element> signatureChildren = this.element.getChildren("type");
        Type[] retVal = new Type[signatureChildren.size()];
        int pos = 0;
        for (Iterator<Element> iterator = signatureChildren.iterator(); iterator.hasNext();) {
            Element typeElem = iterator.next();
            Type type = this.knowledgeBase.getType(typeElem.getAttributeValue("name"));
            retVal[pos] = type;
            pos++;
        }
        return retVal;
    }

    @SuppressWarnings("unchecked")
	public Relation[] getDirectSupertypes() {
        List<Element> superrelationChildren = this.element.getChildren("superrelation");
        if(superrelationChildren.size() == 0) {
            return new Relation[]{getUniversal(this.getArity())};
        }
        Relation[] retVal = new Relation[superrelationChildren.size()];
        int pos = 0;
        for (Iterator<Element> iterator = superrelationChildren.iterator(); iterator.hasNext();) {
            Element superrelationElem = iterator.next();
            Relation type = this.knowledgeBase.getRelation(superrelationElem.getAttributeValue("name"));
            retVal[pos] = type;
            pos++;
        }
        return retVal;
    }

    public void addDirectSupertype(Relation other) {
        Element superrelElem = new Element("superrelation");
        superrelElem.setAttribute("name", other.getName());
        this.element.addContent(superrelElem);
    }

    Element getElement() {
        return this.element;
    }

    @SuppressWarnings("unchecked")
	public int getArity() {
        List<Element> signatureChildren = this.element.getChildren("type");
        return signatureChildren.size();
    }

    public Relation[] getDirectSubtypes() {
        Collection<Relation> subtypes = this.knowledgeBase.getDirectSubtypes(this);
        Relation[] retVal = new Relation[subtypes.size()];
        subtypes.toArray(retVal);
        return retVal;
    }

}
