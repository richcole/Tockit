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

import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.ConceptualSchema;
import org.tockit.conscript.model.ConcreteScale;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class ConceptualSchemaParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "CONCEPTUAL_SCHEME";
	}

	public void parse(CSCTokenizer tokenizer, CSCFile file) throws IOException, DataFormatException {
        String identifier = tokenizer.popCurrentToken();
        tokenizer.consumeToken("=", file);
        ConceptualSchema schema = new ConceptualSchema(file, identifier);
        parseTitleRemarkSpecials(tokenizer, schema);
        tokenizer.consumeToken("(", file);
        String dbIdentifier = tokenizer.popCurrentToken();
        schema.setDatabase(file.findDatabaseDefinition(dbIdentifier));
        if(schema.getDatabase() == null) {
            // @todo support reverse lookup
            throw new DataFormatException("Could not find database with name '" + dbIdentifier + "' -- reverse lookup not yet supported");
        }
        List scales = new ArrayList();
        while(tokenizer.getCurrentToken().equals(",")) {
            tokenizer.advance();
            String scaleId = tokenizer.popCurrentToken();
            ConcreteScale scale = findScale(file, scaleId);
            if(scale == null) {
                scale = new ConcreteScale(file, scaleId);
            }
            scales.add(scale);
        }
        schema.setConcreteScales((ConcreteScale[]) scales.toArray(new ConcreteScale[scales.size()]));
        tokenizer.consumeToken(")", file);
        tokenizer.consumeToken(";", file);
	}

    private ConcreteScale findScale(CSCFile targetFile, String scaleId) {
        return null;
    }
}
