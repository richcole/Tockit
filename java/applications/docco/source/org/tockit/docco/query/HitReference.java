/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.query;

import org.apache.lucene.document.Document;
import org.tockit.docco.GlobalConstants;

public class HitReference implements Comparable {
	private Document doc;
	private float score;
	
	
	public HitReference(Document doc, float score) {
		this.doc = doc;
		this.score = score;
	}
	
	public Document getDocument() {
		return this.doc;
	}
	
	public float getScore() {
		return this.score;
	}
	
	public boolean equals(Object other) {
		if(other.getClass() != this.getClass()) {
			return false;
		}
		HitReference otherHR = (HitReference) other;
		return this.getDocument().getField(GlobalConstants.FIELD_DOC_PATH).toString().equals(
								otherHR.getDocument().getField(GlobalConstants.FIELD_DOC_PATH).toString());
	}
	
	public int hashCode() {
		return this.getDocument().getField(GlobalConstants.FIELD_DOC_PATH).toString().hashCode();
	}

	public String toString() {
		return this.getDocument().get(GlobalConstants.FIELD_DOC_PATH);
	}

    public int compareTo(Object o) {
        return this.hashCode()/2 - o.hashCode()/2;
    }
}
