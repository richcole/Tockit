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
import org.tockit.conscript.model.FormalContext;
import org.tockit.conscript.model.LineDiagram;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class AbstractScaleParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "ABSTRACT_SCALE";
	}

	public void parse(CSCTokenizer tokenizer, CSCFile file) throws IOException, DataFormatException {
        String name = tokenizer.popCurrentToken();
        AbstractScale scale = getAbstractScale(file, name);
        
        tokenizer.consumeToken("=");
        
        parseTitleRemarkSpecials(tokenizer, scale);
        tokenizer.consumeToken("(");
        
        String contextId = tokenizer.popCurrentToken();
        tokenizer.consumeToken(",");
        
        FormalContext context = getFormalContext(file, contextId);
        scale.setContext(context);
        
        if(!tokenizer.getCurrentToken().equals(",")) {
            tokenizer.advance(); // ignore lattice id
        }
        
        do {
            tokenizer.consumeToken(",");
            String diagramId = tokenizer.popCurrentToken();
            LineDiagram diagram = getLineDiagram(file, diagramId);
            scale.addLineDiagram(diagram);
        } while(!tokenizer.getCurrentToken().equals(")"));
        
        tokenizer.consumeToken(")");
        tokenizer.consumeToken(";");

        scale.setInitialized();
        file.add(scale);
        CSCParser.logger.log(Level.FINER, "Abstract scale added: '" + scale.getName() + "'");
	}
}
