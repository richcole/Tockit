/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package org.tockit.swing.preferences;


/**
 * @todo entries should be able to store their default value
 */
public class ConfigurationEntry {
    private ExtendedPreferences node;
    private String key;
    private ConfigurationType type;
    private String name;
    
    public ConfigurationEntry(ExtendedPreferences node, String key, 
                              ConfigurationType type, String name) {
        this.node = node;
        this.key = key;
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public ExtendedPreferences getNode() {
        return this.node;
    }

    public ConfigurationType getType() {
        return this.type;
    }

    public String getKey() {
        return this.key;
    }
}
