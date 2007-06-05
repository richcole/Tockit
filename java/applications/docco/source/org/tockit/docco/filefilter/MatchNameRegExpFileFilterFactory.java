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


public class MatchNameRegExpFileFilterFactory implements FileFilterFactory {
	private class MatchNameRegExpFileFilter extends DoccoFileFilter {
		String regexString;

		public MatchNameRegExpFileFilter(String regexString) {
			this.regexString = regexString;
		}
	
		public boolean accept(File file) {
			return Pattern.matches(regexString, file.getName());
		}

        public String toSerializationString() {
            return MatchNameRegExpFileFilterFactory.class.getName() + ":" + regexString; //$NON-NLS-1$
        }

        public String getDescription() {
        	return MessageFormat.format(GuiMessages.getString("MatchNameRegExpFileFilterFactory.description"), new Object[]{this.regexString}); //$NON-NLS-1$
        }
	}

	public String getDisplayName() {
		return GuiMessages.getString("MatchNameRegExpFileFilterFactory.name"); //$NON-NLS-1$
	}

    public DoccoFileFilter createNewFilter(String filterExpression) {
        return new MatchNameRegExpFileFilter(filterExpression);
    }
}
