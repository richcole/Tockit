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

public class AbstractScale extends SchemaPart{
	private FormalContext context;
	private LineDiagram[] lineDiagrams;
	
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Map specials;
	
	public AbstractScale(URL file, String identifier, FormattedString title,
						  String remark, Map specials, 
						  FormalContext context, LineDiagram[] lineDiagrams) {
		this.file = file;
		this.identifier = identifier;
		this.title = title;
		this.remark = remark;
		this.specials = specials;
		this.context = context;
		this.lineDiagrams = lineDiagrams;
	}
		
	public FormalContext getContext() {
		return context;
	}
	public LineDiagram[] getLineDiagrams() {
		return lineDiagrams;
	}

	public void setContext(FormalContext context) {
		this.context = context;
	}

	public void setLineDiagrams(LineDiagram[] lineDiagrams) {
		this.lineDiagrams = lineDiagrams;
	}

	public URL getFile() {
		return file;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getRemark() {
		return remark;
	}

	public Map getSpecials() {
		return specials;
	}

	public FormattedString getTitle() {
		return title;
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
}