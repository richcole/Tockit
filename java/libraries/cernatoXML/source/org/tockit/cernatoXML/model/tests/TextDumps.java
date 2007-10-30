/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model.tests;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


import org.tockit.cernatoXML.model.*;
import org.tockit.context.model.BinaryRelation;

public class TextDumps {
    public static final void dump(CernatoTable context, PrintStream stream) {
        Set<CernatoObject> objects = context.getObjects();
        Collection<Property> properties = context.getProperties();
        stream.print("\t");
        for (Iterator<Property> iterator = properties.iterator(); iterator.hasNext();) {
            Property property = iterator.next();
            stream.print(property.getName() + "\t");
        }
        stream.println();
        stream.print("\t");
        for (Iterator<Property> iterator = properties.iterator(); iterator.hasNext();) {
            Property property = iterator.next();
            stream.print(property.getType().getName() + "\t");
        }
        stream.println();
        stream.print("\t");
        for (Iterator<Property> iterator = properties.iterator(); iterator.hasNext();) {
            Property property = iterator.next();
            PropertyType type = property.getType();
            if (type instanceof TextualType) {
                stream.print("T\t");
            } else {
                // NumericalType numtype = (NumericalType) type;
                stream.print("N\t"); /// @todo handle digits (lost while parsing XML)
            }
        }
        stream.println();
        for (Iterator<CernatoObject> it1 = objects.iterator(); it1.hasNext();) {
            CernatoObject object = it1.next();
            stream.print(object.getName() + "\t");
            for (Iterator<Property> it2 = properties.iterator(); it2.hasNext();) {
                Property property = it2.next();
                stream.print(context.getRelationship(object, property).getDisplayString() + "\t");
            }
            stream.println();
        }
    }

    public static final void dump(CernatoModel model, View view, PrintStream stream) {
        stream.println(view.getName());
        for (int i = 0; i < view.getName().length(); i++) {
            stream.print("=");
        }
        stream.println();

        ViewContext context = new ViewContext(model.getContext(), view);
        Set<CernatoObject> objects = context.getObjects();
        Set<Criterion> attributes = context.getAttributes();
        BinaryRelation<CernatoObject, Criterion> relation = context.getRelation();

        stream.print("\t");
        for (Iterator<Criterion> iterator = attributes.iterator(); iterator.hasNext();) {
            Criterion criterion = iterator.next();
            stream.print(criterion.getDisplayString() + "\t");
        }
        stream.println();
        for (Iterator<CernatoObject> it1 = objects.iterator(); it1.hasNext();) {
            CernatoObject object = it1.next();
            stream.print(object.getName() + "\t");
            for (Iterator<Criterion> it2 = attributes.iterator(); it2.hasNext();) {
                Criterion criterion = it2.next();
                if (relation.contains(object, criterion)) {
                    stream.print("X");
                }
                stream.print("\t");
            }
            stream.println();
        }
    }
}
