/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package query;

import org.apache.lucene.document.Document;

public class HitReference {
	
	private static final String DOC_PATH_FIELD = "path";
	
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
		return this.getDocument().getField(DOC_PATH_FIELD).equals(otherHR.getDocument().getField(DOC_PATH_FIELD));
	}
	
	public int hashCode() {
		return this.getDocument().getField(DOC_PATH_FIELD).hashCode();
	}

}
