/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.filefilter;

import java.io.File;
import java.util.regex.Pattern;


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
			return MatchPathRegExpFileFilterFactory.class.getName() + ":" + regexString;
		}

		public String getDescription() {
			return "Path matches regular expression '" + this.regexString + "'";
		}
	}

	public String getDisplayName() {
		return "Match path with regular expression";
	}

    public DoccoFileFilter createNewFilter(String filterExpression) {
        return new MatchNameRegExpFileFilter(filterExpression);
    }
	
	// @todo quick hack to get combo boxes in UI working -- should be done with cell renderer	
	public String toString() {
		return getDisplayName();
	}
}
