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

public class LineDiagram extends ConscriptStructure{
	private TypedSize unitLength;
	private List<Point> points = new ArrayList<Point>();
	private List<Line> lines = new ArrayList<Line>();
	private List<FCAObject> objects = new ArrayList<FCAObject>();
	private List<FCAAttribute> attributes = new ArrayList<FCAAttribute>();
	private List<Concept> concepts = new ArrayList<Concept>();
	
	public LineDiagram(String identifier) {
        super(identifier);
	}

	public List<FCAAttribute> getAttributes() {
		return Collections.unmodifiableList(this.attributes);
	}

	public List<Concept> getConcepts() {
		return Collections.unmodifiableList(this.concepts);
	}

	public List<Line> getLines() {
		return Collections.unmodifiableList(this.lines);
	}

	public List<FCAObject> getObjects() {
		return Collections.unmodifiableList(this.objects);
	}

	public List<Point> getPoints() {
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
        if(this.unitLength != null) {
            stream.println("\t\tUNITLENGTH " + this.unitLength);
        }
        stream.println("\t\tPOINTS");
        for (Iterator<Point> iter = this.points.iterator(); iter.hasNext();) {
            Point point = iter.next();
            stream.println("\t\t\t" + point.toString());
        }
        stream.println("\t\tLINES");
        for (Iterator<Line> iter = this.lines.iterator(); iter.hasNext();) {
            Line line = iter.next();
            stream.println("\t\t\t" + line.toString());
        }
        stream.println("\t\tOBJECTS");
        for (Iterator<FCAObject> iter = this.objects.iterator(); iter.hasNext();) {
            FCAObject object = iter.next();
            stream.println("\t\t\t" + object.toString());
        }
        stream.println("\t\tATTRIBUTES");
        for (Iterator<FCAAttribute> iter = this.attributes.iterator(); iter.hasNext();) {
            FCAAttribute attribute = iter.next();
            stream.println("\t\t\t" + attribute.toString());
        }
        stream.println("\t\tCONCEPTS");
        for (Iterator<Concept> iter = this.concepts.iterator(); iter.hasNext();) {
            Concept concept = iter.next();
            stream.println("\t\t\t" + concept.toString());
        }
        stream.println("\t\t;");
    }

}