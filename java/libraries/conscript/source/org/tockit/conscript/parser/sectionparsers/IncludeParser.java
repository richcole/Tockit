/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class IncludeParser extends CSCFileSectionParser {
    private static final String[] INCLUDE_DIRS = {"skalen","scales","abstract","concrete"};

    @Override
	public String getStartToken() {
		return "#INCLUDE";
	}

	@Override
	public void parse(CSCTokenizer tokenizer, CSCFile file) throws IOException, DataFormatException {
        String includeLocation = tokenizer.popCurrentToken();
        tokenizer.consumeToken(";");
        URL includeURL = new URL(file.getLocation(), includeLocation);
        boolean includeOk = false;
        try {
            if(!file.hasInclude(includeURL)) {
                CSCParser.logger.log(Level.FINER, "Trying to include URL: '" + includeURL + "'");
                CSCParser.importCSCFile(includeURL, file);
            }
            includeOk = true;
        } catch (FileNotFoundException e) {
            for (int i = 0; i < INCLUDE_DIRS.length; i++) {
                String dir = INCLUDE_DIRS[i];
                includeURL = new URL(file.getLocation(), dir + "/" + includeLocation);
                try {
                    if(!file.hasInclude(includeURL)) {
                        CSCParser.logger.log(Level.FINER, "Trying to include URL: '" + includeURL + "'");
                        CSCParser.importCSCFile(includeURL, file);
                    }
                    includeOk = true;
                    break;
                } catch (FileNotFoundException e2) {
                    // ignore. next
                }
            }
        }
        if(!includeOk) {
            throw new DataFormatException("Can not find include file '" + includeLocation +
                                          "' referenced from file '" + file.getLocation() +"'");
        }
        CSCParser.logger.log(Level.FINER, "File included: '" + includeURL + "'");
	}
}
