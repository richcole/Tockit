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

import org.tockit.conscript.model.AbstractScale;
import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.ConcreteScale;
import org.tockit.conscript.model.QueryMap;
import org.tockit.conscript.model.StringMap;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class ConcreteScaleParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "CONCRETE_SCALE";
	}

	public void parse(CSCTokenizer tokenizer, CSCFile file) throws IOException, DataFormatException {
        String name = tokenizer.popCurrentToken();
        ConcreteScale scale = getConcreteScale(file, name);
        
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
        
        String abstractScaleId = tokenizer.popCurrentToken();
        AbstractScale abstractScale = file.findAbstractScale(abstractScaleId);
        scale.setAbstractScale(abstractScale);
        
        tokenizer.consumeToken(",");
        
        if(!tokenizer.getCurrentToken().equals(",")) {
            String queryMapId = tokenizer.popCurrentToken();
            QueryMap queryMap = file.findQueryMap(queryMapId);
            scale.setQueryMap(queryMap);
        }
        
        tokenizer.consumeToken(",");
        
        String stringMapId = tokenizer.popCurrentToken();
        StringMap attributeMap = file.findStringMap(stringMapId);
        scale.setAttributeMap(attributeMap);
        
        tokenizer.consumeToken(")");
        
        tokenizer.consumeToken(";");

        scale.setInitialized();
        file.add(scale);
        CSCParser.logger.log(Level.FINER, "Concrete scale added: '" + scale.getName() + "'");
	}
}
