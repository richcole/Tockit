/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
    public AllTests(String s) {
        super(s);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
		suite.addTest(org.tockit.relations.operations.tests.IdentityOperationTest.suite());
		suite.addTest(org.tockit.relations.operations.tests.PickColumnsOperationTest.suite());
		suite.addTest(org.tockit.relations.operations.tests.DropColumnsOperationTest.suite());
		suite.addTest(org.tockit.relations.operations.tests.NegationOperationTest.suite());
		suite.addTest(org.tockit.relations.operations.tests.UnionOperationTest.suite());
		suite.addTest(org.tockit.relations.operations.tests.IntersectionOperationTest.suite());
		suite.addTest(org.tockit.relations.operations.tests.SelectionOperationValueSelectTest.suite());
		suite.addTest(org.tockit.relations.operations.tests.SelectionOperationColumnCompareTest.suite());
		suite.addTest(org.tockit.relations.operations.tests.JoinOperationTest.suite());
		suite.addTest(org.tockit.relations.operations.tests.CrossproductOperationTest.suite());
		suite.addTest(org.tockit.relations.operations.tests.PermutationOperationTest.suite());
        return suite;
    }
}
