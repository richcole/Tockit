/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.plugin;

public class PluginLoadFailedException extends Exception {

	public PluginLoadFailedException(String message) {
		super(message);
	}

	public PluginLoadFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
