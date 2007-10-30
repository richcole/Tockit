/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.model.tests;

import org.tockit.relations.model.Tuple;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TupleTest extends TestCase {
    public TupleTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(TupleTest.class);
    }

    public void testTupleEquality() {
        Tuple<Object> one = new Tuple<Object>(new Object[]{});    
        Tuple<Object> two = new Tuple<Object>(new Object[]{new Object(), "string", new Integer(5)});
        Object object = new Object();    
        Tuple<Object> three = new Tuple<Object>(new Object[]{object, "string", new Integer(5)});    
        Tuple<Object> four = new Tuple<Object>(new Object[]{object, "string", new Integer(5)});
        
        assertTrue(one.equals(one));    
        assertTrue(!one.equals(two));    
        assertTrue(!one.equals(three));    
        assertTrue(!one.equals(four));    

        assertTrue(!two.equals(one));    
        assertTrue(two.equals(two));    
        assertTrue(!two.equals(three));    
        assertTrue(!two.equals(four));    

        assertTrue(!three.equals(one));    
        assertTrue(!three.equals(two));    
        assertTrue(three.equals(three));    
        assertTrue(three.equals(four));    

        assertTrue(!four.equals(one));    
        assertTrue(!four.equals(two));    
        assertTrue(four.equals(three));    
        assertTrue(four.equals(four));    
    }
}
