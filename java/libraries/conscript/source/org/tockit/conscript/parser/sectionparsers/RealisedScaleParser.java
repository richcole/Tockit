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
import org.tockit.conscript.model.ConcreteScale;
import org.tockit.conscript.model.IdentifierMap;
import org.tockit.conscript.model.RealisedScale;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class RealisedScaleParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "REALISED_SCALE";
	}

	public void parse(CSCTokenizer tokenizer, CSCFile file) throws IOException, DataFormatException {
        String name = tokenizer.popCurrentToken();
        RealisedScale scale = getRealisedScale(file, name);
        
        tokenizer.consumeToken("=");
        
        parseTitleRemarkSpecials(tokenizer, scale);
        
        if(tokenizer.getCurrentToken().equals("TABLES") || tokenizer.getCurrentToken().equals("FIELDS")) {
            // @todo parse these bits
            CSCParser.logger.log(Level.WARNING, "TABLES and FIELDS in CONCRETE_SCALE are ignored");
        }

        while(!tokenizer.getCurrentToken().equals("(")) {
            tokenizer.advance();
        }
        
        tokenizer.consumeToken("(");
        
        String concreteScaleId = tokenizer.popCurrentToken();
        ConcreteScale concreteScale = file.findConcreteScale(concreteScaleId);
        scale.setConcreteScale(concreteScale);
        
        tokenizer.consumeToken(",");
        
        String identifierMapId = tokenizer.popCurrentToken();
        IdentifierMap identiferMap = file.findIdentifierMap(identifierMapId);
        scale.setIdentifierMap(identiferMap);
        
        tokenizer.consumeToken(")");
        
        tokenizer.consumeToken(";");

        scale.setInitialized();
        file.add(scale);
        CSCParser.logger.log(Level.FINER, "Realised scale added: '" + scale.getName() + "'");
	}
}
