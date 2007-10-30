/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.context.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

public class BinaryRelationImplementation<D,R> implements BinaryRelation<D,R> {
    private Map<D, Collection<R>> rows = new Hashtable<D, Collection<R>>();

    public boolean contains(D domainObject, R rangeObject) {
        Collection<R> objectAttributes = rows.get(domainObject);
        if (objectAttributes == null) {
            return false;
        }
        return objectAttributes.contains(rangeObject);
    }

    public void insert(D domainObject, R rangeObject) {
        Collection<R> objectAttributes = rows.get(domainObject);
        if (objectAttributes == null) {
            objectAttributes = new HashSet<R>();
            rows.put(domainObject, objectAttributes);
        }
        objectAttributes.add(rangeObject);
    }

    public void remove(D domainObject, R rangeObject) {
        Collection<R> objectAttributes = rows.get(domainObject);
        if (objectAttributes == null) {
            return; // nothing to remove
        }
        objectAttributes.remove(rangeObject);
    }
}