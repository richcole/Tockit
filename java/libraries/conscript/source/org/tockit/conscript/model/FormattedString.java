/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class FormattedString {
	private String content;
	private StringFormat format;
	
	public FormattedString(String content, StringFormat format) {
		this.content = content;
		this.format = format;
	}

	public String getContent() {
		return content;
	}

	public StringFormat getFormat() {
		return format;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setFormat(StringFormat format) {
		this.format = format;
	}

    public String toString() {
        String retVal = "\"" + this.content + "\" ";
        if(this.format != null) {
            retVal += this.format.toString();
        }
        return retVal;
    }
}