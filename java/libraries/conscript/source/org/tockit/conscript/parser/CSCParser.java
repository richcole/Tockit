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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.tockit.conscript.model.*;
import org.tockit.conscript.parser.sectionparsers.*;

public class CSCParser {
    /// @todo now we start hacking...
    public static Hashtable sectionIdMap = new Hashtable();
    
    /// @todo move into tokenizer 
    public static String resolveEscapes(String input) {
    	String output = "";
    	for(int i = 0; i < input.length(); i++) {
    		char curChar = input.charAt(i);
    		if(curChar == '\\') {
    			i++;
    			output += input.charAt(i);
    		} else {
    			output += curChar;
    		}
    	}
    	return output;
    }
    
    protected CSCFileSectionParser currentSectionParser = null;
    
    public void importCSCFile(File file, ConceptualSchema schema) 
    								throws FileNotFoundException, DataFormatException {
        try {
            CSCTokenizer tokenizer = new CSCTokenizer(file);
            
            List diagrams = new ArrayList();
            List contexts = new ArrayList();
            Hashtable queryMaps = new Hashtable();
            
            while(! tokenizer.done()) {
            	this.currentSectionParser = identifySectionParser(tokenizer);
				if(this.currentSectionParser == null) {
					// first round and we don't grok it
					throw new RuntimeException("The specified file is not a" +
												"CSC file.");
				}
            	try {
            		Object result = this.currentSectionParser.parse(tokenizer);
//            		if(result instanceof Diagram2D) {
//            			diagrams.add(result);
//            		} else if (result instanceof ContextImplementation) {
//            			contexts.add(result);
//            		} else if (result instanceof QueryMap) {
//            			QueryMap queryMap = (QueryMap) result;
//            			queryMaps.put(queryMap.name, queryMap);
//            		}
            	} catch (SectionTypeNotSupportedException e) {
            		System.err.println(e.getMessage());
            		// eat a whole section
            		while(!tokenizer.getCurrentToken().equals(";")) {
            			tokenizer.advance();
            		}
            		tokenizer.advance();
            	}
            }
            
            List importedDiagrams = new ArrayList();
            
//            for (Iterator iter = diagrams.iterator(); iter.hasNext();) {
//                Object result = iter.next();
//                if(result instanceof Diagram2D) {
//                    Diagram2D diagram = (Diagram2D) result;
//                    insertDiagram(schema, diagram, (QueryMap) queryMaps.get(sectionIdMap.get(diagram.getTitle())));
//                    importedDiagrams.add(diagram.getTitle());
//                }
//            }
//
//            for (Iterator iter = contexts.iterator(); iter.hasNext();) {
//                Object result = iter.next();
//                if(result instanceof ContextImplementation) {
//                    ContextImplementation context = (ContextImplementation) result;
//                	if(importedDiagrams.contains(context.getName())) {
//                		continue;
//                	}
//                    LatticeGenerator lgen = new GantersAlgorithm();
//                    Lattice lattice = lgen.createLattice(context);
//                    Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, context.getName(), new DefaultDimensionStrategy());
//                    insertDiagram(schema,diagram, (QueryMap) queryMaps.get(sectionIdMap.get(diagram.getTitle())));                	
//                }
//            }
        } catch (IOException e) {
            throw new DataFormatException("Error reading input file", e);
        }
    }

//    protected void insertDiagram(ConceptualSchema schema, Diagram2D diagram, QueryMap queryMap) {
//    	if(queryMap != null) {
//    		replaceObjects(diagram, queryMap);
//    	}
//        Diagram2D existingDiagram = schema.getDiagram(diagram.getTitle());
//        rescale(diagram);
//        if(existingDiagram != null) {
//            schema.replaceDiagram(existingDiagram, diagram);
//        } else {
//        	schema.addDiagram(diagram);
//        }
//    }
//
//    private void replaceObjects(Diagram2D diagram, QueryMap queryMap) {
//    	Iterator nodes = diagram.getNodes();
//    	while (nodes.hasNext()) {
//            DiagramNode node = (DiagramNode) nodes.next();
//            ConceptImplementation concept = (ConceptImplementation) node.getConcept();
//            Iterator objIt = concept.getObjectContingentIterator();
//            while (objIt.hasNext()) {
//                String object = (String) objIt.next();
//                if(queryMap.map.containsKey(object)) {
//                	objIt.remove();
//                	concept.addObject(queryMap.map.get(object));
//                }
//            }
//        }
//    }
//
//    private void rescale(Diagram2D diagram) {
//    	Rectangle2D bounds = diagram.getBounds();
//        
//        double scaleX;
//        if(bounds.getWidth() == 0) {
//            scaleX = Double.MAX_VALUE;
//        } else {
//            scaleX = TARGET_DIAGRAM_WIDTH / bounds.getWidth();
//        }
//        
//        double scaleY;
//        if(bounds.getHeight() == 0) {
//            scaleY = Double.MAX_VALUE;
//        } else {
//            scaleY = TARGET_DIAGRAM_HEIGHT / bounds.getHeight();
//        }
//        
//        double scale = (scaleX < scaleY) ? scaleX : scaleY;
//        
//        Iterator it = diagram.getNodes();
//        while (it.hasNext()) {
//            DiagramNode node = (DiagramNode) it.next();
//            Point2D pos = node.getPosition();
//            node.setPosition(new Point2D.Double(scale * pos.getX(), scale * pos.getY()));
//
//            LabelInfo aLabel = node.getAttributeLabelInfo();
//            if(aLabel != null) {
//                Point2D offset = aLabel.getOffset();
//                aLabel.setOffset(new Point2D.Double(scale * offset.getX(), scale * offset.getY()));
//            }
//
//            LabelInfo oLabel = node.getObjectLabelInfo();
//            if(oLabel != null) {
//                Point2D offset = oLabel.getOffset();
//                oLabel.setOffset(new Point2D.Double(scale * offset.getX(), scale * offset.getY()));
//            }
//        }
//    }
    
    protected CSCFileSectionParser identifySectionParser(CSCTokenizer tokenizer) throws IOException, DataFormatException {
    	CSCFileSectionParser[] parsers = CSCFileSectionParser.getParsers();
		for (int i = 0; i < parsers.length; i++) {
            CSCFileSectionParser sectionType = parsers[i];
            if(sectionType.getStartToken().equals(tokenizer.getCurrentToken())) {
            	tokenizer.advance();
            	return sectionType;
            }
        }
        return this.currentSectionParser;
    }
}
