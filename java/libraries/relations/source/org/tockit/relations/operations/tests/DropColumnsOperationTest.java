/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.tockit.relations.model.Relation;
import org.tockit.relations.model.Tuple;
import org.tockit.relations.model.tests.RelationImplementationTest;
import org.tockit.relations.model.tests.RelationTest;
import org.tockit.relations.operations.DropColumnsOperation;
import org.tockit.relations.operations.RelationOperation;


public class DropColumnsOperationTest extends AbstractRelationOperationTest {
    public DropColumnsOperationTest(String s) {
        super(s);
    }

	public static Test suite() {
		return new TestSuite(DropColumnsOperationTest.class);
	}

    @Override
	protected RelationOperation getOperation() {
        return new DropColumnsOperation(new int[]{1,3});
    }

    @Override
	protected int getExpectedArity() {
        return 1;
    }

    @Override
	protected RelationTestSetup[] getTests() {
    	RelationImplementationTest testCases = new RelationImplementationTest("test cases");
    	testCases.setUp();
    	
    	RelationTestSetup one = new RelationTestSetup();
    	Relation<Object> testRelOne = RelationTest.objectRelation;
        one.input = new Relation[]{testRelOne};
    	one.expectedOutputArity = 3; // we dropped 2 out of 5
    	one.expectedOutputSize = 4; // 2 out of the 5 are the same after projection
    	Tuple<Object> first = testRelOne.getTuples().iterator().next();
		one.expectedTuples = new Object[][]{new Object[]{first.getElement(0), first.getElement(2), first.getElement(4)}};
    	
        return new RelationTestSetup[]{one};
    }
}
