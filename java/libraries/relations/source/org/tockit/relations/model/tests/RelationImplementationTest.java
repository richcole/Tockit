/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations.model.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.tockit.relations.model.Relation;
import org.tockit.relations.model.RelationImplementation;


public class RelationImplementationTest extends RelationTest {
    public RelationImplementationTest(String s) {
        super(s);
    }

	public static Test suite() {
		return new TestSuite(RelationImplementationTest.class);
	}
    
    protected Relation createRelation(String[] dimensionNames) {
        return new RelationImplementation(dimensionNames);
    }

    protected Relation createRelation(int arity) {
        return new RelationImplementation(arity);
    }
}
