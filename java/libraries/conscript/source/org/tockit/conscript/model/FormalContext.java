/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.net.URL;
import java.util.Hashtable;

public class FormalContext extends SchemaPart{
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Hashtable specials;
	private FCAObject[] objects;
	private FCAAttribute[] attributes;
	private BinaryRelation relation;

	public FormalContext(URL file, String identifier, FormattedString title, String remark, Hashtable specials, FCAObject[] objects, FCAAttribute[] attributes, BinaryRelation relation) { 
		this.file = file;
		this.identifier = identifier;
		this.title = title;
		this.remark = remark;
		this.specials = specials;
		this.objects = objects;
		this.attributes = attributes;
		this.relation = relation;
		
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

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setSpecials(Hashtable specials) {
		this.specials = specials;
	}

	public void setTitle(FormattedString title) {
		this.title = title;
	}
	

	public FCAAttribute[] getAttributes() {
		return attributes;
	}

	public FCAObject[] getObjects() {
		return objects;
	}

	public BinaryRelation getRelation() {
		return relation;
	}

	public void setAttributes(FCAAttribute[] attributes) {
		this.attributes = attributes;
	}

	public void setObjects(FCAObject[] objects) {
		this.objects = objects;
	}

	public void setRelation(BinaryRelation relation) {
		this.relation = relation;
	}
}