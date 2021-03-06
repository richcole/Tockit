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
import org.tockit.relations.operations.RelationOperation;
import org.tockit.relations.operations.SelectionOperation;


public class SelectionOperationColumnCompareTest extends AbstractRelationOperationTest {
    public SelectionOperationColumnCompareTest(String s) {
        super(s);
    }

	public static Test suite() {
		return new TestSuite(SelectionOperationColumnCompareTest.class);
	}

    @Override
	protected RelationOperation<Object> getOperation() {
        return SelectionOperation.getColumnEqualitySelect(1,2);
    }

    @Override
	protected int getExpectedArity() {
        return 1;
    }

    @SuppressWarnings("unchecked")
	@Override
	protected RelationTestSetup[] getTests() {
    	RelationImplementationTest testCases = new RelationImplementationTest("test cases");
    	testCases.setUp();
    	
    	RelationTestSetup one = new RelationTestSetup();
    	Relation<Object> testRelOne = RelationTest.stringRelation;
    	testRelOne.addTuple(new String[]{"6","6","6"});
        one.input = new Relation[]{testRelOne};
    	one.expectedOutputArity = testRelOne.getArity();
    	one.expectedOutputSize = 1;
		one.expectedTuples = new Object[][]{new String[]{"6","6","6"}};
		one.unexpectedTuples = new Object[][]{new String[]{"6","5","4"}};
    	
        return new RelationTestSetup[]{one};
    }
}
