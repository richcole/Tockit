/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.cernatoXML.model.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Collection;
import java.util.Iterator;


import ord.tockit.context.model.FCAElement;
import ord.tockit.context.model.FCAElementImplementation;

import org.tockit.cernatoXML.model.*;

public class ViewContextTest extends TestCase {
    final static Class THIS = ViewContextTest.class;

    public ViewContextTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testScaling() {
        checkView(TestData.View1);
        checkView(TestData.View2);
        checkView(TestData.View3);
    }

    private void checkView(View view) {
        ViewContext scaledContext = new ViewContext(TestData.Model, view);
        Collection objects = scaledContext.getObjects();
        Collection attributes = scaledContext.getAttributes();
        for (Iterator it1 = objects.iterator(); it1.hasNext();) {
            CernatoObject object = (CernatoObject) it1.next();
            // @todo re-enable once core FCA is in Tockit
            for (Iterator it2 = attributes.iterator(); it2.hasNext();) {
                FCAElement attribute = (FCAElement) it2.next();
                Criterion criterion = (Criterion) attribute.getData();
                assertEquals(object.getName() + " x " + criterion.getProperty().getName() + ":" + criterion.getValueGroup().getName(),
                        TestData.isInScaledRelation(object, criterion),
                        scaledContext.getRelation().contains(object, new FCAElementImplementation(criterion, null)));
            }
        }
    }
}
