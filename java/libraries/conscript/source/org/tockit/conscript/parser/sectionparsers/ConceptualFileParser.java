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
import org.tockit.conscript.model.ConceptualFile;
import org.tockit.conscript.model.RealisedScale;
import org.tockit.conscript.model.StringMap;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class ConceptualFileParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "CONCEPTUAL_FILE";
	}

	public void parse(CSCTokenizer tokenizer, CSCFile file) throws IOException, DataFormatException {
        String fileId = tokenizer.popCurrentToken();
        tokenizer.consumeToken("=");
        ConceptualFile conceptualFile = getConceptualFile(file, fileId);
        
        parseTitleRemarkSpecials(tokenizer, conceptualFile);
        
        tokenizer.consumeToken("(");
        
        String objectMapId = tokenizer.popCurrentToken();
        StringMap objectMap = getStringMap(file, objectMapId);
        conceptualFile.setObjectMap(objectMap);
        
        tokenizer.consumeToken(",");
        
        do {
            tokenizer.consumeToken(",");
            String scaleId = tokenizer.popCurrentToken();
            RealisedScale scale = getRealisedScale(file, scaleId);
            conceptualFile.addRealisedScale(scale);
        } while( tokenizer.getCurrentToken().equals(","));
        
        tokenizer.consumeToken(")");
        tokenizer.consumeToken(";");
        
        conceptualFile.setInitialized();
        CSCParser.logger.log(Level.FINER, "Conceptual file added: '" + conceptualFile.getName() + "'");
	}
}
