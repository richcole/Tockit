/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.IOException;

import org.tockit.conscript.model.*;
import org.tockit.conscript.model.AbstractScale;
import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.FormalContext;
import org.tockit.conscript.model.FormattedString;
import org.tockit.conscript.model.ConscriptStructure;
import org.tockit.conscript.model.IdentifierMap;
import org.tockit.conscript.model.LineDiagram;
import org.tockit.conscript.model.QueryMap;
import org.tockit.conscript.model.StringMap;
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

    protected FormalContext getFormalContext(CSCFile file, String contextId) {
        FormalContext context = file.findFormalContext(contextId);
        if(context == null) {
            context = new FormalContext(contextId);
            file.add(context);
        }
        return context;
    }

    protected LineDiagram getLineDiagram(CSCFile file, String diagramId) {
        LineDiagram diagram = file.findLineDiagram(diagramId);
        if(diagram == null) {
            diagram = new LineDiagram(diagramId);
            file.add(diagram);
        }
        return diagram;
    }

    protected StringMap getStringMap(CSCFile file, String mapId) {
        StringMap map = file.findStringMap(mapId);
        if(map == null) {
            map = new StringMap(mapId);
            file.add(map);
        }
        return map;
    }

    protected IdentifierMap getIdentifierMap(CSCFile file, String mapId) {
        IdentifierMap map = file.findIdentifierMap(mapId);
        if(map == null) {
            map = new IdentifierMap(mapId);
            file.add(map);
        }
        return map;
    }

    protected QueryMap getQueryMap(CSCFile file, String mapId) {
        QueryMap map = file.findQueryMap(mapId);
        if(map == null) {
            map = new QueryMap(mapId);
            file.add(map);
        }
        return map;
    }

    protected AbstractScale getAbstractScale(CSCFile file, String scaleId) {
        AbstractScale scale = file.findAbstractScale(scaleId);
        if(scale == null) {
            scale = new AbstractScale(scaleId);
            file.add(scale);
        }
        return scale;
    }

    protected ConcreteScale getConcreteScale(CSCFile file, String scaleId) {
        ConcreteScale scale = file.findConcreteScale(scaleId);
        if(scale == null) {
            scale = new ConcreteScale(scaleId);
            file.add(scale);
        }
        return scale;
    }

    protected RealisedScale getRealisedScale(CSCFile file, String scaleId) {
        RealisedScale scale = file.findRealisedScale(scaleId);
        if(scale == null) {
            scale = new RealisedScale(scaleId);
            file.add(scale);
        }
        return scale;
    }

    protected DatabaseDefinition getDatabaseDefinition(CSCFile file, String dbDefId) {
        DatabaseDefinition dbDef = file.findDatabaseDefinition(dbDefId);
        if(dbDef == null) {
            dbDef = new DatabaseDefinition(dbDefId);
            file.add(dbDef);
        }
        return dbDef;
    }

    protected ConceptualSchema getConceptualSchema(CSCFile file, String schemaId) {
        ConceptualSchema schema = file.findConceptualSchema(schemaId);
        if(schema == null) {
            schema = new ConceptualSchema(schemaId);
            file.add(schema);
        }
        return schema;
    }

    protected ConceptualFile getConceptualFile(CSCFile file, String fileId) {
        ConceptualFile conceptualFile = file.findConceptualFile(fileId);
        if(file == null) {
            conceptualFile = new ConceptualFile(fileId);
            file.add(conceptualFile);
        }
        return conceptualFile;
    }
}