/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.query;

import org.apache.lucene.search.Query;

public class SimpleQueryReference {
	private Boolean required = null;
	private Boolean prohibited = null;
	private Query query;

	public SimpleQueryReference (Query query) {
		this.query = query;
	}

	public SimpleQueryReference (Query query, boolean required, boolean prohibited) {
		this.query = query;
		this.required = new Boolean(required);
		this.prohibited = new Boolean(prohibited);
	}

	public Query getQuery() {
		return this.query;
	}

	public Boolean isRequired() {
		return this.required;
	}

	public Boolean isProhibited() {
		return this.prohibited;
	}
	
	public String getStringQueryRepresentation () {
		String res = "";
		if (this.required != null) {
			if (this.required.booleanValue()) {
				res = "+";
			}
		}
		if (this.prohibited != null) {
			if (this.prohibited.booleanValue()) {
				res = "-";
			}
		}
		res = res + query.toString();
			
		return res;
	}
	

	public String toString() {
		String str = "Query: " + this.query;
		if (this.required != null) {
			str = str + ", required = " + this.required.booleanValue();
		} 
		if (this.prohibited != null) {
			str = str + ", prohibited = " + this.prohibited.booleanValue();
		}
		str = str + ". Query type: " + this.query.getClass();
		return str;
	}
}
