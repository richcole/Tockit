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
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tockit.conscript.model.*;
import org.tockit.conscript.parser.sectionparsers.*;

public class CSCParser {
    public static Logger logger = Logger.getLogger(CSCParser.class.getName());
	
    public static ConceptualFile importCSCFile(URL mainInput) throws FileNotFoundException, DataFormatException {
        try {
        	ConceptualFile mainFile = new ConceptualFile(mainInput,"mainFile", new FormattedString("",new StringFormat()),
        												 mainInput.toString(), null, null, null);
            CSCTokenizer tokenizer = new CSCTokenizer(new InputStreamReader(mainInput.openStream()));
            
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
            	try {
            		currentSectionParser.parse(tokenizer, mainFile);
            	} catch (SectionTypeNotSupportedException e) {
            		System.err.println(e.getMessage());
            		// eat a whole section
            		while(!tokenizer.getCurrentToken().equals(";")) {
            			tokenizer.advance();
            		}
            		tokenizer.advance();
            	}
            }
        
			return mainFile;
        } catch (IOException e) {
            throw new DataFormatException("Error reading input file", e);
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
		logger.setLevel(Level.ALL);
		File inputFile = new File(args[0]);
		importCSCFile(inputFile.toURL());    	
    }
}
