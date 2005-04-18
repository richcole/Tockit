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
import org.tockit.relations.model.tests.RelationImplementationTest;
import org.tockit.relations.model.tests.RelationTest;
import org.tockit.relations.operations.CrossproductOperation;
import org.tockit.relations.operations.RelationOperation;


public class CrossproductOperationTest extends AbstractRelationOperationTest {
    public CrossproductOperationTest(String s) {
        super(s);
    }

	public static Test suite() {
		return new TestSuite(CrossproductOperationTest.class);
	}

    protected RelationOperation getOperation() {
        return new CrossproductOperation();
    }

    protected int getExpectedArity() {
        return 2;
    }

    protected RelationTestSetup[] getTests() {
    	RelationImplementationTest testCases = new RelationImplementationTest("test cases");
    	testCases.setUp();
    	
		RelationTestSetup one = new RelationTestSetup();
		Relation testRelOne = RelationTest.stringRelation;
		one.input = new Relation[]{testRelOne, testRelOne};
		one.expectedOutputArity = testRelOne.getArity() * 2;
		one.expectedOutputSize = testRelOne.getSize() * testRelOne.getSize();
		one.expectedTuples = new Object[][]{new String[]{"1","6","1","1","6","1"}};
		one.unexpectedTuples = new Object[][]{new String[]{"1","6","1","1","2","7"}};
    	
        return new RelationTestSetup[]{one};
    }
}
