/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model;

import java.util.Hashtable;
import java.util.Set;

import org.tockit.util.ListSet;
import org.tockit.util.ListSetImplementation;

public class CernatoTable {
    private ListSet<CernatoObject> objects = new ListSetImplementation<CernatoObject>();
    private ListSet<Property> properties = new ListSetImplementation<Property>();
    private Hashtable<CernatoObject, Hashtable<Property, Value>> relation = 
    	new Hashtable<CernatoObject, Hashtable<Property, Value>>();

    public CernatoTable() {
        // nothing to do
    }

    public void add(CernatoObject object) {
        objects.add(object);
        relation.put(object, new Hashtable<Property, Value>());
    }

    public Set<CernatoObject> getObjects() {
        return objects;
    }

    public void add(Property property) {
        properties.add(property);
    }

    public Set<Property> getProperties() {
        return properties;
    }

    public void setRelationship(CernatoObject object, Property property, Value value) {
        Hashtable<Property, Value> row = relation.get(object);
        row.put(property, value);
    }

    public Value getRelationship(CernatoObject object, Property property) {
        Hashtable<Property, Value> row = relation.get(object);
        return row.get(property);
    }
}
