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
import org.tockit.conscript.model.DatabaseDefinition;
import org.tockit.conscript.model.DatabaseDefinitions;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class DatabaseParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "DATABASE";
	}

	public void parse(CSCTokenizer tokenizer, ConceptualFile targetFile) throws IOException, DataFormatException {
        String identifier = tokenizer.popCurrentToken();
        DatabaseDefinitions dbDefinitions = new DatabaseDefinitions(targetFile, identifier);
        
        tokenizer.consumeToken("=", targetFile);
        
        // ignore specials for now
        // @todo add specials
        while(!tokenizer.getCurrentToken().equals("(")) {
            tokenizer.advance();
        }
        tokenizer.consumeToken("(", targetFile);
        String name = tokenizer.popCurrentToken();
        tokenizer.consumeToken(",", targetFile);
        String table = tokenizer.popCurrentToken();
        tokenizer.consumeToken(",", targetFile);
        String primaryKey = tokenizer.popCurrentToken();
        tokenizer.consumeToken(")", targetFile);

        DatabaseDefinition dbDefinition = new DatabaseDefinition(identifier, name, table, primaryKey);
        dbDefinitions.setDatabases(new DatabaseDefinition[] {dbDefinition});
        targetFile.setDatabaseDefinitions(dbDefinitions);
        
        tokenizer.consumeToken(";", targetFile);
	}
}
