/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.tockit.conscript.model.*;
import org.tockit.conscript.parser.sectionparsers.*;

public class CSCParser {
    public static final Logger logger = Logger.getLogger(CSCParser.class.getName());
	
    public static CSCFile importCSCFile(URL mainInput, CSCFile parent) throws FileNotFoundException, DataFormatException {
        try {
        	CSCFile mainFile = new CSCFile(mainInput, parent);
            CSCTokenizer tokenizer = new CSCTokenizer(mainInput);
            
            CSCFileSectionParser currentSectionParser = null;
            while(! tokenizer.done()) {
            	CSCFileSectionParser newSectionParser = identifySectionParser(tokenizer);
            	if(newSectionParser != null) {
            		currentSectionParser = newSectionParser;
            	}
				if(currentSectionParser == null) {
					// first round and we don't grok it
					throw new DataFormatException("The specified file is not a CSC file.");
				}
          		currentSectionParser.parse(tokenizer, mainFile);
            }
        
            mainFile.checkForInitialization();
            
			return mainFile;
        } catch (FileNotFoundException e) {
            throw e; // we keep the file not found ones
        } catch (IOException e) {
            throw new DataFormatException("Error reading input file '" + mainInput + "'", e);
        }
    }
    
    protected static CSCFileSectionParser identifySectionParser(CSCTokenizer tokenizer) throws IOException, DataFormatException {
    	CSCFileSectionParser[] parsers = CSCFileSectionParser.getParsers();
		for (int i = 0; i < parsers.length; i++) {
            CSCFileSectionParser sectionType = parsers[i];
            if(sectionType.getStartToken().equals(tokenizer.getCurrentToken())) {
            	tokenizer.advance();
            	return sectionType;
            }
        }
        return null;
    }
    
    /**
     * Main method for testing.
     */
    public static void main(String[] args) throws FileNotFoundException, MalformedURLException, DataFormatException {
		//logger.setLevel(java.util.logging.Level.FINER);
		File inputFile = new File(args[0]);
		CSCFile result = importCSCFile(inputFile.toURL(), null);
        result.printCSC(System.out);
    }
}
