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
import org.tockit.relations.model.RelationImplementation;
import org.tockit.relations.model.tests.RelationImplementationTest;
import org.tockit.relations.operations.IntersectionOperation;
import org.tockit.relations.operations.RelationOperation;


public class IntersectionOperationTest extends AbstractRelationOperationTest {
    public IntersectionOperationTest(String s) {
        super(s);
    }

	public static Test suite() {
		return new TestSuite(IntersectionOperationTest.class);
	}

    protected RelationOperation getOperation() {
        return new IntersectionOperation();
    }

    protected int getExpectedArity() {
        return 2;
    }

    protected RelationTestSetup[] getTests() {
    	RelationImplementationTest testCases = new RelationImplementationTest("test cases");
    	testCases.setUp();
    	
		RelationTestSetup one = new RelationTestSetup();
		Relation testRelOne = RelationImplementationTest.stringRelation;
		one.input = new Relation[]{testRelOne, testRelOne};
		one.expectedOutputArity = testRelOne.getArity();
		one.expectedOutputSize = testRelOne.getSize();
		one.expectedTuples = new Object[][]{new String[]{"1","6","1"}};
		one.unexpectedTuples = new Object[][]{new String[]{"1","6","6"}};
    	
		Relation testRelTwo = new RelationImplementation(3);
		testRelTwo.addTuple(new String[]{"1","6","1"});
		testRelTwo.addTuple(new String[]{"b","b","c"});
		testRelTwo.addTuple(new String[]{"a","b","b"});

		one.input = new Relation[]{testRelOne, testRelTwo};
		one.expectedOutputArity = testRelOne.getArity();
		one.expectedOutputSize = 1;
		one.expectedTuples = new Object[][]{new String[]{"1","6","1"}};
		one.unexpectedTuples = new Object[][]{new String[]{"1","6","6"}, new String[]{"b","b","c"}, new String[]{"5","4","3"}};
    	
        return new RelationTestSetup[]{one};
    }
}
