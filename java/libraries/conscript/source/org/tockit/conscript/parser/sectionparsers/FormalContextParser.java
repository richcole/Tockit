/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;

import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.FCAAttribute;
import org.tockit.conscript.model.FCAObject;
import org.tockit.conscript.model.FormalContext;
import org.tockit.conscript.model.FormattedString;
import org.tockit.conscript.model.Point;
import org.tockit.conscript.model.StringFormat;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class FormalContextParser extends CSCFileSectionParser {
	public void parse(CSCTokenizer tokenizer, CSCFile file)
	                        throws IOException, DataFormatException {
		String contextId = tokenizer.popCurrentToken();
        FormalContext context = getFormalContext(file, contextId);

        tokenizer.consumeToken("=");
        parseTitleRemarkSpecials(tokenizer, context);

		tokenizer.consumeToken("OBJECTS");

		while (!tokenizer.getCurrentToken().equals("ATTRIBUTES")) {
            long number = Long.parseLong(tokenizer.popCurrentToken());
            String objectId = tokenizer.popCurrentToken();
            FormattedString description = null;
            if(!tokenizer.newLineHasStarted()) {
                String descriptionText = tokenizer.popCurrentToken();
                StringFormat descriptionFormat = null;
                if(!tokenizer.newLineHasStarted()) {
                    descriptionFormat = new StringFormat(tokenizer.popCurrentToken());
                }
                description = new FormattedString(descriptionText, descriptionFormat);
            }
            // @todo the empty point is a hack. How to the objects in the context and 
            //       the ones in the diagram relate to each other? Do we need two types?
            FCAObject object = new FCAObject(new Point(number, Double.MIN_VALUE, 0, null, null), objectId, description);
            context.addObject(object);
		}
		tokenizer.consumeToken("ATTRIBUTES");

		while (!tokenizer.getCurrentToken().equals("RELATION")) {
            long number = Long.parseLong(tokenizer.popCurrentToken());
            String attributeId = tokenizer.popCurrentToken();
            FormattedString description = null;
            if(!tokenizer.newLineHasStarted()) {
                String descriptionText = tokenizer.popCurrentToken();
                StringFormat descriptionFormat = null;
                if(!tokenizer.newLineHasStarted()) {
                    descriptionFormat = new StringFormat(tokenizer.popCurrentToken());
                }
                description = new FormattedString(descriptionText, descriptionFormat);
            }
            // @todo the empty point is a hack. How to the objects in the context and 
            //       the ones in the diagram relate to each other? Do we need two types?
            FCAAttribute attribute = new FCAAttribute(new Point(number, Double.MIN_VALUE, 0, null, null), attributeId, description);
            context.addAttribute(attribute);
		}
        tokenizer.consumeToken("RELATION");
        
        int height = Integer.parseInt(tokenizer.popCurrentToken());
        if(height != context.getObjects().size()) {
            throw new DataFormatException("Relation height (" + height + ") does not match number of objects (" +
                                          context.getObjects().size() +") in context '" +
                                          contextId + "', line " + tokenizer.getCurrentLine() + ", file '" +
                                          file.getLocation() + "'");
        }
        tokenizer.consumeToken(",");
        int width = Integer.parseInt(tokenizer.popCurrentToken());
        if(width != context.getAttributes().size()) {
            throw new DataFormatException("Relation width (" + width + ") does not match number of attributes (" +
                                          context.getAttributes().size() + ") in context '" +
                                          contextId + "', line " + tokenizer.getCurrentLine() + ", file '" +
                                          file.getLocation() + "'");
        }

		Iterator<FCAObject> objIt = context.getObjects().iterator(); // iterate over objects/rows
		while (objIt.hasNext()) {
			FCAObject object = objIt.next();
			String row = tokenizer.getCurrentToken(); // get row string
			if (row.length() == 0) {
				throw new DataFormatException(
					"Missing row in the relation in line "
						+ tokenizer.getCurrentLine());
			}
			if (row.length() < context.getAttributes().size()) {
				throw new DataFormatException(
					"Incomplete row in the relation in line "
						+ tokenizer.getCurrentLine());
			}
			Iterator<FCAAttribute> attrIt = context.getAttributes().iterator(); // iterate over attributes
			int i = 0; // count pos in string
			while (attrIt.hasNext()) {
				FCAAttribute attribute = attrIt.next();
				if (row.charAt(i) == '*') {
					context.setRelationship(object,attribute); // hit --> add to relation
				}
				i++;
			}
			tokenizer.advance(); // next row
		}

        tokenizer.consumeToken(";");

        context.setInitialized();
        CSCParser.logger.log(Level.FINER, "Formal context added: '" + context.getName() + "'");
	}

	public String getStartToken() {
		return "FORMAL_CONTEXT";
	}
}