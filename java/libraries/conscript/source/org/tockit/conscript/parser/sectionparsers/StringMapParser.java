/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.IOException;

import org.tockit.conscript.model.ConceptualFile;
import org.tockit.conscript.model.StringMap;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class StringMapParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "STRING_MAP";
	}

	public void parse(CSCTokenizer tokenizer, ConceptualFile targetFile) throws IOException, DataFormatException {
        String id = tokenizer.popCurrentToken();
        StringMap map = new StringMap(targetFile, id);
        tokenizer.consumeToken("=", targetFile);
        while(!tokenizer.getCurrentToken().equals(";")) {
            tokenizer.consumeToken("(", targetFile);
            String concreteObject = tokenizer.popCurrentToken();
            tokenizer.consumeToken(",", targetFile);
            String abstractObjectId = tokenizer.popCurrentToken();
            tokenizer.consumeToken(")", targetFile);
            map.addEntry(concreteObject, abstractObjectId);
        }
        tokenizer.consumeToken(";", targetFile);
	}
}
