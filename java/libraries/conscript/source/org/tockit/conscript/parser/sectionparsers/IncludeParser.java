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

import org.tockit.conscript.model.ConceptualFile;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class IncludeParser extends CSCFileSectionParser {
    private static final String[] INCLUDE_DIRS = {"skalen","scales","abstract","concrete"};

    public String getStartToken() {
		return "#INCLUDE";
	}

	public void parse(CSCTokenizer tokenizer, ConceptualFile targetFile) throws IOException, DataFormatException {
        String includeLocation = tokenizer.popCurrentToken();
        tokenizer.consumeToken(";", targetFile);
        URL includeURL = new URL(targetFile.getFile(), includeLocation);
        CSCParser.logger.log(Level.FINER, "Including URL: '" + includeURL + "'");
		ConceptualFile includeFile = null;
        try {
            includeFile = CSCParser.importCSCFile(includeURL);
        } catch (FileNotFoundException e) {
            for (int i = 0; i < INCLUDE_DIRS.length; i++) {
                String dir = INCLUDE_DIRS[i];
                includeURL = new URL(targetFile.getFile(), dir + "/" + includeLocation);
                try {
                    includeFile = CSCParser.importCSCFile(includeURL);
                    break;
                } catch (FileNotFoundException e2) {
                    // ignore. next
                }
            }
        }
        if(includeFile == null) {
            throw new DataFormatException("Can not find include file '" + includeLocation +
                                          "' referenced from file '" + targetFile.getFile() +"'");
        }
        merge(targetFile, includeFile);
	}

    private void merge(ConceptualFile targetFile, ConceptualFile includeFile) {
        // @todo should merge here
        System.out.println("to merge:\n" + includeFile.getFile());
    }
}
