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
import java.util.Iterator;
import java.util.List;

import org.tockit.conscript.model.BinaryRelationImplementation;
import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.FormalContext;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class FormalContextParser extends CSCFileSectionParser {
	public void parse(CSCTokenizer tokenizer, CSCFile targetFile)
		throws IOException, DataFormatException {
		List objects = new ArrayList();
		List attributes = new ArrayList();
		String contextId = tokenizer.popCurrentToken();
        FormalContext context = new FormalContext(targetFile, contextId);

        parseTitleRemarkSpecials(tokenizer, context);
        
		while (!tokenizer.getCurrentToken().equals("OBJECTS")) {
			tokenizer.advance();
		}
		tokenizer.consumeToken("OBJECTS", targetFile);

		while (!tokenizer.getCurrentToken().equals("ATTRIBUTES")) {
			// find objects until attributes come
            
			tokenizer.advance(); // skip number
			tokenizer.advance(); // skip id
			objects.add(tokenizer.getCurrentToken()); // use name
			tokenizer.advance(); // next
		}
		tokenizer.consumeToken("ATTRIBUTES", targetFile);

		while (!tokenizer.getCurrentToken().equals("RELATION")) {
			// find attributes until relation comes
			tokenizer.advance(); // skip number
			tokenizer.advance(); // skip id
            attributes.add(tokenizer.getCurrentToken()); // use name
			tokenizer.advance(); // next
		}
        tokenizer.consumeToken("RELATION", targetFile);
        
        int height = Integer.parseInt(tokenizer.popCurrentToken());
        if(height != objects.size()) {
            throw new DataFormatException("Relation height does not match number of objects in context '" +
                                          contextId + "', line " + tokenizer.getCurrentLine() + ", file '" +
                                          targetFile.getLocation() + "'");
        }
        tokenizer.consumeToken(",", targetFile);
        int width = Integer.parseInt(tokenizer.popCurrentToken());
        if(width != attributes.size()) {
            throw new DataFormatException("Relation width does not match number of attributes in context '" +
                                          contextId + "', line " + tokenizer.getCurrentLine() + ", file '" +
                                          targetFile.getLocation() + "'");
        }

		// create relation
        BinaryRelationImplementation relation = new BinaryRelationImplementation();
		Iterator objIt = objects.iterator(); // iterate over objects/rows
		while (objIt.hasNext()) {
			Object object = objIt.next();
			String row = tokenizer.getCurrentToken(); // get row string
			if (row.length() == 0) {
				throw new DataFormatException(
					"Missing row in the relation in line "
						+ tokenizer.getCurrentLine());
			}
			if (row.length() < attributes.size()) {
				throw new DataFormatException(
					"Incomplete row in the relation in line "
						+ tokenizer.getCurrentLine());
			}
			Iterator attrIt = attributes.iterator(); // iterate over attributes
			int i = 0; // count pos in string
			while (attrIt.hasNext()) {
				Object attribute = attrIt.next();
				if (row.charAt(i) == '*') {
					relation.insert(object,attribute); // hit --> add to relation
				}
				i++;
			}
			tokenizer.advance(); // next row
		}

        tokenizer.consumeToken(";", targetFile);
	}

	public String getStartToken() {
		return "FORMAL_CONTEXT";
	}
}