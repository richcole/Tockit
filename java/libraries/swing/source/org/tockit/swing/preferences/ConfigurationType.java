/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.swing.preferences;


public class ConfigurationType {
    public static class Enumeration extends ConfigurationType {
        private String[] values;
        private Enumeration(String[] values) {
            this.values = values;
        }
        public String[] getValues() {
            return this.values;
        }
    }

    public final static ConfigurationType INTEGER = new ConfigurationType();
    public final static ConfigurationType DOUBLE = new ConfigurationType();
    public final static ConfigurationType BOOLEAN = new ConfigurationType();
    public final static ConfigurationType STRING = new ConfigurationType();
    public final static ConfigurationType COLOR = new ConfigurationType();
    public final static ConfigurationType FONT_FAMILY = new ConfigurationType();
    private ConfigurationType() {};
    public static ConfigurationType createEnumType(String[] values) {
        return new Enumeration(values);
    }
}
