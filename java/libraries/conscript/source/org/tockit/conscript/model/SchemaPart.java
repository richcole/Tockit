/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.net.URL;
import java.util.Map;

public abstract class SchemaPart {

	abstract public URL getFile();
	abstract public String getIdentifier();
	abstract public String getRemark();
	abstract public Map getSpecials();
	abstract public FormattedString getTitle();

	abstract public void setFile(URL file);
	abstract public void setIdentifier(String identifier);
	abstract public void setRemark(String remark);
	abstract public void setSpecials(Map specials);
	abstract public void setTitle(FormattedString title);
}