/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;

import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.FCAAttribute;
import org.tockit.conscript.model.FCAObject;
import org.tockit.conscript.model.FormattedString;
import org.tockit.conscript.model.Line;
import org.tockit.conscript.model.LineDiagram;
import org.tockit.conscript.model.Point;
import org.tockit.conscript.model.StringFormat;
import org.tockit.conscript.model.TypedSize;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class LineDiagramParser extends CSCFileSectionParser {
	public String getStartToken() {
		return "LINE_DIAGRAM";
	}

	public void parse(CSCTokenizer tokenizer, CSCFile file)
	                        throws IOException, DataFormatException {
		String identifier = tokenizer.popCurrentToken();
        LineDiagram diagram = getLineDiagram(file, identifier);
        if(diagram.isInitialized()) {
            throw new DataFormatException("Line diagram with name '" + diagram.getName() + "' defined twice.");
        }

        tokenizer.consumeToken("=");

		parseTitleRemarkSpecials(tokenizer, diagram);

        tokenizer.consumeToken("UNITLENGTH");
        double value = Double.parseDouble(tokenizer.popCurrentToken());
        String unitType = tokenizer.popCurrentToken();
        diagram.setUnitLength(new TypedSize(value,unitType));

		tokenizer.consumeToken("POINTS");

        Map points = new Hashtable();
		while (!tokenizer.getCurrentToken().equals("LINES")) {
			Long id = new Long(tokenizer.popCurrentToken());

			double x = Double.parseDouble(tokenizer.popCurrentToken());
			double y = Double.parseDouble(tokenizer.popCurrentToken());
			Point point = new Point(id.longValue(), x, y, null, null);
            points.put(id, point);
            diagram.addPoint(point);
            while(!tokenizer.newLineHasStarted()) { // ignore optional format definitions
                // @todo add parsing here
                tokenizer.advance();
            }
		}
		tokenizer.consumeToken("LINES");

		while (!tokenizer.getCurrentToken().equals("OBJECTS")) {
            tokenizer.consumeToken("(");
            Long from = new Long(tokenizer.popCurrentToken());
            tokenizer.consumeToken(",");
            Long to = new Long(tokenizer.popCurrentToken());
            tokenizer.consumeToken(")");
            
            Point fromPoint = (Point)points.get(from);
            if(fromPoint == null) {
                throw new DataFormatException("Can not resolve line start point " +
                                              from +" in LINE_DIAGRAM '" + diagram.getName() +
                                              "' in file '" + file.getLocation() +"', line " + tokenizer.getCurrentLine());
            }
            Point toPoint = (Point)points.get(to);
            if(fromPoint == null) {
                throw new DataFormatException("Can not resolve line end point " +
                                              to +" in LINE_DIAGRAM '" + diagram.getName() +
                                              "' in file '" + file.getLocation() +"', line " + tokenizer.getCurrentLine());
            }
            diagram.addLine(new Line(fromPoint, toPoint, null));
		}
		tokenizer.advance();

		while (!tokenizer.getCurrentToken().equals("ATTRIBUTES")) {
            Long pointId = new Long(tokenizer.popCurrentToken());
            Point point = (Point) points.get(pointId);
            if(point == null) {
                throw new DataFormatException("Can not resolve point " +
                                              point +" for object in LINE_DIAGRAM '" + diagram.getName() +
                                              "' in file '" + file.getLocation() +"', line " + tokenizer.getCurrentLine());
            }
            
            String id = tokenizer.popCurrentToken();
			FormattedString label = null;
            
			if (!tokenizer.newLineHasStarted()) {
                String content = tokenizer.popCurrentToken();
                StringFormat format = null;
                try {
                    if (!tokenizer.newLineHasStarted()) {
                        format = new StringFormat(tokenizer.popCurrentToken());
                    }
                } catch(Exception e) {
                    throw new DataFormatException("Can not parse string format " +
                            " for object in LINE_DIAGRAM '" + diagram.getName() +
                            "' in file '" + file.getLocation() +"', line " + tokenizer.getCurrentLine());
                }
                label = new FormattedString(content, format);
			}
			
            diagram.addObject(new FCAObject(point, id, label));
		}
		tokenizer.advance();

		while (!tokenizer.getCurrentToken().equals("CONCEPTS")) {
            Long pointId = new Long(tokenizer.popCurrentToken());
            Point point = (Point) points.get(pointId);
            if(point == null) {
                throw new DataFormatException("Can not resolve point " +
                                              point +" for attribute in LINE_DIAGRAM '" + diagram.getName() +
                                              "' in file '" + file.getLocation() +"', line " + tokenizer.getCurrentLine());
            }
            
            String id = tokenizer.popCurrentToken();
            FormattedString label = null;
            
            if (!tokenizer.newLineHasStarted()) {
                String content = tokenizer.popCurrentToken();
                StringFormat format = null;
                if (!tokenizer.newLineHasStarted()) {
                    format = new StringFormat(tokenizer.popCurrentToken());
                }
                label = new FormattedString(content, format);
            }
            
            diagram.addAttribute(new FCAAttribute(point, id, label));
		}
		tokenizer.advance();

		while (!tokenizer.getCurrentToken().equals(";")) {
			// we ignore the concept definitions 
			tokenizer.advance();
		}
        tokenizer.consumeToken(";");

        diagram.setInitialized();
        CSCParser.logger.log(Level.FINER, "Line diagram added: '" + diagram.getName() + "'");
	}
}