/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class ConceptualSchema extends ConscriptStructure{
	private DatabaseDefinition database;
	private List<ConcreteScale> concreteScales = new ArrayList<ConcreteScale>();
	
	public ConceptualSchema(String identifier) {
        super(identifier);
	}

	public List<ConcreteScale> getConcreteScales() {
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

    @Override
	public void printCSC(PrintStream stream) {
        printTitleRemarkSpecials(stream);
        stream.print("\t\t(" + this.database.getName());
        for (Iterator<ConcreteScale> iter = this.concreteScales.iterator(); iter.hasNext();) {
            ConcreteScale scale = iter.next();
            stream.println(",");
            stream.print("\t\t\t" + scale.getName());
        }
        stream.println(");");
    }
}