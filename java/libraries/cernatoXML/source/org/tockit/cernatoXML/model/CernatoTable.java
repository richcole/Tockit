/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

public class CernatoTable {
    private List objects = new ArrayList();
    private List properties = new ArrayList();
    private Hashtable relation = new Hashtable();

    public CernatoTable() {
    }

    public void add(CernatoObject object) {
        objects.add(object);
        relation.put(object, new Hashtable());
    }

    public Collection getObjects() {
        return objects;
    }

    public void add(Property property) {
        properties.add(property);
    }

    public Collection getAttributes() {
        return properties;
    }

    public void setRelationship(CernatoObject object, Property property, AttributeValue value) {
        Hashtable row = (Hashtable) relation.get(object);
        row.put(property, value);
    }

    public AttributeValue getRelationship(CernatoObject object, Property property) {
        Hashtable row = (Hashtable) relation.get(object);
        return (AttributeValue) row.get(property);
    }
}
