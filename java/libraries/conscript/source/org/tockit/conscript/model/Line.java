/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class Line {
	private Point from;
	private Point to;
	private PointFormat format;
	
	public Line(Point from, Point to, PointFormat format) {
		this.from = from;
		this.to = to;
		this.format = format;
	}

	public PointFormat getFormat() {
		return format;
	}

	public Point getFrom() {
		return from;
	}

	public Point getTo() {
		return to;
	}

	public void setFormat(PointFormat format) {
		this.format = format;
	}

	public void setFrom(Point from) {
		this.from = from;
	}

	public void setTo(Point to) {
		this.to = to;
	}

    @Override
	public String toString() {
        return "(" + this.from.getNumber() + ", " + this.to.getNumber() + ")";
    }
}