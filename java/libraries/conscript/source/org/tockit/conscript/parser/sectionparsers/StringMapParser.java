/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.IOException;
import java.util.logging.Level;

import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.StringMap;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class StringMapParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "STRING_MAP";
	}

	public void parse(CSCTokenizer tokenizer, CSCFile file) throws IOException, DataFormatException {
        String id = tokenizer.popCurrentToken();
        StringMap map = new StringMap(file, id);
        tokenizer.consumeToken("=", file);
        while(!tokenizer.getCurrentToken().equals(";")) {
            tokenizer.consumeToken("(", file);
            String concreteObject = tokenizer.popCurrentToken();
            tokenizer.consumeToken(",", file);
            String abstractObjectId = tokenizer.popCurrentToken();
            tokenizer.consumeToken(")", file);
            map.addEntry(concreteObject, abstractObjectId);
        }
        tokenizer.consumeToken(";", file);
        
        file.add(map);
        CSCParser.logger.log(Level.FINER, "String map added: '" + map.getName() + "'");
	}
}
