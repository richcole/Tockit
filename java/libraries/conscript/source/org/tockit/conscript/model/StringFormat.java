/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

public class StringFormat {

	public static final class HorizontalAlign {
		protected HorizontalAlign() {}
	}
	public static final class VerticalAlign {
		protected VerticalAlign(){}
	}
	public static final class FontStyle{
		protected FontStyle() {}
	}
	
	public static final HorizontalAlign LEFT = new HorizontalAlign();
	public static final HorizontalAlign H_CENTER= new HorizontalAlign();
	public static final HorizontalAlign RIGHT= new HorizontalAlign();

	public static final VerticalAlign BOTTOM = new VerticalAlign();
	public static final VerticalAlign V_CENTER= new VerticalAlign();
	public static final VerticalAlign TOP= new VerticalAlign();

	public static final FontStyle BOLD = new FontStyle();
	public static final FontStyle CURSIVE = new FontStyle();
	public static final FontStyle OUTLINED = new FontStyle();
	public static final FontStyle SHADOWED = new FontStyle();
	public static final FontStyle UNDERLINED = new FontStyle();

	private String fontFamily;
	private FontStyle fontStyle;
	private String colorName;
	private TypedSize fontSize;
	private Point2D offset;
	private HorizontalAlign horizontalAlign;
	private VerticalAlign verticalAlign;
	private Rectangle clipbox;	
	
	public StringFormat(String formatCode){
        // @todo add parse code here
	}

	public Rectangle getClipbox() {
		return clipbox;
	}

	public String getColorName() {
		return colorName;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public TypedSize getFontSize() {
		return fontSize;
	}

	public FontStyle getFontStyle() {
		return fontStyle;
	}

	public HorizontalAlign getHorizontalAlign() {
		return horizontalAlign;
	}

	public Point2D getOffset() {
		return offset;
	}

	public VerticalAlign getVerticalAlign() {
		return verticalAlign;
	}

	public void setClipbox(Rectangle clipbox) {
		this.clipbox = clipbox;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public void setFontSize(TypedSize fontSize) {
		this.fontSize = fontSize;
	}

	public void setFontStyle(FontStyle fontStyle) {
		this.fontStyle = fontStyle;
	}

	public void setHorizontalAlign(HorizontalAlign horizontalAlign) {
		this.horizontalAlign = horizontalAlign;
	}

	public void setOffset(Point2D offset) {
		this.offset = offset;
	}

	public void setVerticalAlign(VerticalAlign verticalAlign) {
		this.verticalAlign = verticalAlign;
	}

}