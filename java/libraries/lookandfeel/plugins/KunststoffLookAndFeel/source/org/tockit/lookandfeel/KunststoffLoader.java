/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.lookandfeel;

import org.tockit.plugin.Plugin;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.incors.plaf.kunststoff.KunststoffLookAndFeel;

/**
 * A class loading the Kunststoff lookAndFeel from Incors.
 */
public class KunststoffLoader implements Plugin {
    public void load() {
        try {
            UIManager.setLookAndFeel(new KunststoffLookAndFeel());
            UIDefaults defaults = UIManager.getDefaults();
            defaults.put("ClassLoader", this.getClass().getClassLoader());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException("Loading Kunststoff look and feel failed");
        }
    }
}
