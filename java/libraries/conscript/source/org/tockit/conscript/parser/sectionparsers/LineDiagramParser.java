/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.awt.geom.Point2D;
import java.io.IOException;

import org.tockit.conscript.model.ConceptualFile;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class LineDiagramParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "LINE_DIAGRAM";
	}

	public void parse(CSCTokenizer tokenizer, ConceptualFile targetFile)
		throws IOException, DataFormatException {
		//        	SimpleLineDiagram diagram = new SimpleLineDiagram();

		String identifier = tokenizer.getCurrentToken();
		tokenizer.advance();

		consumeToken(tokenizer, "=");

		if (tokenizer.getCurrentToken().equals("TITLE")) {
			tokenizer.advance();
			String title = tokenizer.getCurrentToken();
			tokenizer.advance();
			if (!title.equals("")) {
				identifier = title;
			}
		}
		//        	diagram.setTitle(identifier);
//		CSCParser.sectionIdMap.put(identifier, identifier);

		while (!tokenizer.getCurrentToken().equals("POINTS")) {
			// we ignore remark, special list and unitlength
			tokenizer.advance();
		}
		tokenizer.advance();

		while (!tokenizer.getCurrentToken().equals("LINES")) {
			String id = tokenizer.getCurrentToken();
			tokenizer.advance();

			double x = Double.parseDouble(tokenizer.getCurrentToken());
			tokenizer.advance();
			double y = Double.parseDouble(tokenizer.getCurrentToken());
			tokenizer.advance();
			Point2D position = new Point2D.Double(x, y);

			//            	DiagramNode node = new DiagramNode(diagram, id, position, new ConceptImplementation(), null, null, null);
			//            	diagram.addNode(node);
		}
		tokenizer.advance();

		while (!tokenizer.getCurrentToken().equals("OBJECTS")) {
			String linecode = tokenizer.getCurrentToken();

			String from = linecode.substring(1, linecode.indexOf(','));
			String to =
				linecode.substring(
					linecode.indexOf(',') + 1,
					linecode.length() - 1);

			while (to.startsWith(" ")) {
				to = to.substring(1);
			}

			//                DiagramNode fromNode = diagram.getNode(from);
			//                DiagramNode toNode = diagram.getNode(to);
			//                diagram.addLine(fromNode, toNode);
			//                
			//                ConceptImplementation fromConcept = (ConceptImplementation) fromNode.getConcept();
			//                ConceptImplementation toConcept = (ConceptImplementation) toNode.getConcept();
			//            	fromConcept.addSubConcept(toNode.getConcept());
			//            	toConcept.addSuperConcept(fromConcept);

			tokenizer.advance();
		}
		tokenizer.advance();

		while (!tokenizer.getCurrentToken().equals("ATTRIBUTES")) {
			//            	DiagramNode node = diagram.getNode(tokenizer.getCurrentToken());
			tokenizer.advance();

			tokenizer.advance(); // ignore id of object

			String content = tokenizer.getCurrentToken();
			tokenizer.advance();

			//                LabelInfo labelInfo;
			if (!tokenizer.newLineHasStarted()) {
				// still on the same line --> we have a formatting string
				String formattingString = tokenizer.getCurrentToken();
				tokenizer.advance();

				//                	labelInfo = parseLabelInfo(formattingString);
			} else {
				//                	labelInfo = new LabelInfo();
			}

			//                ConceptImplementation concept = (ConceptImplementation) node.getConcept();
			//                concept.addObject(content);

			//                node.setObjectLabelInfo(labelInfo);
		}
		tokenizer.advance();

		while (!tokenizer.getCurrentToken().equals("CONCEPTS")) {
			//                DiagramNode node = diagram.getNode(tokenizer.getCurrentToken());
			tokenizer.advance();

			tokenizer.advance(); // ignore id of attribute

			String content = tokenizer.getCurrentToken();
			tokenizer.advance();

			//                LabelInfo labelInfo;
			if (!tokenizer.newLineHasStarted()) {
				// still on the same line --> we have a formatting string
				String formattingString = tokenizer.getCurrentToken();
				tokenizer.advance();

				//                    labelInfo = parseLabelInfo(formattingString);
			} else {
				//                    labelInfo = new LabelInfo();
			}

			//                ConceptImplementation concept = (ConceptImplementation) node.getConcept();
			//                concept.addAttribute(new Attribute(content));
			//                node.setAttributeLabelInfo(labelInfo);
		}
		tokenizer.advance();

		while (!tokenizer.getCurrentToken().equals(";")) {
			// we ignore the concept definitions 
			tokenizer.advance();
		}
		consumeToken(tokenizer, ";");
	}

	private String[] extractFormattingStringSegment(String formattingString) {
		if (formattingString.length() == 0) {
			return new String[] { null, "" };
		}
		String segment, rest;
		if (formattingString.startsWith("(")) {
			int parPos = formattingString.indexOf(')');
			segment = formattingString.substring(1, parPos);
			rest = formattingString.substring(parPos + 1);
			int commaPos = rest.indexOf(',');
			if (commaPos != -1) {
				rest = rest.substring(commaPos + 1);
			}
		} else {
			int commaPos = formattingString.indexOf(',');
			if (commaPos == -1) {
				segment = new String(formattingString);
				rest = "";
			} else {
				segment = formattingString.substring(0, commaPos);
				rest = formattingString.substring(commaPos + 1);
			}
		}
		return new String[] { segment, rest };
	}

	private Object parseLabelInfo(String formattingString) {
		//        	LabelInfo retVal = new LabelInfo();
		String[] nextSplit = extractFormattingStringSegment(formattingString);
		String fontName = nextSplit[0];
		formattingString = nextSplit[1];
		nextSplit = extractFormattingStringSegment(formattingString);
		String fontStyleString = nextSplit[0];
		formattingString = nextSplit[1];
		nextSplit = extractFormattingStringSegment(formattingString);
		String fontColorString = nextSplit[0];
		formattingString = nextSplit[1];
		nextSplit = extractFormattingStringSegment(formattingString);
		String fontSizeString = nextSplit[0];
		formattingString = nextSplit[1];
		nextSplit = extractFormattingStringSegment(formattingString);
		String offsetString = nextSplit[0];
		formattingString = nextSplit[1];
		nextSplit = extractFormattingStringSegment(formattingString);
		String alignmentString = nextSplit[0];
		formattingString = nextSplit[1];
		nextSplit = extractFormattingStringSegment(formattingString);
		String clipBoxString = nextSplit[0];
		formattingString = nextSplit[1];
		nextSplit = extractFormattingStringSegment(formattingString);

		// we support only offset and alignment at the moment
		if (offsetString != null && offsetString.length() != 0) {
			int commaPos = offsetString.indexOf(',');
			String xPart = offsetString.substring(0, commaPos);
			String yPart = offsetString.substring(commaPos + 1);
			//				retVal.setOffset(Double.parseDouble(xPart), -Double.parseDouble(yPart));
		}

		if (alignmentString.indexOf('l') != -1) {
			//				retVal.setTextAlignment(LabelInfo.ALIGNLEFT);
		} else if (alignmentString.indexOf('r') != -1) {
			//                retVal.setTextAlignment(LabelInfo.ALIGNRIGHT);
		} else if (alignmentString.indexOf('c') != -1) {
			//                retVal.setTextAlignment(LabelInfo.ALIGNCENTER);
		}

		return null;
	}
}