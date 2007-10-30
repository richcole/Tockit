/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.operations.tests;

import java.util.Iterator;

import org.tockit.relations.model.Relation;
import org.tockit.relations.model.RelationImplementation;
import org.tockit.relations.model.Tuple;
import org.tockit.relations.operations.RelationOperation;

import junit.framework.TestCase;


public abstract class AbstractRelationOperationTest extends TestCase {
	protected static class RelationTestSetup {
		public Relation[] input;
		public int expectedOutputArity;
		public int expectedOutputSize;
		public Object[][] expectedTuples;
		public Object[][] unexpectedTuples;
	}
	
	public AbstractRelationOperationTest(String s) {
		super(s);
	}
	
	public void testBaseFeatures() {
		RelationOperation op = getOperation();
        assertEquals("Operation doesn't match the arity expected.", getExpectedArity(), op.getArity());
		assertNotNull("Operation name must not be null", op.getName());
		assertTrue("Operation name must not be empty", op.getName().length() != 0);
		assertNotNull("Operation must provide parameter name array", op.getParameterNames());
		assertEquals("Length of parameter names array must be the operation's arity", op.getArity(), op.getParameterNames().length);
		try {
			// the following numbers are arbitrary postitive numbers
			Relation test = new RelationImplementation(4);
			Relation[] input = new Relation[op.getArity() + 3];
			for (int i = 0; i < input.length; i++) {
				input[i] = test;
			}
			op.apply(input);
			fail("Operation failed to throw exception for illegal argument (too big)");
		} catch (IllegalArgumentException e) {
			// ignore
		}
		try {
			Relation[] input = new Relation[0];
			op.apply(input);
			fail("Operation failed to throw exception for illegal argument (empty array)");
		} catch (IllegalArgumentException e) {
			// ignore
		}
	}
	
	public void testOperations() {
		RelationTestSetup[] tests = getTests();
		RelationOperation op = getOperation();
		for (int i = 0; i < tests.length; i++) {
            RelationTestSetup test = tests[i];
			Relation result = op.apply(test.input);
			if(logResults()) {
				for (Iterator<Tuple> iter = result.getTuples().iterator(); iter.hasNext();) {
	                Tuple tuple = iter.next();
	                System.out.println(tuple);
	            }			
			}
			assertEquals("Result of operation has wrong arity", test.expectedOutputArity, result.getArity());            
			assertEquals("Result of operation has wrong size", test.expectedOutputSize, result.getSize());
			if(test.expectedTuples != null) {
				for (int j = 0; j < test.expectedTuples.length; j++) {
					Object[] tuple = test.expectedTuples[j];
					assertTrue("Expected tuple not found in result", result.isRelated(tuple));                
				}          
			}
			if(test.unexpectedTuples != null) {
				for (int j = 0; j < test.unexpectedTuples.length; j++) {
					Object[] tuple = test.unexpectedTuples[j];
					assertTrue("Unexpected tuple found in result", !result.isRelated(tuple));                
				}          
			}
        }
	}
	
	/**
	 * Override to return true if logging is needed.
	 * 
	 * Here is the copy and paste bit:
	 *   protected boolean logResults() { return true; }
	 */
	protected boolean logResults() {
		return false;
	}
	
	protected abstract RelationOperation getOperation();
	
	protected abstract int getExpectedArity();
	
	protected abstract RelationTestSetup[] getTests();
}
