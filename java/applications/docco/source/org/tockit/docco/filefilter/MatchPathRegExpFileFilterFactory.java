/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.filefilter;

import java.io.File;
import java.text.MessageFormat;
import java.util.regex.Pattern;

import org.tockit.docco.gui.GuiMessages;


public class MatchPathRegExpFileFilterFactory implements FileFilterFactory {
	private class MatchNameRegExpFileFilter extends DoccoFileFilter {
		String regexString;
	
		public MatchNameRegExpFileFilter(String regexString) {
			this.regexString = regexString;
		}
		
		public boolean accept(File file) {
			return Pattern.matches(regexString, file.getPath());
		}


		public String toSerializationString() {
			return MatchPathRegExpFileFilterFactory.class.getName() + ":" + regexString; //$NON-NLS-1$
		}

		public String getDescription() {
        	return MessageFormat.format("MatchPathRegExpFileFilterFactory.description", new Object[]{this.regexString}); //$NON-NLS-1$
		}
	}

	public String getDisplayName() {
		return GuiMessages.getString("MatchPathRegExpFileFilterFactory.name"); //$NON-NLS-1$
	}

    public DoccoFileFilter createNewFilter(String filterExpression) {
        return new MatchNameRegExpFileFilter(filterExpression);
    }
}
