/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormalContext extends ConscriptStructure{
    private List objects = new ArrayList();
	private List attributes = new ArrayList();
	private BinaryRelationImplementation relation = new BinaryRelationImplementation();

	public FormalContext(String identifier) {
        super(identifier);
    }

	public List getAttributes() {
		return Collections.unmodifiableList(attributes);
	}

	public List getObjects() {
		return Collections.unmodifiableList(objects);
	}

	public BinaryRelation getRelation() {
		return relation;
	}

	public void addAttribute(FCAAttribute attribute) {
		this.attributes.add(attribute);
	}

	public void addObject(FCAObject object) {
		this.objects.add(object);
	}

    public void setRelationship(FCAObject object, FCAAttribute attribute) {
        this.relation.insert(object, attribute);
    }

    public void removeRelationship(FCAObject object, FCAAttribute attribute) {
        this.relation.remove(object, attribute);
    }
}