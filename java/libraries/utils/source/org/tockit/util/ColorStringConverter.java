/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.util;

import java.awt.Color;

/**
 * Function class with static methods to read and write colors from strings.
 * 
 * The difference to Color.decode(String) is that this class does support alpha
 * and can write the colors, too.
 */
public class ColorStringConverter {

    private static final String chars = "0123456789abcdef";

    private static String toHex(int i) {
        int low = i % 16;
        int high = (i / 16) % 16;
        return chars.charAt(low) + "" + chars.charAt(high);
    }

    private static int fromHex(String s) {
    	String lower = s.toLowerCase();
        return chars.indexOf(lower.charAt(0)) * 16 + chars.indexOf(lower.charAt(1));
    }

    public static Color stringToColor(String s) {
        Color color = new Color(fromHex(s.substring(3, 5)),
                fromHex(s.substring(5, 7)),
                fromHex(s.substring(7, 9)),
                fromHex(s.substring(1, 3)));
        return color;

    }

    public static String colorToString(Color color) {
        return "#" + toHex(color.getAlpha()) +
                toHex(color.getRed()) +
                toHex(color.getGreen()) +
                toHex(color.getBlue());
    }
}
