/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class Point {
	private long number;
	private double x;
	private double y;
	private FormattedString label;
	private PointFormat format;
	
	public Point(long number, double x, double y, FormattedString label, PointFormat format) {
		this.number = number;
		this.x = x;
		this.y = y;
		this.label = label;
		this.format = format;
	}

	public PointFormat getFormat() {
		return format;
	}

	public FormattedString getLabel() {
		return label;
	}

	public long getNumber() {
		return number;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setFormat(PointFormat format) {
		this.format = format;
	}

	public void setLabel(FormattedString label) {
		this.label = label;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}
    
    public String toString() {
        if(this.x == Double.MIN_VALUE) {
            return "" + this.number;
        }
        return "" + this.number + " " + this.x + " " + this.y;
    }
}