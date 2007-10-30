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
import org.tockit.relations.model.Tuple;
import org.tockit.relations.operations.PermutationOperation;
import org.tockit.relations.operations.RelationOperation;


public class PermutationOperationTest extends AbstractRelationOperationTest {
    public PermutationOperationTest(String s) {
        super(s);
    }

	public static Test suite() {
		return new TestSuite(PermutationOperationTest.class);
	}

    @Override
	protected RelationOperation<Object> getOperation() {
        return new PermutationOperation<Object>(new int[]{1,3,5});
    }

    @Override
	protected int getExpectedArity() {
        return 1;
    }

    @SuppressWarnings("unchecked")
	@Override
	protected RelationTestSetup[] getTests() {
		RelationImplementation<Object> testRelOne = new RelationImplementation<Object>(6);
		testRelOne.addTuple(new Tuple<Object>(new String[]{"a","b","c","d","e","f"}));
		testRelOne.addTuple(new Tuple<Object>(new String[]{"b","c","d","e","f","a"}));
		testRelOne.addTuple(new Tuple<Object>(new String[]{"a","b","d","e","f","c"}));
		testRelOne.addTuple(new Tuple<Object>(new String[]{"f","b","c","d","e","a"}));

		RelationTestSetup one = new RelationTestSetup();
        one.input = new Relation[]{testRelOne};
    	one.expectedOutputArity = testRelOne.getArity();
    	one.expectedOutputSize = testRelOne.getSize();
		one.expectedTuples = new Object[][]{new String[]{"a","d","c","f","e","b"},
											new String[]{"a","e","d","c","f","b"}};
		one.unexpectedTuples = new Object[][]{new String[]{"a","b","c","d","e","f"}};
    	
        return new RelationTestSetup[]{one};
    }
}
