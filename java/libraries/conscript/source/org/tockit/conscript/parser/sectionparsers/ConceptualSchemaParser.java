/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tockit.conscript.model.ConceptualFile;
import org.tockit.conscript.model.ConceptualSchema;
import org.tockit.conscript.model.ConcreteScale;
import org.tockit.conscript.model.DatabaseDefinition;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class ConceptualSchemaParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "CONCEPTUAL_SCHEME";
	}

	public void parse(CSCTokenizer tokenizer, ConceptualFile targetFile) throws IOException, DataFormatException {
        String identifier = tokenizer.popCurrentToken();
        tokenizer.consumeToken("=", targetFile);
        ConceptualSchema schema = new ConceptualSchema(targetFile, identifier);
        parseTitleRemarkSpecials(tokenizer, schema);
        tokenizer.consumeToken("(", targetFile);
        String dbIdentifier = tokenizer.popCurrentToken();
        DatabaseDefinition[] dbDefs = targetFile.getDatabaseDefinitions().getDatabases();
        for (int i = 0; i < dbDefs.length; i++) {
            DatabaseDefinition definition = dbDefs[i];
            if(definition.getIdentifier().equals(dbIdentifier)) {
                schema.setDatabase(definition);
                break;
            }
        }
        if(schema.getDatabase() == null) {
            // @todo support reverse lookup
            throw new DataFormatException("Could not find database with name '" + dbIdentifier + "' -- reverse lookup not yet supported");
        }
        List scales = new ArrayList();
        while(tokenizer.getCurrentToken().equals(",")) {
            tokenizer.advance();
            String scaleId = tokenizer.popCurrentToken();
            ConcreteScale scale = findScale(targetFile, scaleId);
            if(scale == null) {
                scale = new ConcreteScale(targetFile, scaleId);
            }
            scales.add(scale);
        }
        schema.setConcreteScales((ConcreteScale[]) scales.toArray(new ConcreteScale[scales.size()]));
        tokenizer.consumeToken(")", targetFile);
        tokenizer.consumeToken(";", targetFile);
	}

    private ConcreteScale findScale(ConceptualFile targetFile, String scaleId) {
        return null;
    }
}
