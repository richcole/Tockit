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
import org.tockit.relations.operations.JoinOperation;
import org.tockit.relations.operations.RelationOperation;


public class JoinOperationTest extends AbstractRelationOperationTest {
    public JoinOperationTest(String s) {
        super(s);
    }

	public static Test suite() {
		return new TestSuite(JoinOperationTest.class);
	}

    protected RelationOperation getOperation() {
        return new JoinOperation(new int[]{2}, true, new int[]{1}, false);
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
		one.expectedOutputArity = 5;
		one.expectedOutputSize = 7;
		one.expectedTuples = new Object[][]{new String[]{"1","6","2","1","2"}};
		one.unexpectedTuples = new Object[][]{new String[]{"1","6","1","1","2"}};
    	
        return new RelationTestSetup[]{one};
    }
}
