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
import org.tockit.conscript.model.ConceptualSchema;
import org.tockit.conscript.model.ConcreteScale;
import org.tockit.conscript.model.DatabaseDefinition;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class ConceptualSchemaParser extends CSCFileSectionParser {
	@Override
	public String getStartToken() {
		return "CONCEPTUAL_SCHEME";
	}

	@Override
	public void parse(CSCTokenizer tokenizer, CSCFile file) throws IOException, DataFormatException {
        String name = tokenizer.popCurrentToken();
        tokenizer.consumeToken("=");
        ConceptualSchema schema = getConceptualSchema(file, name);

        parseTitleRemarkSpecials(tokenizer, schema);
        
        tokenizer.consumeToken("(");
        
        String dbIdentifier = tokenizer.popCurrentToken();
        DatabaseDefinition databaseDefinition = getDatabaseDefinition(file, dbIdentifier);
        schema.setDatabase(databaseDefinition);
        
        do {
            tokenizer.advance();
            String scaleId = tokenizer.popCurrentToken();
            ConcreteScale scale = getConcreteScale(file, scaleId);
            schema.addConcreteScale(scale);
        } while(tokenizer.getCurrentToken().equals(","));
        
        tokenizer.consumeToken(")");
        tokenizer.consumeToken(";");

        schema.setInitialized();
        CSCParser.logger.log(Level.FINER, "Conceptual schema added: '" + schema.getName() + "'");
	}
}
