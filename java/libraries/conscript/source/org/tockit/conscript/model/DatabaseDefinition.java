/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.io.PrintStream;

public class DatabaseDefinition extends ConscriptStructure {
    private String databaseName;
	private String table;
	private String primaryKey;
	
	public DatabaseDefinition(String name) {
        super(name);
	}

    public String getDatabaseName() {
        return this.databaseName;
    }
    
	public String getPrimaryKey() {
		return primaryKey;
	}

	public String getTable() {
		return table;
	}
    
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
    
    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }
    
    public void setTable(String table) {
        this.table = table;
    }

    @Override
	public void printCSC(PrintStream stream) {
        printTitleRemarkSpecials(stream);
        stream.print("\t\t(\"" + this.databaseName + "\", ");
        stream.print("\"" + this.table + "\", ");
        stream.println("\"" + this.primaryKey + "\");");
    }
}