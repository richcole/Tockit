/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.IOException;

import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.DatabaseDefinition;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class DatabaseParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "DATABASE";
	}

	public void parse(CSCTokenizer tokenizer, CSCFile file) throws IOException, DataFormatException {
        String name = tokenizer.popCurrentToken();
        tokenizer.consumeToken("=", file);

        DatabaseDefinition dbDefinition = new DatabaseDefinition(file, name);
        parseTitleRemarkSpecials(tokenizer, dbDefinition);
        
        tokenizer.consumeToken("(", file);
        dbDefinition.setDatabaseName(tokenizer.popCurrentToken());
        tokenizer.consumeToken(",", file);
        dbDefinition.setTable(tokenizer.popCurrentToken());
        tokenizer.consumeToken(",", file);
        dbDefinition.setPrimaryKey(tokenizer.popCurrentToken());
        tokenizer.consumeToken(")", file);

        file.add(dbDefinition);
        
        tokenizer.consumeToken(";", file);
	}
}
