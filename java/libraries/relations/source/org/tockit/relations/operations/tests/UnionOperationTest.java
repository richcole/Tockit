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
import org.tockit.relations.operations.RelationOperation;
import org.tockit.relations.operations.UnionOperation;


public class UnionOperationTest extends AbstractRelationOperationTest {
    public UnionOperationTest(String s) {
        super(s);
    }

	public static Test suite() {
		return new TestSuite(UnionOperationTest.class);
	}

    protected RelationOperation getOperation() {
        return new UnionOperation();
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
    	
		RelationTestSetup two = new RelationTestSetup();
		Relation testRelTwo = new RelationImplementation(3);
		testRelTwo.addTuple(new String[]{"a","b","c"});
		testRelTwo.addTuple(new String[]{"b","b","c"});
		testRelTwo.addTuple(new String[]{"a","b","b"});

		one.input = new Relation[]{testRelOne, testRelTwo};
		one.expectedOutputArity = testRelOne.getArity();
		one.expectedOutputSize = testRelOne.getSize() + testRelTwo.getSize();
		one.expectedTuples = new Object[][]{new String[]{"1","6","1"}, new String[]{"b","b","c"}};
    	
        return new RelationTestSetup[]{one};
    }
}
