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

public class StringMap extends SchemaPart{
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Map specials;
	private Map map;
		
	public StringMap(URL file, String identifier, FormattedString title,
					  String remark, Map specials, Map map) {
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

	public Map getMap() {
		return map;
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

	public void setMap(Map map) {
		this.map = map;
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