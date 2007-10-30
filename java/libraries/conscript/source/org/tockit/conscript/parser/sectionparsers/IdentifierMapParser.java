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
import org.tockit.conscript.model.IdentifierMap;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class IdentifierMapParser extends CSCFileSectionParser {
	@Override
	public String getStartToken() {
		return "IDENTIFIER_MAP";
	}

	@Override
	public void parse(CSCTokenizer tokenizer, CSCFile file) throws IOException, DataFormatException {
        String id = tokenizer.popCurrentToken();
        IdentifierMap map = getIdentifierMap(file, id);
        tokenizer.consumeToken("=");
        
        if(tokenizer.getCurrentToken().equals("REMARK")) {
            tokenizer.consumeToken("REMARK");
            map.setRemark(tokenizer.popCurrentToken());
        }
        
        while(!tokenizer.getCurrentToken().equals(";")) {
            tokenizer.consumeToken("(");
            String from = tokenizer.popCurrentToken();
            tokenizer.consumeToken(",");
            String to = tokenizer.popCurrentToken();
            tokenizer.consumeToken(")");
            map.addEntry(from, to);
        }
        tokenizer.consumeToken(";");

        map.setInitialized();
        CSCParser.logger.log(Level.FINER, "Identifier map added: '" + map.getName() + "'");
	}
}
