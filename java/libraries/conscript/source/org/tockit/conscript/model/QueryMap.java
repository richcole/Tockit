package org.tockit.conscript.model;

import java.net.URL;
import java.util.Hashtable;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class QueryMap extends SchemaPart{
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Hashtable specials;
	private Hashtable map;
		
	public QueryMap() {
	}

	public QueryMap(URL file, String identifier, FormattedString title,
					  String remark, Hashtable specials, Hashtable map) {
		this.file = file;
		this.identifier = identifier;
		this.title = title;
		this.remark = remark;
		this.specials = specials;
		this.map = map;
	}
	
	public URL getFile() {
		return file;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Hashtable getMap() {
		return map;
	}

	public String getRemark() {
		return remark;
	}

	public Hashtable getSpecials() {
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

	public void setMap(Hashtable map) {
		this.map = map;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setSpecials(Hashtable specials) {
		this.specials = specials;
	}

	public void setTitle(FormattedString title) {
		this.title = title;
	}
}

