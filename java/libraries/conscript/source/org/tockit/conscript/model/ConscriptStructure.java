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

public abstract class ConscriptStructure {
    private String name;
    private FormattedString title = null;
    private String remark = null;
    private Map specials = new Hashtable();

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
    	this.remark = remark;
    }

    public void addSpecial(String special, String value) {
        specials.put(special, value);
    }

    public void setTitle(FormattedString title) {
    	this.title = title;
    }
}