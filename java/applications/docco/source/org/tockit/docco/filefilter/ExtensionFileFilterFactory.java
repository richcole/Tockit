/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.filefilter;

import java.io.File;
import java.util.StringTokenizer;


public class ExtensionFileFilterFactory implements FileFilterFactory {
	private class ExtensionFileFilter extends DoccoFileFilter {
		String[] extensions;

		public ExtensionFileFilter(String extensionString) {
			StringTokenizer tokenizer = new StringTokenizer(extensionString, " ,;");
			this.extensions = new String[tokenizer.countTokens()];
			int count = 0;
			while(tokenizer.hasMoreTokens()) {
				this.extensions[count] = tokenizer.nextToken();
				count++;
			}
		}

		public boolean accept(File file) {
			String name = file.getName().toLowerCase();
			for (int i = 0; i < this.extensions.length; i++) {
				String extension = this.extensions[i];
				if(name.endsWith("." + extension)) {
					return true;
				}
			}
			return false;
		}

        public String toSerializationString() {
			return ExtensionFileFilterFactory.class.getName() + ":" + getExtensionsString();
        }

        public String getDescription() {
        	if(this.extensions.length == 1) {
				return "Files having extension: " + this.extensions[0];
        	} else {
				return "Files having extensions: " + getExtensionsString();
        	}
        }
        
        private String getExtensionsString() {
        	StringBuffer retVal = new StringBuffer();
        	for (int i = 0; i < this.extensions.length; i++) {
                if(i != 0) {
                	retVal.append(";");
                }
				retVal.append(this.extensions[i]);
            }
            return retVal.toString();
        }
	}

	public DoccoFileFilter createNewFilter(String filterExpression) {
		return new ExtensionFileFilter(filterExpression);
	}

	public String getDisplayName() {
		return "Match file extension";
	}
	
	// @todo quick hack to get combo boxes in UI working -- should be done with cell renderer	
	public String toString() {
		return getDisplayName();
	}
}
