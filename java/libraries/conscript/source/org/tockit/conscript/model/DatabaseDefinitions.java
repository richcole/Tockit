/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

public class DatabaseDefinitions extends SchemaPart{
	private DatabaseDefinition[] databases;
    
    public DatabaseDefinitions(ConceptualFile file, String identifier) {
        super(file, identifier);
	}

	public DatabaseDefinition[] getDatabases() {
		return databases;
	}

	public void setDatabases(DatabaseDefinition[] databases) {
		this.databases = databases;
	}
}