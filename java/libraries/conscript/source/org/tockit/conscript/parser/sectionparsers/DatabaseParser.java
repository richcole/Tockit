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
import org.tockit.conscript.model.DatabaseDefinition;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class DatabaseParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "DATABASE";
	}

	public void parse(CSCTokenizer tokenizer, CSCFile file) throws IOException, DataFormatException {
        String name = tokenizer.popCurrentToken();
        tokenizer.consumeToken("=");

        DatabaseDefinition dbDefinition = getDatabaseDefinition(file, name);
        parseTitleRemarkSpecials(tokenizer, dbDefinition);
        
        tokenizer.consumeToken("(");
        dbDefinition.setDatabaseName(tokenizer.popCurrentToken());
        tokenizer.consumeToken(",");
        dbDefinition.setTable(tokenizer.popCurrentToken());
        tokenizer.consumeToken(",");
        dbDefinition.setPrimaryKey(tokenizer.popCurrentToken());
        tokenizer.consumeToken(")");

        tokenizer.consumeToken(";");

        dbDefinition.setInitialized();
        file.add(dbDefinition);
        CSCParser.logger.log(Level.FINER, "Database definition added: " + dbDefinition.getName() + "'");
	}
}
