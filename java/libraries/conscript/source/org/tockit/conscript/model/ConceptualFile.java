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

public class ConceptualFile {
	private StringMap objectMap;
	private RealisedScale[] realisedScales;
	
	private URL file;
	private String identifier;
	private FormattedString title;
	private String remark;
	private Hashtable specials;
    private DatabaseDefinitions databaseDefinitions;
	
	public ConceptualFile(URL file, String identifier, FormattedString title, 
						   String remark, Hashtable specials, StringMap objectMap, 
						   RealisedScale[] realisedScales) {
		this.file = file;
		this.identifier = identifier;
		this.title = title;
		this.remark = remark;
		this.specials = specials;
		this.objectMap = objectMap;
		this.realisedScales = realisedScales;
	}

	public URL getFile() {
		return file;
	}

	public String getIdentifier() {
		return identifier;
	}

	public StringMap getObjectMap() {
		return objectMap;
	}

	public RealisedScale[] getRealisedScales() {
		return realisedScales;
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

	public void setFile(URL file) {
		this.file = file;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setObjectMap(StringMap objectMap) {
		this.objectMap = objectMap;
	}

	public void setRealisedScales(RealisedScale[] realisedScales) {
		this.realisedScales = realisedScales;
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
    
    public DatabaseDefinitions getDatabaseDefinitions() {
        return this.databaseDefinitions;
    }
    
    public void setDatabaseDefinitions(DatabaseDefinitions databaseDefinitions) {
        this.databaseDefinitions = databaseDefinitions;
    }
}