/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package org.tockit.swing;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 * This class provides services to ease session management.
 * 
 * It extends the standard JDK preferences to allow storing more complex information
 * such as windows placements, colors and string lists.
 */
public class ExtendedPreferences extends Preferences {
    private Preferences javaPreferences = null;
    
    /**
     * A string used to identify the child containing window placement information.
     */
    private static final String WINDOW_PLACEMENT_CHILD_NODE_NAME = "org.tockit.swing.SessionManagement.windowPlacement";

    /**
     * Public construction only through static methods.
     */
    private ExtendedPreferences(Preferences preferences) {
        this.javaPreferences = preferences;
    }
    
    /**
     * Gets the user preferences for the package of the class.
     * 
     * For reason unknown to the author Java does not allow using the same signature,
     * so we have to add the "Ex" bit at the end. Apart from the fact that covariant
     * return types seem perfectly ok, this is a static method where no overriding can
     * occur anyway. At least not that I can see. The alternative would have been a
     * lot of casting.
     * 
     * @see systemNodeForPackageEx(Class)
     */
    public static ExtendedPreferences userNodeForPackageEx(Class cl) {
        return new ExtendedPreferences(Preferences.userNodeForPackage(cl));
    }
    
    /**
     * Gets the system preferences for the package of the class.
     * 
     * For reason unknown to the author Java does not allow using the same signature,
     * so we have to add the "Ex" bit at the end. Apart from the fact that covariant
     * return types seem perfectly ok, this is a static method where no overriding can
     * occur anyway. At least not that I can see. The alternative would have been a
     * lot of casting.
     * 
     * @see userNodeForPackageEx(Class)
     */
    public static ExtendedPreferences systemNodeForPackageEx(Class cl) {
        return new ExtendedPreferences(Preferences.systemNodeForPackage(cl));
    }
    
    /**
     * Stores the placement of a window in the preferences.
     * 
     * This includes position, size and flags such as maximimzed.
     *
     * @param window       the window whose placement is to be saved.
     * 
     * @see restoreWindowPlacement(Window, Rectangle)
     */
    public void storeWindowPlacement(Window window) {
        Preferences winPrefs = this.javaPreferences.node(WINDOW_PLACEMENT_CHILD_NODE_NAME);
        if(window instanceof Frame) {
            Frame frame = (Frame)window;
            winPrefs.putInt("state", frame.getExtendedState());
            frame.setExtendedState(Frame.NORMAL);           
        }
        winPrefs.putInt("x",window.getX());
        winPrefs.putInt("y",window.getY());
        winPrefs.putInt("width",window.getWidth());
        winPrefs.putInt("height",window.getHeight());
    }
    
    /**
     * Restores the placement of a window from the preferences.
     * 
     * This includes position, size and flags such as maximimzed. If the settings
     * can not be found in the preferences, the given rectangle is used instead.
     * 
     * @param window       the window whose placement is to be restored.
     * @param rectangle    the default placement. Must not be null.
     * 
     * @see storeWindowPlacement(Window)
     */
    public void restoreWindowPlacement(Window window, Rectangle defaultPlacement) {
        Preferences winPrefs = this.javaPreferences.node(WINDOW_PLACEMENT_CHILD_NODE_NAME);
        int x = winPrefs.getInt("x", defaultPlacement.x);
        int y = winPrefs.getInt("y", defaultPlacement.y);
        int width = winPrefs.getInt("width", defaultPlacement.width);
        int height = winPrefs.getInt("height", defaultPlacement.height);
        window.setBounds(x, y, width, height);
        if(window instanceof Frame) {
            Frame frame = (Frame)window;
            int state = winPrefs.getInt("state", Frame.NORMAL);
            frame.setExtendedState(state);
        }
    }
    
    /**
     * Stores a list of strings in the preferences.
     * 
     * @param nodeName     a string used as name for a node in which the strings are stored
     * @param data         a collection of objects whose toString() will be stored by iterating
     * 
     * @see restoreStringList(String)
     */
    public void putStringList(String nodeName, Collection data) {
        Preferences listPrefs = this.javaPreferences.node(nodeName);
        int count = 1;
        for (Iterator iter = data.iterator(); iter.hasNext();) {
            String string = iter.next().toString();
            listPrefs.put(String.valueOf(count), string);
            count++;
        }
    }
    
    /**
     * Retrieves a list of strings from the preferences.
     * 
     * @param nodeName     a string used as name for a node in which the strings are stored
     * 
     * @return a List of strings retrieved
     * 
     * @see storeStringList(String, Collection)
     */
    public List getStringList(String nodeName) {
        List retVal = new ArrayList();
        Preferences listPrefs = this.javaPreferences.node(nodeName);
        int count = 1;
        while(true) {
            String value = listPrefs.get(String.valueOf(count), null);
            if(value == null) {
                break;
            }
            retVal.add(value);
            count++;
        }
        return retVal;
    }

