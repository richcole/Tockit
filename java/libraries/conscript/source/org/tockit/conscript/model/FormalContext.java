/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class FormalContext extends SchemaPart{
    private FCAObject[] objects;
	private FCAAttribute[] attributes;
	private BinaryRelation relation;

	public FormalContext(ConceptualFile file, String identifier) {
        super(file, identifier);
    }

	public FCAAttribute[] getAttributes() {
		return attributes;
	}

	public FCAObject[] getObjects() {
		return objects;
	}

	public BinaryRelation getRelation() {
		return relation;
	}

	public void setAttributes(FCAAttribute[] attributes) {
		this.attributes = attributes;
	}

	public void setObjects(FCAObject[] objects) {
		this.objects = objects;
	}

	public void setRelation(BinaryRelation relation) {
		this.relation = relation;
	}
}