/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.swing.preferences;


public class ConfigurationSubsection {
    private ConfigurationEntry[] entries;
    private String name;
    
    public ConfigurationSubsection(ConfigurationEntry[] entries, String name) {
        this.entries = entries;
        this.name = name;
    }
    
    public ConfigurationEntry[] getEntries() {
        return this.entries;
    }

    public String getName() {
        return this.name;
    }
}