    /**
     * Stores a color in the preferences.
     * 
     * @param nodeName     a string used as name for a node in which the data is stored
     * @param value        the color to store, must not be null
     * 
     * @see retrieveColor(String, Color)
     */
    public void putColor(String nodeName, Color value) {
        Preferences colorPrefs = this.javaPreferences.node(nodeName);
        colorPrefs.putInt("red", value.getRed());
        colorPrefs.putInt("green", value.getGreen());
        colorPrefs.putInt("blue", value.getBlue());
        colorPrefs.putInt("alpha", value.getAlpha());
    }

    /**
     * Retrieves a color from the preferences.
     * 
     * @param nodeName     a string used as name for a node in which the data is stored
     * @param defaultValue the color to use in case no value can be retrieved from the preferences
     * 
     * @return the color found or the default value if no color could be found
     * 
     * @see storeColor(String, Color)
     */
    public Color getColor(String nodeName, Color defaultValue) {
        if(defaultValue == null) {
            try {
                if(!this.javaPreferences.nodeExists(nodeName)) {
                    return null;
                }
                Preferences colorPrefs = this.javaPreferences.node(nodeName);
                int red = colorPrefs.getInt("red", 0);
                int green = colorPrefs.getInt("green", 0);
                int blue = colorPrefs.getInt("blue", 0);
                int alpha = colorPrefs.getInt("alpha", 0);
                return new Color(red, green, blue, alpha);
            } catch (BackingStoreException e) {
                return null;
            }
        } else {
            try {
                if(!this.javaPreferences.nodeExists(nodeName)) {
                    return defaultValue;
                }
                Preferences colorPrefs = this.javaPreferences.node(nodeName);
                int red = colorPrefs.getInt("red", defaultValue.getRed());
                int green = colorPrefs.getInt("green", defaultValue.getGreen());
                int blue = colorPrefs.getInt("blue", defaultValue.getBlue());
                int alpha = colorPrefs.getInt("alpha", defaultValue.getAlpha());
                return new Color(red, green, blue, alpha);
            } catch (BackingStoreException e) {
                return defaultValue;
            }
        }
    }

    // from here on it is the implementation of the preferences API by delegation
    
    public void clear() throws BackingStoreException {
        this.javaPreferences.clear();
    }

    public void flush() throws BackingStoreException {
        this.javaPreferences.flush();
    }

    public void removeNode() throws BackingStoreException {
        this.javaPreferences.removeNode();
    }

    public void sync() throws BackingStoreException {
        this.javaPreferences.sync();
    }

    public boolean isUserNode() {
        return this.javaPreferences.isUserNode();
    }

    public void exportNode(OutputStream os) throws IOException, BackingStoreException {
        this.javaPreferences.exportNode(os);
    }

    public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
        this.javaPreferences.exportSubtree(os);
    }

    public String absolutePath() {
        return this.javaPreferences.absolutePath();
    }

    public String name() {
        return this.javaPreferences.name();
    }

    public String toString() {
        return this.javaPreferences.toString();
    }

    public String[] childrenNames() throws BackingStoreException {
        return this.javaPreferences.childrenNames();
    }

    public String[] keys() throws BackingStoreException {
        return this.javaPreferences.keys();
    }

    public void remove(String key) {
        this.javaPreferences.remove(key);
    }

    public boolean nodeExists(String pathName) throws BackingStoreException {
        return this.javaPreferences.nodeExists(pathName);
    }

    public double getDouble(String key, double def) {
        return this.javaPreferences.getDouble(key, def);
    }

    public void putDouble(String key, double value) {
        this.javaPreferences.putDouble(key, value);
    }

    public float getFloat(String key, float def) {
        return this.javaPreferences.getFloat(key, def);
    }

    public void putFloat(String key, float value) {
        this.javaPreferences.putFloat(key, value);
    }

    public int getInt(String key, int def) {
        return this.javaPreferences.getInt(key, def);
    }

    public void putInt(String key, int value) {
        this.javaPreferences.putInt(key, value);
    }

    public long getLong(String key, long def) {
        return this.javaPreferences.getLong(key, def);
    }

    public void putLong(String key, long value) {
        this.javaPreferences.putLong(key, value);
    }

    public void putBoolean(String key, boolean value) {
        this.javaPreferences.putBoolean(key, value);
    }

    public boolean getBoolean(String key, boolean def) {
        return this.javaPreferences.getBoolean(key, def);
    }

    public void putByteArray(String key, byte[] value) {
        this.javaPreferences.putByteArray(key, value);
    }

    public byte[] getByteArray(String key, byte[] def) {
        return this.javaPreferences.getByteArray(key, def);
    }

    public void addNodeChangeListener(NodeChangeListener ncl) {
        this.javaPreferences.addNodeChangeListener(ncl);
    }

    public void removeNodeChangeListener(NodeChangeListener ncl) {
        this.javaPreferences.removeNodeChangeListener(ncl);
    }

    public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        this.javaPreferences.addPreferenceChangeListener(pcl);
    }

    public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        this.javaPreferences.removePreferenceChangeListener(pcl);
    }

    public Preferences parent() {
        return this.javaPreferences.parent();
    }

    public void put(String key, String value) {
        this.javaPreferences.put(key, value);
    }

    public Preferences node(String pathName) {
        return this.javaPreferences.node(pathName);
    }

    public String get(String key, String def) {
        return this.javaPreferences.get(key, def);
    }
}
