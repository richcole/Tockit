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
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class RemarkParser extends CSCFileSectionParser {
	@Override
	public String getStartToken() {
		return "REMARK";
	}

	@Override
	public void parse(CSCTokenizer tokenizer, CSCFile file) throws IOException, DataFormatException {
        String remark = tokenizer.popCurrentToken();
        file.addRemark(remark);
        
        tokenizer.consumeToken(";");
        
        CSCParser.logger.log(Level.FINER, "Remark added: " + remark + "'");
	}
}
