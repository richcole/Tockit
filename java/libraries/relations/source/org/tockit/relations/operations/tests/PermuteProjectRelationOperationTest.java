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
import org.tockit.relations.operations.PermuteProjectRelationOperation;
import org.tockit.relations.operations.RelationOperation;


public class PermuteProjectRelationOperationTest extends AbstractRelationOperationTest {
    public PermuteProjectRelationOperationTest(String s) {
        super(s);
    }

	public static Test suite() {
		return new TestSuite(PermuteProjectRelationOperationTest.class);
	}

    protected RelationOperation getOperation() {
        return new PermuteProjectRelationOperation(new int[]{1,2,1,2});
    }

    protected int getExpectedArity() {
        return 1;
    }

    protected RelationTestSetup[] getTests() {
    	RelationImplementationTest testCases = new RelationImplementationTest("test cases");
    	testCases.setUp();
    	
    	RelationTestSetup one = new RelationTestSetup();
    	Relation testRelOne = RelationImplementationTest.stringRelation;
        one.input = new Relation[]{testRelOne};
    	one.expectedOutputArity = 4;
    	one.expectedOutputSize = testRelOne.getSize();
    	Tuple first = (Tuple) testRelOne.getTuples().iterator().next();
		one.expectedTuples = new Object[][]{new String[]{"6","1","6","1"}};
		one.unexpectedTuples = new Object[][]{new String[]{"1","6","6","1"}};
    	
        return new RelationTestSetup[]{one};
    }
}
