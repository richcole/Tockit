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

import org.tockit.conscript.model.AbstractScale;
import org.tockit.conscript.model.ConceptualFile;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class AbstractScaleParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "ABSTRACT_SCALE";
	}

	public void parse(CSCTokenizer tokenizer, ConceptualFile file) throws IOException, DataFormatException {
        String name = tokenizer.popCurrentToken();
        AbstractScale scale = new AbstractScale(file, name);
        
        tokenizer.consumeToken("=", file);
        
        parseTitleRemarkSpecials(tokenizer, scale);
        tokenizer.consumeToken("(", file);
        
        String contextId = tokenizer.popCurrentToken();
        tokenizer.consumeToken(",", file);
        
        String latticeId = null;
        if(!tokenizer.getCurrentToken().equals(",")) {
            latticeId = tokenizer.popCurrentToken();
        }
        
        List diagramIds = new ArrayList();
        do {
            tokenizer.consumeToken(",", file);
            String diagramId = tokenizer.popCurrentToken();
        } while(!tokenizer.getCurrentToken().equals(")"));
        
        tokenizer.consumeToken(")", file);
        tokenizer.consumeToken(";", file);
	}
}
