/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class LineFormat {
	public static final class LineStyle{
		protected LineStyle() { };
	}
	public static final LineStyle SOLID = new LineStyle();
	public static final LineStyle DASHED = new LineStyle();
	public static final LineStyle DOTTED = new LineStyle();
	
	private LineStyle style;
	private TypedSize width;
	private String colorName;
	
	public LineFormat(LineStyle style, TypedSize width, String colorName) {
		this.style = style;
		this.width = width;
		this.colorName = colorName;
	}

	public String getColorName() {
		return colorName;
	}

	public LineStyle getStyle() {
		return style;
	}

	public TypedSize getWidth() {
		return width;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public void setStyle(LineStyle style) {
		this.style = style;
	}

	public void setWidth(TypedSize width) {
		this.width = width;
	}
}