/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class PointFormat {
	
	public static final class FillStyle{
		protected FillStyle() { }
	}
	
	public static final FillStyle EMPTY = new FillStyle();
	public static final FillStyle FULL = new FillStyle();
	public static final FillStyle LOWER = new FillStyle();
	public static final FillStyle UPPER = new FillStyle();
	
	private TypedSize radius;
	private LineFormat outlineFormat;
	private FillStyle fillStyle;
	private String fillColorName;
	
	public PointFormat(TypedSize radius, LineFormat outlineFormat, FillStyle fillStyle, String fillColorName) {
		this.radius = radius;
		this.outlineFormat = outlineFormat;
		this.fillStyle = fillStyle;
		this.fillColorName = fillColorName;
	}

	public String getFillColorName() {
		return fillColorName;
	}

	public FillStyle getFillStyle() {
		return fillStyle;
	}

	public LineFormat getOutlineFormat() {
		return outlineFormat;
	}

	public TypedSize getRadius() {
		return radius;
	}

	public void setFillColorName(String fillColorName) {
		this.fillColorName = fillColorName;
	}

	public void setFillStyle(FillStyle fillStyle) {
		this.fillStyle = fillStyle;
	}

	public void setOutlineFormat(LineFormat outlineFormat) {
		this.outlineFormat = outlineFormat;
	}

	public void setRadius(TypedSize radius) {
		this.radius = radius;
	}

}