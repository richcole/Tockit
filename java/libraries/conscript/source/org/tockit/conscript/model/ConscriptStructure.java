/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

/**
 * @todo subtypes should check for initialization in setters, too
 */
public abstract class ConscriptStructure {
    private String name;
    private FormattedString title = null;
    private String remark = null;
    private Map specials = new Hashtable();
    
    private boolean initialized = false;

    public ConscriptStructure(String name) {
        this.name = name;
    }
    
    public String getName() {
    	return name;
    }

    public String getRemark() {
    	return remark;
    }

    public Map getSpecials() {
    	return Collections.unmodifiableMap(specials);
    }

    public FormattedString getTitle() {
    	return title;
    }

    public void setRemark(String remark) {
        if(this.isInitialized()) {
            throw new IllegalStateException("Structure already initialized");
        }
    	this.remark = remark;
    }

    public void addSpecial(String special, String value) {
        if(this.isInitialized()) {
            throw new IllegalStateException("Structure already initialized");
        }
        specials.put(special, value);
    }

    public void setTitle(FormattedString title) {
        if(this.isInitialized()) {
            throw new IllegalStateException("Structure already initialized");
        }
    	this.title = title;
    }
    
    public void setInitialized() {
        if(this.isInitialized()) {
            throw new IllegalStateException("Structure already initialized");
        }
        this.initialized = true;
    }
    
    public boolean isInitialized() {
        return this.initialized;
    }
}