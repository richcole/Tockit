/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.util.List;

public class RemarkStructure extends SchemaPart{
    private List remarks;
	
	public RemarkStructure(ConceptualFile file, String identifier) {
        super(file, identifier);
    }

	public List getRemarks() {
		return remarks;
	}
	
	public void setRemarks(List remarks) {
		this.remarks = remarks;
	}
}