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

    public void printCSC(PrintStream stream) {
        printIdentifierLine(stream);
        stream.println("\tOBJECTS");
        for (Iterator iter = this.objects.iterator(); iter.hasNext();) {
            FCAObject object = (FCAObject) iter.next();
            stream.println("\t\t" + object.getPoint() + " " + object.getIdentifier() + " " + 
                           object.getDescription());
        }
        stream.println("\tATTRIBUTES");
        for (Iterator iter = this.attributes.iterator(); iter.hasNext();) {
            FCAAttribute attribute = (FCAAttribute) iter.next();
            stream.println("\t\t" + attribute.getPoint() + " " + attribute.getIdentifier() + " " + 
                           attribute.getDescription());
        }
        stream.println("\tRELATION");
        stream.println("\t\t" + this.objects.size() + " " + this.attributes.size());
        for (Iterator it1 = this.objects.iterator(); it1.hasNext();) {
            FCAObject object = (FCAObject) it1.next();
            stream.print("\t\t");
            for (Iterator it2 = this.attributes.iterator(); it2.hasNext();) {
                FCAAttribute attribute = (FCAAttribute) it2.next();
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