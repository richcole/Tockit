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
import java.util.List;

public class LineDiagram extends ConscriptStructure{
	private TypedSize unitLength;
	private List points = new ArrayList();
	private List lines = new ArrayList();
	private List objects = new ArrayList();
	private List attributes = new ArrayList();
	private List concepts = new ArrayList();
	
	public LineDiagram(String identifier) {
        super(identifier);
	}

	public List getAttributes() {
		return Collections.unmodifiableList(this.attributes);
	}

	public List getConcepts() {
		return Collections.unmodifiableList(this.concepts);
	}

	public List getLines() {
		return Collections.unmodifiableList(this.lines);
	}

	public List getObjects() {
		return Collections.unmodifiableList(this.objects);
	}

	public List getPoints() {
		return Collections.unmodifiableList(this.points);
	}

	public TypedSize getUnitLength() {
		return this.unitLength;
	}

	public void addAttribute(FCAAttribute attribute) {
		this.attributes.add(attribute);
	}

	public void addConcept(Concept concept) {
		this.concepts.add(concept);
	}

	public void addLine(Line line) {
		this.lines.add(line);
	}

	public void addObject(FCAObject object) {
		this.objects.add(object);
	}

	public void addPoint(Point point) {
		this.points.add(point);
	}

	public void setUnitLength(TypedSize unitLength) {
		this.unitLength = unitLength;
	}

    public void printCSC(PrintStream stream) {
        printTitleRemarkSpecials(stream);
        stream.println("\t\tTODO");
        stream.println("\t\tTODO");
        stream.println("\t\tTODO");
    }

}