/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @todo subtypes should check for initialization in setters, too
 */
public abstract class ConscriptStructure {
    private String name;
    private FormattedString title = null;
    private String remark = null;
    private Map<String, Set> specials = new Hashtable<String, Set>();
    
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

    public Map<String, Set> getSpecials() {
        return Collections.unmodifiableMap(specials);
    }

    public Set getSpecials(String specialGroup) {
        return Collections.unmodifiableSet(specials.get(specialGroup));
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
        Set<String> specialSet = this.specials.get(special);
        if(specialSet == null) {
            specialSet = new HashSet<String>();
            this.specials.put(special, specialSet);
        }
        specialSet.add(value);
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

    abstract public void printCSC(PrintStream stream);

    protected void printTitleRemarkSpecials(PrintStream stream) {
        stream.println("\t" + getName() + " = ");
        if(this.title != null) {
            stream.println("\t\tTITLE \"" + this.title + "\"");
        }
        if(this.remark != null) {
            stream.println("\t\tREMARK \"" + this.remark + "\"");
        }
        if(!this.specials.isEmpty()) {
            stream.println("\t\tSPECIAL");
            for (Iterator<String> iter = this.specials.keySet().iterator(); iter.hasNext();) {
                String special = iter.next();
                Set specialSet = this.specials.get(special);
                for (Iterator innerIter = specialSet.iterator(); innerIter.hasNext();) {
                    String value = (String) innerIter.next();
                    stream.println("\t\t\t\"" + special + ":" + value + "\"");
                }
            }
        }
    }
}