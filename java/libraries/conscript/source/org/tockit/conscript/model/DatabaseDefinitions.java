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

public class DatabaseDefinitions extends SchemaPart{
	private DatabaseDefinition[] databases;
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Hashtable specials;
	
	public DatabaseDefinitions(URL file, String identifier, FormattedString title, 
								String remark, Hashtable specials, DatabaseDefinition[] databases) {
		this.file = file;
		this.identifier = identifier;
		this.title = title;
		this.remark = remark;
		this.specials = specials;
		this.databases = databases;
	}

	public DatabaseDefinition[] getDatabases() {
		return databases;
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

	public void setDatabases(DatabaseDefinition[] databases) {
		this.databases = databases;
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
}