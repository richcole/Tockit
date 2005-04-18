/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations.tests;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.tockit.relations.model.Relation;
import org.tockit.relations.model.tests.RelationImplementationTest;
import org.tockit.relations.model.tests.RelationTest;
import org.tockit.relations.operations.NegationOperation;
import org.tockit.relations.operations.RelationOperation;


public class NegationOperationTest extends AbstractRelationOperationTest {
    public NegationOperationTest(String s) {
        super(s);
    }

	public static Test suite() {
		return new TestSuite(NegationOperationTest.class);
	}

    protected RelationOperation getOperation() {
    	Set domain = new HashSet();
		domain.add("1");
		domain.add("2");
		domain.add("3");
		domain.add("4");
		domain.add("5");
		domain.add("6");
		domain.add("7");
        return new NegationOperation(new Set[]{domain, domain, domain});
    }

    protected int getExpectedArity() {
        return 1;
    }
    
    protected RelationTestSetup[] getTests() {
    	RelationImplementationTest testCases = new RelationImplementationTest("test cases");
    	testCases.setUp();
    	
    	RelationTestSetup one = new RelationTestSetup();
    	Relation testRelOne = RelationTest.stringRelation;
        one.input = new Relation[]{testRelOne};
    	one.expectedOutputArity = testRelOne.getArity();
    	one.expectedOutputSize = 7 * 7 * 7 - testRelOne.getSize();
		one.expectedTuples = new Object[][]{new String[]{"6","1","6"}};
		one.unexpectedTuples = new Object[][]{new String[]{"1","6","1"}};
    	
        return new RelationTestSetup[]{one};
    }
}
