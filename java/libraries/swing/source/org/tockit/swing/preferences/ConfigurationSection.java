/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package org.tockit.swing.preferences;


public class ConfigurationSection {
    private ConfigurationSubsection[] subsections;
    private String name;
    
    public ConfigurationSection(ConfigurationSubsection[] subsections, String name) {
        this.subsections = subsections;
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }

    public ConfigurationSubsection[] getSubsections() {
        return this.subsections;
    }
}
