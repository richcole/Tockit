/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ConceptualSchema extends ConscriptStructure{
	private DatabaseDefinition database;
	private List concreteScales = new ArrayList();
	
	public ConceptualSchema(String identifier) {
        super(identifier);
	}

	public List getConcreteScales() {
		return Collections.unmodifiableList(concreteScales);
	}

	public DatabaseDefinition getDatabase() {
		return database;
	}

	public void addConcreteScale(ConcreteScale concreteScale) {
		this.concreteScales.add(concreteScale);
	}

	public void setDatabase(DatabaseDefinition database) {
		this.database = database;
	}
}