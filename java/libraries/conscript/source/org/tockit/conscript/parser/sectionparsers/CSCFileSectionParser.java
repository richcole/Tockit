/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.IOException;

import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.FormattedString;
import org.tockit.conscript.model.ConscriptStructure;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;


public abstract class CSCFileSectionParser {
	protected static final CSCFileSectionParser[] CSC_FILE_SECTIONS_PARSERS = 
									new CSCFileSectionParser[]{
												new RemarkParser(),
												new FormalContextParser(),
												new LineDiagramParser(),
												new StringMapParser(),
												new IdentifierMapParser(),
												new QueryMapParser(),
												new AbstractScaleParser(),
												new ConcreteScaleParser(),
												new RealisedScaleParser(),
												new DatabaseParser(),
												new ConceptualSchemaParser(),
												new ConceptualFileParser(),
												new IncludeParser()
									}; 

	abstract public String getStartToken();
	
	abstract public void parse(CSCTokenizer tokenizer, CSCFile targetFile) throws IOException, DataFormatException;
	
	public static CSCFileSectionParser[] getParsers() {
		return CSC_FILE_SECTIONS_PARSERS;
	}

    protected void parseTitleRemarkSpecials(CSCTokenizer tokenizer, ConscriptStructure schemaPart) throws IOException, DataFormatException {
        if (tokenizer.getCurrentToken().equals("TITLE")) {
            tokenizer.advance();
            String title = tokenizer.popCurrentToken();
            // @todo parse formatting
            schemaPart.setTitle(new FormattedString(title, null));
        }
        if (tokenizer.getCurrentToken().equals("REMARK")) {
            tokenizer.advance();
            String remark = tokenizer.popCurrentToken();
            schemaPart.setRemark(remark);
        }
        if(tokenizer.getCurrentToken().equals("SPECIAL")) {
            tokenizer.advance();
            do {
                String special = tokenizer.popCurrentToken();
                int colonPos = special.indexOf(':');
                if(colonPos == -1) {
                    // @todo pass file along to enhance error message
                    throw new DataFormatException("Can not parse special '" + special + "')");
                }
                String specialId = special.substring(0, colonPos);
                String specialValue = special.substring(colonPos+1);
                schemaPart.addSpecial(specialId, specialValue);
            } while (tokenizer.currentTokenIsString());
        }
    }

    protected void throwFailedReferenceException(CSCTokenizer tokenizer, CSCFile file, String type, String reference) throws DataFormatException {
        throw new DataFormatException("Can not resolve reference to " + 
                                      type + " '" + reference + "' referenced in file '" +
                                      file.getLocation() + "', line " + tokenizer.getCurrentLine() +".");
    }
}