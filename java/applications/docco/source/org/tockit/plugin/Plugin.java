/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.plugin;

/**
 * Plugin interface. All plugins should implement this interface.
 */
public interface Plugin {
	/**
	 * Load plugin.
	 * 
	 * <p>
	 * This method is expected to perform all startup actions needed 
	 * and is also responsible for registering plugin implementation 
	 * with a corresponding registry. 
	 * </p>
	 * <p>
	 * For instance, DocumentHandler plugin should register itself with DocumentHandlerRegistry.
	 * </p>
	 */
	public void load();
}
