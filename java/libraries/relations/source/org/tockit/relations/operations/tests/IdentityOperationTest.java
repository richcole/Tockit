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
import org.tockit.relations.operations.IdentityOperation;
import org.tockit.relations.operations.RelationOperation;


public class IdentityOperationTest extends AbstractRelationOperationTest {
    public IdentityOperationTest(String s) {
        super(s);
    }

	public static Test suite() {
		return new TestSuite(IdentityOperationTest.class);
	}

    @Override
	protected RelationOperation<Object> getOperation() {
        return new IdentityOperation<Object>();
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
        one.input = new Relation[]{testRelOne};
    	one.expectedOutputArity = testRelOne.getArity();
    	one.expectedOutputSize = testRelOne.getSize();
    	Tuple<? extends Object> firstInputTuple = testRelOne.getTuples().iterator().next();
        one.expectedTuples = new Object[][]{firstInputTuple.getData()};
    	
        return new RelationTestSetup[]{one};
    }
}
