/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.IOException;

import org.tockit.conscript.model.ConceptualFile;
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
	
	abstract public void parse(CSCTokenizer tokenizer, ConceptualFile targetFile) throws IOException, DataFormatException;
	
	public static CSCFileSectionParser[] getParsers() {
		return CSC_FILE_SECTIONS_PARSERS;
	}
}