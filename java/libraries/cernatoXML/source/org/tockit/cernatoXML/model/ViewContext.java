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


public class ViewContext implements Context {
    private CernatoTable context;
    private ScalingRelation relation;
    private Set attributes;
    private String name;

    private class ScalingRelation implements BinaryRelation {
        public boolean contains(Object domainObject, Object rangeObject) {
            if (!(domainObject instanceof CernatoObject)) {
                return false;
            }
            CernatoObject fcaObject = (CernatoObject) domainObject;
            if (!(rangeObject instanceof Criterion)) {
                return false;
            }
            Criterion criterion = (Criterion) rangeObject;
            AttributeValue relationValue = context.getRelationship(fcaObject, criterion.getProperty());
            return criterion.getValueGroup().containsValue(relationValue);
        }
    }

    public ViewContext(CernatoTable context, View view) {
        this.context = context;
        this.relation = new ScalingRelation();
        attributes = new HashSet();
        for (Iterator iterator = view.getCriteria().iterator(); iterator.hasNext();) {
            Criterion criterion = (Criterion) iterator.next();
            attributes.add(criterion);
        }
        this.name = view.getName();
    }

    public Set getObjects() {
        return context.getObjects();
    }

    public Set getAttributes() {
        return attributes;
    }

    public BinaryRelation getRelation() {
        return relation;
    }

	public String getName() {
		return this.name;
	}
}
