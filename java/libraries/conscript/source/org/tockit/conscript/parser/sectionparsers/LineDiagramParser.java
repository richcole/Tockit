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
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.FCAAttribute;
import org.tockit.conscript.model.FCAObject;
import org.tockit.conscript.model.FormattedString;
import org.tockit.conscript.model.Line;
import org.tockit.conscript.model.LineDiagram;
import org.tockit.conscript.model.Point;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class LineDiagramParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "LINE_DIAGRAM";
	}

	public void parse(CSCTokenizer tokenizer, CSCFile targetFile)
	                        throws IOException, DataFormatException {
		String identifier = tokenizer.popCurrentToken();
        LineDiagram diagram = new LineDiagram(targetFile, identifier);

        tokenizer.consumeToken("=", targetFile);

		parseTitleRemarkSpecials(tokenizer, diagram);

		while (!tokenizer.getCurrentToken().equals("POINTS")) {
			// we ignore remark, special list and unitlength
			tokenizer.advance();
		}
		tokenizer.advance();

        Map points = new Hashtable();
		while (!tokenizer.getCurrentToken().equals("LINES")) {
			Long id = new Long(tokenizer.popCurrentToken());

			double x = Double.parseDouble(tokenizer.popCurrentToken());
			double y = Double.parseDouble(tokenizer.popCurrentToken());
			points.put(id, new Point(id.longValue(), x, y, null, null));
            while(!tokenizer.newLineHasStarted()) { // ignore optional format definitions
                // @todo add parsing here
                tokenizer.advance();
            }
		}
        diagram.setPoints((Point[]) points.values().toArray(new Point[points.size()]));
		tokenizer.advance();

        List lines = new ArrayList();
		while (!tokenizer.getCurrentToken().equals("OBJECTS")) {
            tokenizer.consumeToken("(", targetFile);
            Long from = new Long(tokenizer.popCurrentToken());
            tokenizer.consumeToken(",", targetFile);
            Long to = new Long(tokenizer.popCurrentToken());
            tokenizer.consumeToken(")", targetFile);
            
            Point fromPoint = (Point)points.get(from);
            if(fromPoint == null) {
                throw new DataFormatException("Can not resolve line start point " +
                                              from +" in LINE_DIAGRAM '" + diagram.getName() +
                                              "' in file '" + targetFile.getLocation() +"', line " + tokenizer.getCurrentLine());
            }
            Point toPoint = (Point)points.get(to);
            if(fromPoint == null) {
                throw new DataFormatException("Can not resolve line end point " +
                                              to +" in LINE_DIAGRAM '" + diagram.getName() +
                                              "' in file '" + targetFile.getLocation() +"', line " + tokenizer.getCurrentLine());
            }
            lines.add(new Line(fromPoint, toPoint, null));
		}
		tokenizer.advance();

        List objects = new ArrayList();
		while (!tokenizer.getCurrentToken().equals("ATTRIBUTES")) {
            Long pointId = new Long(tokenizer.popCurrentToken());
            Point point = (Point) points.get(pointId);
            if(point == null) {
                throw new DataFormatException("Can not resolve point " +
                                              point +" for object in LINE_DIAGRAM '" + diagram.getName() +
                                              "' in file '" + targetFile.getLocation() +"', line " + tokenizer.getCurrentLine());
            }
            
            String id = tokenizer.popCurrentToken();
			String content = tokenizer.popCurrentToken();
			FormattedString format = null;
            
			if (!tokenizer.newLineHasStarted()) {
				// still on the same line --> we have a formatting string
				tokenizer.popCurrentToken();
                // @todo parse format
			}
			
            objects.add(new FCAObject(point, id, content, format));
		}
		tokenizer.advance();
        diagram.setObjects((FCAObject[]) objects.toArray(new FCAObject[objects.size()]));

        List attributes = new ArrayList();
		while (!tokenizer.getCurrentToken().equals("CONCEPTS")) {
            Long pointId = new Long(tokenizer.popCurrentToken());
            Point point = (Point) points.get(pointId);
            if(point == null) {
                throw new DataFormatException("Can not resolve point " +
                                              point +" for attribute in LINE_DIAGRAM '" + diagram.getName() +
                                              "' in file '" + targetFile.getLocation() +"', line " + tokenizer.getCurrentLine());
            }
            
            String id = tokenizer.popCurrentToken();
            String content = tokenizer.popCurrentToken();
            FormattedString format = null;
            
            if (!tokenizer.newLineHasStarted()) {
                // still on the same line --> we have a formatting string
                tokenizer.popCurrentToken();
                // @todo parse format
            }
            
            attributes.add(new FCAAttribute(point, id, content, format));
		}
		tokenizer.advance();
        diagram.setAttributes((FCAAttribute[]) attributes.toArray(new FCAAttribute[attributes.size()]));

		while (!tokenizer.getCurrentToken().equals(";")) {
			// we ignore the concept definitions 
			tokenizer.advance();
		}
        tokenizer.consumeToken(";", targetFile);
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