/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.net.URL;
import java.util.Map;
import java.util.List;

public class RemarkStructure extends SchemaPart{
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Map specials;
	private List remarks;
	
	public RemarkStructure(URL file, String identifier, FormattedString title, 
							String remark, Map specials, List remarks) { 
	this.file = file;
	this.identifier = identifier;
	this.title = title;
	this.remark = remark;
	this.specials = specials;
	this.remarks = remarks;
	}

	public URL getFile() {
		return this.file;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public String getRemark() {
		return this.remark;
	}

	public Map getSpecials() {
		return this.specials;
	}

	public List getRemarks() {
		return remarks;
	}
	
	public FormattedString getTitle() {
		return this.title;
	}
	
	public void setFile(URL file) {
		this.file = file;
	}
	
	public void setIdentifier(String identifier) {
			this.identifier = identifier;
	}
	
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public void setSpecials(Map specials) {
		this.specials = specials;
	}
	
	public void setTitle(FormattedString title) {
		this.title = title;
	}

	public void setRemarks(List remarks) {
		this.remarks = remarks;
	}

}