/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.tockit.context.model.BinaryRelation;
import org.tockit.context.model.Context;


public class ViewContext implements Context<CernatoObject, Criterion> {
    private CernatoTable context;
    private ScalingRelation relation;
    private Set<Criterion> attributes;
    private String name;

    private class ScalingRelation implements BinaryRelation<CernatoObject, Criterion> {
        public boolean contains(CernatoObject domainObject, Criterion rangeObject) {
            Value relationValue = context.getRelationship(domainObject, rangeObject.getProperty());
            return rangeObject.getValueGroup().containsValue(relationValue);
        }
    }

    public ViewContext(CernatoTable context, View view) {
        this.context = context;
        this.relation = new ScalingRelation();
        attributes = new HashSet<Criterion>();
        for (Iterator<Criterion> iterator = view.getCriteria().iterator(); iterator.hasNext();) {
            Criterion criterion = iterator.next();
            attributes.add(criterion);
        }
        this.name = view.getName();
    }

    public Set<CernatoObject> getObjects() {
        return context.getObjects();
    }

    public Set<Criterion> getAttributes() {
        return attributes;
    }

    public BinaryRelation<CernatoObject, Criterion> getRelation() {
        return relation;
    }

	public String getName() {
		return this.name;
	}
}
