/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class FCAObject {
	private Point point;
	private String identifier;
	private FormattedString format;
    private String content;
	
	public FCAObject(Point point, String identifier, String content, FormattedString format) {
		this.point = point;
		this.identifier = identifier;
        this.content = content;
		this.format = format;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Point getPoint() {
		return point;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setPoint(Point point) {
		this.point = point;
	}
    
    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public FormattedString getFormat() {
        return this.format;
    }
    
    public void setFormat(FormattedString format) {
        this.format = format;
    }
}