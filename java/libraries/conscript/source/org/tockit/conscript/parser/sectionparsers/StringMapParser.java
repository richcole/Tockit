/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.IOException;

import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class StringMapParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "STRING_MAP";
	}

	public Object parse(CSCTokenizer tokenizer) throws IOException, DataFormatException {
		throw new SectionTypeNotSupportedException("parse() in " + this.getClass().getName() + " not yet implemented.");
	}
}