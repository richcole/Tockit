/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.swing.preferences;

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
 * 
 * Note that while this class extends the Preferences class, it does not use the superclass
 * directly but instead delegates to the version passed into the constructor since that is
 * the only way to decorate existing Preferences instances. Extending the Preferences class
 * is really to be read only as subtyping, not reuse; unfortunately there is no interface for
 * the Preferences API, only the implementation. 
 */
public class ExtendedPreferences extends Preferences {
    private Preferences javaPreferences = null;
    
    /**
     * A string used to identify the child containing window placement information.
     */
    private static final String WINDOW_PLACEMENT_CHILD_NODE_NAME = "org.tockit.swing.SessionManagement.windowPlacement";

    /**
     * Creates a new ExtendedPreferences object wrapping the given Preferences.
     */
    public ExtendedPreferences(Preferences preferences) {
        this.javaPreferences = preferences;
    }
    
    /**
     * Returns a user node for a certain class.
     * 
     * The node is determined by a subnode of the package node which uses the
     * unqualified class name.
     * 
     * @param cl the class determining the node
     * 
     * @return a node representing the class in the preferences
     */    
    public static ExtendedPreferences userNodeForClass(Class<?> cl) {
        String name = getUnqualifiedClassName(cl);
        return new ExtendedPreferences(Preferences.userNodeForPackage(cl).node(name));
    }
    
    /**
     * Returns a system node for a certain class.
     * 
     * The node is determined by a subnode of the package node which uses the
     * unqualified class name.
     * 
     * @param cl the class determining the node
     * 
     * @return a node representing the class in the preferences
     */    
    public static ExtendedPreferences systemNodeForClass(Class<?> cl) {
        String name = getUnqualifiedClassName(cl);
        return new ExtendedPreferences(Preferences.systemNodeForPackage(cl).node(name));
    }
        
    /**
     * Stores the placement of a window in the preferences.
     * 
     * This includes position, size and flags such as maximized.
     * 
     * The naming scheme differs for the storeWindowPlacement/restoreWindowPlacement
     * methods to indicate that they do more than the put/get methods -- they affect
     * the window given.
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
     * This includes position, size and flags such as maximized. If the settings
     * can not be found in the preferences, the given rectangle is used instead.
     * 
     * The naming scheme differs for the storeWindowPlacement/restoreWindowPlacement
     * methods to indicate that they do more than the put/get methods -- they affect
     * the window given.
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
    public void putStringList(String nodeName, Collection<?> data) {
        Preferences listPrefs = this.javaPreferences.node(nodeName);
        int count = 1;
        for (Iterator<?> iter = data.iterator(); iter.hasNext();) {
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
    public List<String> getStringList(String nodeName) {
        List<String> retVal = new ArrayList<String>();
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
     * @param value        the color to store, if null this method does nothing
     * 
     * @see retrieveColor(String, Color)
     */
    public void putColor(String nodeName, Color value) {
        if(value == null) {
            return;
        }
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

    /**
     * A helper method to find the unqualified name of a class.
     * 
     * @param cl the class to find the name for, must not be null
     * 
     * @return the unqualified name of the class
     */
    private static String getUnqualifiedClassName(Class<?> cl) {
        // if there is no dot in the string the lastIndexOf will be
        // -1, thus the full string will be returned
        String fullName = cl.getName();
        return fullName.substring(fullName.lastIndexOf('.') + 1);
    }

    /**
     * Removes this node and all nodes below.
     */
    public static void removeBranch(Preferences node) throws BackingStoreException {
        try {
            for (int i = 0; i < node.childrenNames().length; i++) {
                String childName = node.childrenNames()[i];
                Preferences child = node.node(childName);
                removeBranch(child);
            }
            node.removeNode();
        } catch (IllegalStateException e) {
            // we might get a remove multiple times, hard to avoid since it is hard to
            // detect if a node is still there
        }
    }

    // from here on it is the implementation of the preferences API by delegation
    
    @Override
	public void clear() throws BackingStoreException {
        this.javaPreferences.clear();
    }

    @Override
	public void flush() throws BackingStoreException {
        this.javaPreferences.flush();
    }

    @Override
	public void removeNode() throws BackingStoreException {
        this.javaPreferences.removeNode();
    }

    @Override
	public void sync() throws BackingStoreException {
        this.javaPreferences.sync();
    }

    @Override
	public boolean isUserNode() {
        return this.javaPreferences.isUserNode();
    }

    @Override
	public void exportNode(OutputStream os) throws IOException, BackingStoreException {
        this.javaPreferences.exportNode(os);
    }

    @Override
	public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
        this.javaPreferences.exportSubtree(os);
    }

    @Override
	public String absolutePath() {
        return this.javaPreferences.absolutePath();
    }

    @Override
	public String name() {
        return this.javaPreferences.name();
    }

    @Override
	public String toString() {
        return this.javaPreferences.toString();
    }

    @Override
	public String[] childrenNames() throws BackingStoreException {
        return this.javaPreferences.childrenNames();
    }

    @Override
	public String[] keys() throws BackingStoreException {
        return this.javaPreferences.keys();
    }

    @Override
	public void remove(String key) {
        this.javaPreferences.remove(key);
    }

    @Override
	public boolean nodeExists(String pathName) throws BackingStoreException {
        return this.javaPreferences.nodeExists(pathName);
    }

    @Override
	public double getDouble(String key, double def) {
        return this.javaPreferences.getDouble(key, def);
    }

    @Override
	public void putDouble(String key, double value) {
        this.javaPreferences.putDouble(key, value);
    }

    @Override
	public float getFloat(String key, float def) {
        return this.javaPreferences.getFloat(key, def);
    }

    @Override
	public void putFloat(String key, float value) {
        this.javaPreferences.putFloat(key, value);
    }

    @Override
	public int getInt(String key, int def) {
        return this.javaPreferences.getInt(key, def);
    }

    @Override
	public void putInt(String key, int value) {
        this.javaPreferences.putInt(key, value);
    }

    @Override
	public long getLong(String key, long def) {
        return this.javaPreferences.getLong(key, def);
    }

    @Override
	public void putLong(String key, long value) {
        this.javaPreferences.putLong(key, value);
    }

    @Override
	public void putBoolean(String key, boolean value) {
        this.javaPreferences.putBoolean(key, value);
    }

    @Override
	public boolean getBoolean(String key, boolean def) {
        return this.javaPreferences.getBoolean(key, def);
    }

    @Override
	public void putByteArray(String key, byte[] value) {
        this.javaPreferences.putByteArray(key, value);
    }

    @Override
	public byte[] getByteArray(String key, byte[] def) {
        return this.javaPreferences.getByteArray(key, def);
    }

    @Override
	public void addNodeChangeListener(NodeChangeListener ncl) {
        this.javaPreferences.addNodeChangeListener(ncl);
    }

    @Override
	public void removeNodeChangeListener(NodeChangeListener ncl) {
        this.javaPreferences.removeNodeChangeListener(ncl);
    }

    @Override
	public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
        this.javaPreferences.addPreferenceChangeListener(pcl);
    }

    @Override
	public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
        this.javaPreferences.removePreferenceChangeListener(pcl);
    }

    @Override
	public Preferences parent() {
        return this.javaPreferences.parent();
    }

    @Override
	public void put(String key, String value) {
        this.javaPreferences.put(key, value);
    }

    @Override
	public Preferences node(String pathName) {
        return this.javaPreferences.node(pathName);
    }

    @Override
	public String get(String key, String def) {
        return this.javaPreferences.get(key, def);
    }
}
