/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class DatabaseDefinition {
	private String name;
	private String table;
	private String primaryKey;
	
	public DatabaseDefinition(String name, String table, String primaryKey) {
		this.name = name;
		this.table = table;
		this.primaryKey = primaryKey;
	}

	public String getName() {
		return name;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public String getTable() {
		return table;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public void setTable(String table) {
		this.table = table;
	}
}