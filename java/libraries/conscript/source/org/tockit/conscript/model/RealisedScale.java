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

public class RealisedScale extends SchemaPart{
	private ConcreteScale concreteScale;
	private IdentifierMap identifierMap;
	
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Map specials;
	
	public RealisedScale(URL file, String identifier, FormattedString title, 
						  String remark, Map specials, ConcreteScale concreteScale, 
						  IdentifierMap identifierMap) {
		this.file = file;
		this.identifier = identifier;
		this.title = title;
		this.remark = remark;
		this.specials = specials;
		this.concreteScale = concreteScale;
		this.identifierMap = identifierMap;				  	
	}

	public ConcreteScale getConcreteScale() {
		return concreteScale;
	}

	public URL getFile() {
		return file;
	}

	public String getIdentifier() {
		return identifier;
	}

	public IdentifierMap getIdentifierMap() {
		return identifierMap;
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

	public void setConcreteScale(ConcreteScale concreteScale) {
		this.concreteScale = concreteScale;
	}

	public void setFile(URL file) {
		this.file = file;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setIdentifierMap(IdentifierMap identifierMap) {
		this.identifierMap = identifierMap;
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