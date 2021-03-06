/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.io.PrintStream;

public class ConcreteScale extends ConscriptStructure{
	private String[] tables;
	private String[] fields;
	private AbstractScale abstractScale;
	private QueryMap queryMap;
	private StringMap attributeMap;
	
	public ConcreteScale(String name) {
		super(name);
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

	public QueryMap getQueryMap() {
		return queryMap;
	}

	public String[] getTables() {
		return tables;
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

	public void setQueryMap(QueryMap queryMap) {
		this.queryMap = queryMap;
	}

	public void setTables(String[] tables) {
		this.tables = tables;
	}

    @Override
	public void printCSC(PrintStream stream) {
        printTitleRemarkSpecials(stream);
        stream.print("\t\t(" + this.abstractScale.getName() + ", ");
        if(this.queryMap != null) {
            stream.print(this.queryMap.getName());
        }
        stream.println(", " + this.attributeMap + ");");
    }
}