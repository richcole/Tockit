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

public class ConcreteScale extends SchemaPart{
	private String[] tables;
	private String[] fields;
	private AbstractScale abstractScale;
	private QueryMap queryMap;
	private StringMap attributeMap;
	
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Hashtable specials;
	
	public ConcreteScale(URL file, String identifier) {
		this.file = file;
		this.identifier = identifier;
	}

	public AbstractScale getAbstractScale() {
		return abstractScale;
	}

	public StringMap getAttributeMap() {
		return attributeMap;
	}

	public String[] getFields() {
		return fields;
	}

	public URL getFile() {
		return file;
	}

	public String getIdentifier() {
		return identifier;
	}

	public QueryMap getQueryMap() {
		return queryMap;
	}

	public String getRemark() {
		return remark;
	}

	public Hashtable getSpecials() {
		return specials;
	}

	public String[] getTables() {
		return tables;
	}

	public FormattedString getTitle() {
		return title;
	}

	public void setAbstractScale(AbstractScale abstractScale) {
		this.abstractScale = abstractScale;
	}

	public void setAttributeMap(StringMap attributeMap) {
		this.attributeMap = attributeMap;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public void setFile(URL file) {
		this.file = file;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setQueryMap(QueryMap queryMap) {
		this.queryMap = queryMap;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setSpecials(Hashtable specials) {
		this.specials = specials;
	}

	public void setTables(String[] tables) {
		this.tables = tables;
	}

	public void setTitle(FormattedString title) {
		this.title = title;
	}
}