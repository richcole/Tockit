/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class LineDiagram extends SchemaPart{
	private TypedSize unitLength;
	private Point[] points;
	private Line[] lines;
	private FCAObject[] objects;
	private FCAAttribute[] attributes;
	private Concept[] concepts;
	
	public LineDiagram(CSCFile file, String identifier) {
        super(file, identifier);
	}

	public FCAAttribute[] getAttributes() {
		return attributes;
	}

	public Concept[] getConcepts() {
		return concepts;
	}

	public Line[] getLines() {
		return lines;
	}

	public FCAObject[] getObjects() {
		return objects;
	}

	public Point[] getPoints() {
		return points;
	}

	public TypedSize getUnitLength() {
		return unitLength;
	}

	public void setAttributes(FCAAttribute[] attributes) {
		this.attributes = attributes;
	}

	public void setConcepts(Concept[] concepts) {
		this.concepts = concepts;
	}

	public void setLines(Line[] lines) {
		this.lines = lines;
	}

	public void setObjects(FCAObject[] objects) {
		this.objects = objects;
	}

	public void setPoints(Point[] points) {
		this.points = points;
	}

	public void setUnitLength(TypedSize unitLength) {
		this.unitLength = unitLength;
	}

}