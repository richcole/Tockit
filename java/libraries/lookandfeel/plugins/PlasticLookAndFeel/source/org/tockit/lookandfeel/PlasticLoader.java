/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package org.tockit.lookandfeel;

import org.tockit.plugin.Plugin;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;

import com.jgoodies.plaf.plastic.PlasticLookAndFeel;
import com.jgoodies.plaf.plastic.theme.SkyBlue;

/**
 * A class loading a variant of the plastic lookAndFeel from JGoodies.
 */
public class PlasticLoader implements Plugin {
    public void load() {
        try {
            SkyBlue theme = new SkyBlue(){
                protected ColorUIResource getSecondary3() {
                     return new ColorUIResource(214,212,206);
                }
                protected ColorUIResource getPrimary1() {
                    return new ColorUIResource(150,150,200);
                }
                protected ColorUIResource getPrimary3() {
                    return new ColorUIResource(150,150,200);
                }
                public ColorUIResource getPrimaryControlHighlight() {
                    return new ColorUIResource(230,230,255);
                }
                public ColorUIResource getPrimaryControlDarkShadow() {
                    return new ColorUIResource(100,100,150);
                }
                public ColorUIResource getFocusColor() {
                    return new ColorUIResource(50,50,80);
                }
                public ColorUIResource getHighlightedTextColor() {
                    return new ColorUIResource(255,255,255);
                }
            };
            PlasticLookAndFeel.setMyCurrentTheme(theme);
            UIManager.setLookAndFeel(new PlasticLookAndFeel());
            UIDefaults defaults = UIManager.getDefaults();
            defaults.put("ClassLoader", this.getClass().getClassLoader());
        } catch (UnsupportedLookAndFeelException e1) {
            throw new RuntimeException("Loading Plastic look and feel failed");
        }
    }
}
