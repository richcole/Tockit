/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.net.URL;
import java.util.Hashtable;

public class LineDiagram extends SchemaPart{
	private TypedSize unitLength;
	private Point[] points;
	private Line[] lines;
	private FCAObject[] objects;
	private FCAAttribute[] attributes;
	private Concept[] concepts;
	
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Hashtable specials;
	
	public LineDiagram(URL file, String identifier, FormattedString title,
						String remark, Hashtable specials, TypedSize unitLength,
						Point[] points, Line[] lines, FCAObject[] objects,
						FCAAttribute[] attributes, Concept[] concepts) {
		this.file = file;
		this.identifier = identifier;
		this.title = title;
		this.remark = remark;
		this.specials = specials;
		this.unitLength = unitLength;
		this.points = points;
		this.lines = lines;
		this.objects = objects;
		this.attributes = attributes;
		this.concepts = concepts;		
	}

	public FCAAttribute[] getAttributes() {
		return attributes;
	}

	public Concept[] getConcepts() {
		return concepts;
	}

	public URL getFile() {
		return file;
	}

	public String getIdentifier() {
		return identifier;
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

	public String getRemark() {
		return remark;
	}

	public Hashtable getSpecials() {
		return specials;
	}

	public FormattedString getTitle() {
		return title;
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

	public void setFile(URL file) {
		this.file = file;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setSpecials(Hashtable specials) {
		this.specials = specials;
	}

	public void setTitle(FormattedString title) {
		this.title = title;
	}

	public void setUnitLength(TypedSize unitLength) {
		this.unitLength = unitLength;
	}

}