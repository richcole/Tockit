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
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;

import org.tockit.conscript.parser.CSCParser;

public class StringFormat {

	public static final class HorizontalAlign {
		protected HorizontalAlign() {}
        public static HorizontalAlign getAlign(char id) {
            switch(id) {
                case 'l':
                    return LEFT;
                case 'c':
                    return H_CENTER;
                case 'r':
                    return RIGHT;
                default:
                    throw new IllegalArgumentException("Unknown horizontal alignment '" + id +"'");
            }
        }
	}
	public static final class VerticalAlign {
		protected VerticalAlign(){}
        public static VerticalAlign getAlign(char id) {
            switch(id) {
                case 'b':
                    return BOTTOM;
                case 'c':
                    return V_CENTER;
                case 't':
                    return TOP;
                default:
                    throw new IllegalArgumentException("Unknown vertical alignment '" + id +"'");
            }
        }
	}
	public static final class FontStyle{
		protected FontStyle() {}
        public static FontStyle getFontStyle(char id) {
            switch(id) {
                case 'b':
                    return BOLD;
                case 'c':
                    return CURSIVE;
                case 'o':
                    return OUTLINED;
                case 's':
                    return SHADOWED;
                case 'u':
                    return UNDERLINED;
                default:
                    throw new IllegalArgumentException("Unknown font style '" + id +"'");
            }
        }
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
	private Rectangle2D clipbox;	
	
	public StringFormat(String formattingString){
        String[] nextSplit = extractFormattingStringSegment(formattingString);
        this.fontFamily = nextSplit[0];
        formattingString = nextSplit[1];
        
        nextSplit = extractFormattingStringSegment(formattingString);
        String fontStyleString = nextSplit[0];
        formattingString = nextSplit[1];
        this.fontStyle = getFontStyle(fontStyleString);
        
        nextSplit = extractFormattingStringSegment(formattingString);
        this.colorName = nextSplit[0];
        formattingString = nextSplit[1];

        nextSplit = extractFormattingStringSegment(formattingString);
        String fontSizeString = nextSplit[0];
        this.fontSize = new TypedSize(fontSizeString);
        formattingString = nextSplit[1];

        nextSplit = extractFormattingStringSegment(formattingString);
        String offsetString = nextSplit[0];
        if (offsetString != null && offsetString.length() != 0) {
            int commaPos = offsetString.indexOf(',');
            String xPart = offsetString.substring(0, commaPos);
            String yPart = offsetString.substring(commaPos + 1);
            double x = Double.parseDouble(xPart);
            double y = Double.parseDouble(yPart);
            this.offset = new Point2D.Double(x,y);
        }
        formattingString = nextSplit[1];

        nextSplit = extractFormattingStringSegment(formattingString);
        String alignmentString = nextSplit[0];
        this.horizontalAlign = getHorizontalAlign(alignmentString);
        this.verticalAlign = getVerticalAlign(alignmentString);
        formattingString = nextSplit[1];

        nextSplit = extractFormattingStringSegment(formattingString);
        String clipBoxString = nextSplit[0];
        if (clipBoxString != null && clipBoxString.length() != 0) {
            int commaPos = clipBoxString.indexOf(',');
            String xPart = clipBoxString.substring(0, commaPos);
            String yPart = clipBoxString.substring(commaPos + 1);
            double x = Double.parseDouble(xPart);
            double y = Double.parseDouble(yPart);
            this.clipbox = new Rectangle2D.Double(0,0,x,y);
        }
	}

    private VerticalAlign getVerticalAlign(String alignmentString) {
        if(alignmentString.length() < 2) {
            return V_CENTER;
        }
        return VerticalAlign.getAlign(alignmentString.charAt(1));
    }

    private HorizontalAlign getHorizontalAlign(String alignmentString) {
        if(alignmentString.length() < 1) {
            return H_CENTER;
        }
        return HorizontalAlign.getAlign(alignmentString.charAt(0));
    }

    private FontStyle getFontStyle(String fontStyleString) {
        if(fontStyleString.length() < 1) {
            return null;
        }
        return FontStyle.getFontStyle(fontStyleString.charAt(0));
    }

    private String[] extractFormattingStringSegment(String formattingString) {
        if (formattingString.length() == 0) {
            return new String[] { null, "" };
        }
        String segment, rest;
        if (formattingString.startsWith("(")) {
            int parPos = formattingString.indexOf(')');
            segment = formattingString.substring(1, parPos);
            rest = formattingString.substring(parPos + 1);
            int commaPos = rest.indexOf(',');
            if (commaPos != -1) {
                rest = rest.substring(commaPos + 1);
            }
        } else {
            int commaPos = formattingString.indexOf(',');
            if (commaPos == -1) {
                segment = new String(formattingString);
                rest = "";
            } else {
                segment = formattingString.substring(0, commaPos);
                rest = formattingString.substring(commaPos + 1);
            }
        }
        CSCParser.logger.log(Level.FINEST, "Format string split: " + segment + "-" + rest);
        return new String[] { segment, rest };
    }

    public Rectangle2D getClipbox() {
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