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

import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class FormalContextParser extends CSCFileSectionParser {
	public Object parse(CSCTokenizer tokenizer)
		throws IOException, DataFormatException {
		List objects = new ArrayList();
		List attributes = new ArrayList();
		String contextTitle = tokenizer.getCurrentToken();
		CSCParser.sectionIdMap.put(contextTitle, contextTitle);

		while (!tokenizer.getCurrentToken().equals("OBJECTS")) {
			// ignore everything before the objects
			if (tokenizer.getCurrentToken().equals("TITLE")) {
				tokenizer.advance();
				CSCParser.sectionIdMap.put(
					tokenizer.getCurrentToken(),
					contextTitle);
				contextTitle = tokenizer.getCurrentToken();
			}
			tokenizer.advance();
		}
		tokenizer.advance(); // skip "OBJECTS"

		//            ContextImplementation context = new ContextImplementation(contextTitle);

		while (!tokenizer.getCurrentToken().equals("ATTRIBUTES")) {
			// find objects until attributes come
			tokenizer.advance(); // skip number
			tokenizer.advance(); // skip id
			objects.add(tokenizer.getCurrentToken()); // use name
			tokenizer.advance(); // next
		}
		tokenizer.advance(); // skip "ATTRIBUTES"
		//            context.getObjects().addAll(objects); // we have all objects

		while (!tokenizer.getCurrentToken().equals("RELATION")) {
			// find attributes until relation comes
			tokenizer.advance(); // skip number
			tokenizer.advance(); // skip id
			//                attributes.add(new Attribute(tokenizer.getCurrentToken(), null)); // use name
			tokenizer.advance(); // next
		}
		tokenizer.advance(); // skip "RELATION"
		tokenizer.advance(); // skip size ...
		tokenizer.advance(); // ... both parts of it
		//            context.getAttributes().addAll(attributes); // we have all attributes

		//            BinaryRelationImplementation relation = context.getRelationImplementation();

		// create relation
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
					//                        relation.insert(object,attribute); // hit --> add to relation
				}
				i++;
			}
			tokenizer.advance(); // next row
		}

		consumeToken(tokenizer, ";");

		return null;
	}

	public String getStartToken() {
		return "FORMAL_CONTEXT";
	}
}