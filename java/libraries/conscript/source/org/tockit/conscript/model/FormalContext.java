/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.tockit.context.model.BinaryRelation;
import org.tockit.context.model.BinaryRelationImplementation;

public class FormalContext extends ConscriptStructure{
    private List<FCAObject> objects = new ArrayList<FCAObject>();
	private List<FCAAttribute> attributes = new ArrayList<FCAAttribute>();
	private BinaryRelationImplementation<FCAObject, FCAAttribute> relation = 
					new BinaryRelationImplementation<FCAObject, FCAAttribute>();

	public FormalContext(String identifier) {
        super(identifier);
    }

	public List<FCAAttribute> getAttributes() {
		return Collections.unmodifiableList(attributes);
	}

	public List<FCAObject> getObjects() {
		return Collections.unmodifiableList(objects);
	}

	public BinaryRelation<FCAObject, FCAAttribute> getRelation() {
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

    @Override
	public void printCSC(PrintStream stream) {
        printTitleRemarkSpecials(stream);
        stream.println("\tOBJECTS");
        for (Iterator<FCAObject> iter = this.objects.iterator(); iter.hasNext();) {
            FCAObject object = iter.next();
            stream.println("\t\t" + object.getPoint() + " " + object.getIdentifier() + " " + 
                           object.getDescription());
        }
        stream.println("\tATTRIBUTES");
        for (Iterator<FCAAttribute> iter = this.attributes.iterator(); iter.hasNext();) {
            FCAAttribute attribute = iter.next();
            stream.println("\t\t" + attribute.getPoint() + " " + attribute.getIdentifier() + " " + 
                           attribute.getDescription());
        }
        stream.println("\tRELATION");
        stream.println("\t\t" + this.objects.size() + " " + this.attributes.size());
        for (Iterator<FCAObject> it1 = this.objects.iterator(); it1.hasNext();) {
            FCAObject object = it1.next();
            stream.print("\t\t");
            for (Iterator<FCAAttribute> it2 = this.attributes.iterator(); it2.hasNext();) {
                FCAAttribute attribute = it2.next();
                if(this.relation.contains(object, attribute)) {
                    stream.print("*");
                } else {
                    stream.print(".");
                }
            }
            stream.println();
        }
    }
}