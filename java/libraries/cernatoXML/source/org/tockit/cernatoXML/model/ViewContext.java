/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import ord.tockit.context.model.BinaryRelation;
import ord.tockit.context.model.FCAElement;
import ord.tockit.context.model.FCAElementImplementation;

public class ViewContext {
    private CernatoModel model;
    private ScalingRelation relation;
    private Collection attributes;
    private String name;

    private class ScalingRelation implements BinaryRelation {
        public boolean contains(Object domainObject, Object rangeObject) {
            if (!(domainObject instanceof CernatoObject)) {
                return false;
            }
            CernatoObject fcaObject = (CernatoObject) domainObject;
            if (!(rangeObject instanceof FCAElement)) {
                return false;
            }
            FCAElement attribute = (FCAElement) rangeObject;
            if (!(attribute.getData() instanceof Criterion)) {
                return false;
            }
            Criterion criterion = (Criterion) attribute.getData();
            AttributeValue relationValue = model.getContext().getRelationship(fcaObject, criterion.getProperty());
            return criterion.getValueGroup().containsValue(relationValue);
        }
    }

    public ViewContext(CernatoModel model, View view) {
        this.model = model;
        this.relation = new ScalingRelation();
        attributes = new HashSet();
        for (Iterator iterator = view.getCriteria().iterator(); iterator.hasNext();) {
            Criterion criterion = (Criterion) iterator.next();
            attributes.add(new FCAElementImplementation(criterion, null));
        }
        this.name = view.getName();
    }

    public Collection getObjects() {
        return model.getContext().getObjects();
    }

    public Collection getAttributes() {
        return attributes;
    }

    public BinaryRelation getRelation() {
        return relation;
    }

	public String getName() {
		return this.name;
	}
}
