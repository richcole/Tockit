/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.relations;

import java.util.Iterator;
import java.util.Set;

import org.tockit.relations.model.Relation;
import org.tockit.relations.model.RelationImplementation;
import org.tockit.relations.operations.CrossproductOperation;
import org.tockit.relations.operations.IntersectionOperation;
import org.tockit.relations.operations.PermutationOperation;
import org.tockit.relations.operations.PickColumnsOperation;


public class BasicRelationalAlgebra {
	public static Relation getDelta(Set domain) {
		RelationImplementation result = new RelationImplementation(2);
		for (Iterator iter = domain.iterator(); iter.hasNext();) {
            Object cur = iter.next();
            result.addTuple(new Object[]{cur, cur});
        }
        return result;
	}
	
	public static Relation intersect(Relation left, Relation right) {
		return IntersectionOperation.intersect(left, right);
	}
	
	public static Relation crossproduct(Relation left, Relation right) {
		return CrossproductOperation.crossproduct(left, right);
	}
	
	public static Relation project(Relation input, int[] columns) {
		int lastCol = -1;
		for (int i = 0; i < columns.length; i++) {
            int col = columns[i];
			if(col < 0 || col >= input.getArity()) {
				throw new IllegalArgumentException("Column out of range");
			}
			if(col <= lastCol) {
				throw new IllegalArgumentException("Columns have to be given in increasing order");
			}
            lastCol = col;
        }
        return PickColumnsOperation.pickColumns(input, columns);
	}
	
	public static Relation permute(Relation input, int firstColumn, int secondColumn) {
		return PermutationOperation.exchange(input, firstColumn, secondColumn);
	}
}
