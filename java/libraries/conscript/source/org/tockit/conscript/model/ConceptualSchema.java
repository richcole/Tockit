/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;


public class ConceptualSchema extends SchemaPart{
	private DatabaseDefinition database;
	private ConcreteScale[] concreteScales;
	
	public ConceptualSchema(CSCFile file, String identifier) {
        super(file, identifier);
	}

	public ConcreteScale[] getConcreteScales() {
		return concreteScales;
	}

	public DatabaseDefinition getDatabase() {
		return database;
	}

	public void setConcreteScales(ConcreteScale[] concreteScales) {
		this.concreteScales = concreteScales;
	}

	public void setDatabase(DatabaseDefinition database) {
		this.database = database;
	}
}