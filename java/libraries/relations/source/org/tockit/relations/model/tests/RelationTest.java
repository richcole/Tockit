/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.model.tests;

import org.tockit.relations.model.Relation;
import org.tockit.relations.model.Tuple;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RelationTest extends TestCase {
    public static Relation stringRelation;
    public static Relation objectRelation;
    public static Relation mixedRelation;
    public static Relation unnamedRelation;
    
    public RelationTest(String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(RelationTest.class);
    }
    
    public void testSetup() {
        assertEquals("stringRelation has wrong arity", 3, stringRelation.getArity());
        assertEquals("objectRelation has wrong arity", 5, objectRelation.getArity());
        assertEquals("mixedRelation has wrong arity", 2, mixedRelation.getArity());
        assertEquals("unnamedRelation has wrong arity", 7, unnamedRelation.getArity());
    }

    public void setUp() {
        Object o1 = new Object();
        Object o2 = new Object();
        Object o3 = new Object();
        Object o4 = new Object();
        Object o5 = new Object();
        Object o6 = new Object();
        Object o7 = new Object();
        
        stringRelation = new Relation(new String[]{"eins", "zwei", "drei"});
        stringRelation.addTuple(new Tuple(new String[]{"1","6","1"}));
        stringRelation.addTuple(new Tuple(new String[]{"2","1","2"}));
        stringRelation.addTuple(new Tuple(new String[]{"3","2","1"}));
        stringRelation.addTuple(new Tuple(new String[]{"4","3","2"}));
        stringRelation.addTuple(new Tuple(new String[]{"5","4","3"}));
        stringRelation.addTuple(new Tuple(new String[]{"6","5","4"}));
        stringRelation.addTuple(new Tuple(new String[]{"7","6","5"}));
        
        objectRelation = new Relation(new String[]{"one", "two", "three", "four", "five"});
        objectRelation.addTuple(new Tuple(new Object[]{o1,o2,o3,o4,o5}));
        objectRelation.addTuple(new Tuple(new Object[]{o2,o3,o4,o5,o2}));
        objectRelation.addTuple(new Tuple(new Object[]{o1,o3,o3,o6,o5}));
        objectRelation.addTuple(new Tuple(new Object[]{o1,o2,o1,o4,o5}));
        objectRelation.addTuple(new Tuple(new Object[]{o1,o2,o7,o1,o5}));

        mixedRelation = new Relation(new String[]{"object", "string"});
        mixedRelation.addTuple(new Tuple(new Object[]{o1,"1"}));
        mixedRelation.addTuple(new Tuple(new Object[]{o2,"2"}));
        mixedRelation.addTuple(new Tuple(new Object[]{o3,"3"}));
        mixedRelation.addTuple(new Tuple(new Object[]{o4,"4"}));
        mixedRelation.addTuple(new Tuple(new Object[]{o5,"5"}));
        mixedRelation.addTuple(new Tuple(new Object[]{o6,"6"}));
        mixedRelation.addTuple(new Tuple(new Object[]{o7,"7"}));
        
        unnamedRelation = new Relation(7);
        // the unnamed one gets 7 objects noone else has
        unnamedRelation.addTuple(new Tuple(new Object[]{
            new Object(), new Object(), new Object(), new Object(),
            new Object(), new Object(), new Object()
        }));
    }
}
