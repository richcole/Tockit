/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.tockit.conscript.parser.DataFormatException;

public class CSCFile {
	private URL location;
    private CSCFile parent;
    private List<String> remarks = new ArrayList<String>();
    
    private static class StructureId {
        static List<StructureId> ids = new ArrayList<StructureId>(); 
        private String idString;
        public StructureId(String idString) {
            this.idString = idString;
            ids.add(this);
        }
        public String getIdString() {
            return this.idString;
        }
    }

    /**
     * We use a map instead of different members to allow reusing code more
     * without introspection.
     * 
     * @TODO this is not fully typesafe since users expect a particular ConscriptStructure --
     *       mapping a Class<T> to a Map<String,T> might be a better approach instead of the
     *       StructureIds 
     */
    private Map<StructureId, Map<String,ConscriptStructure>> structures = 
    	new Hashtable<StructureId, Map<String,ConscriptStructure>>();
    
    private final static StructureId FORMAL_CONTEXT = new StructureId("FORMAL_CONTEXT");
    private final static StructureId LINE_DIAGRAM = new StructureId("LINE_DIAGRAM");
    private final static StructureId STRING_MAP = new StructureId("STRING_MAP");
    private final static StructureId IDENTIFIER_MAP = new StructureId("IDENTIFIER_MAP");
    private final static StructureId QUERY_MAP = new StructureId("QUERY_MAP");
    private final static StructureId ABSTRACT_SCALE = new StructureId("ABSTRACT_SCALE");
    private final static StructureId CONCRETE_SCALE = new StructureId("CONCRETE_SCALE");
    private final static StructureId REALIZED_SCALE = new StructureId("REALIZED_SCALE");
    private final static StructureId DATABASE_DEFINITION = new StructureId("DATABASE");
    private final static StructureId CONCEPTUAL_SCHEMA = new StructureId("CONCEPTUAL_SCHEME");
    private final static StructureId CONCEPTUAL_FILE = new StructureId("CONCEPTUAL_FILE");

    private List<CSCFile> includeFiles = new ArrayList<CSCFile>();

    public CSCFile(URL file, CSCFile parent) {
		this.location = file;
        this.parent = parent;
        for (Iterator<StructureId> iter = StructureId.ids.iterator(); iter.hasNext();) {
            StructureId id = iter.next();
            this.structures.put(id, new Hashtable<String,ConscriptStructure>());
        }
        if(parent != null) {
            parent.includeFiles.add(this);
        }
	}
    
    public URL getLocation() {
        return this.location;
    }
    
    public CSCFile getParent() {
        return this.parent;
    }
    
    public List<String> getRemarks() {
        return Collections.unmodifiableList(this.remarks);
    }
    
    public List<CSCFile> getIncludeFiles() {
        return Collections.unmodifiableList(this.includeFiles);
    }
    
    public void addRemark(String remark) {
        this.remarks.add(remark);
    }
    
    public void add(FormalContext context) {
        Map<String, ConscriptStructure> formalContexts = this.structures.get(FORMAL_CONTEXT);
        formalContexts.put(context.getName(), context);
    }

    public void add(LineDiagram diagram) {
        Map<String, ConscriptStructure> lineDiagrams = this.structures.get(LINE_DIAGRAM);
        lineDiagrams.put(diagram.getName(), diagram);
    }

    public void add(StringMap map) {
        Map<String, ConscriptStructure> stringMaps = this.structures.get(STRING_MAP);
        stringMaps.put(map.getName(), map);
    }

    public void add(IdentifierMap map) {
        Map<String, ConscriptStructure> identifierMaps = this.structures.get(IDENTIFIER_MAP);
        identifierMaps.put(map.getName(), map);
    }

    public void add(QueryMap map) {
        Map<String, ConscriptStructure> queryMaps = this.structures.get(QUERY_MAP);
        queryMaps.put(map.getName(), map);
    }

    public void add(AbstractScale scale) {
        Map<String, ConscriptStructure> abstractScales = this.structures.get(ABSTRACT_SCALE);
        abstractScales.put(scale.getName(), scale);
    }

    public void add(ConcreteScale scale) {
        Map<String, ConscriptStructure> concreteScales = this.structures.get(CONCRETE_SCALE);
        concreteScales.put(scale.getName(), scale);
    }

    public void add(RealisedScale scale) {
        Map<String, ConscriptStructure> realisedScales = this.structures.get(REALIZED_SCALE);
        realisedScales.put(scale.getName(), scale);
    }

    public void add(DatabaseDefinition dbDefinition) {
        Map<String, ConscriptStructure> dbDefs = this.structures.get(DATABASE_DEFINITION);
        dbDefs.put(dbDefinition.getName(), dbDefinition);
    }

    public void add(ConceptualSchema schema) {
        Map<String, ConscriptStructure> schemas = this.structures.get(CONCEPTUAL_SCHEMA);
        schemas.put(schema.getName(), schema);
    }

    public void add(ConceptualFile file) {
        Map<String, ConscriptStructure> files = this.structures.get(CONCEPTUAL_FILE);
        files.put(file.getName(), file);
    }
    
    public List<ConscriptStructure> getFormalContexts() {
        return getListGlobal(FORMAL_CONTEXT);
    }
    
    public List<ConscriptStructure> getLineDiagrams() {
        return getListGlobal(LINE_DIAGRAM);
    }
    
    public List<ConscriptStructure> getStringMaps() {
        return getListGlobal(STRING_MAP);
    }
    
    public List<ConscriptStructure> getIdentiferMaps() {
        return getListGlobal(IDENTIFIER_MAP);
    }
    
    public List<ConscriptStructure> getQueryMaps() {
        return getListGlobal(QUERY_MAP);
    }
    
    public List<ConscriptStructure> getAbstractScales() {
        return getListGlobal(ABSTRACT_SCALE);
    }
    
    public List<ConscriptStructure> getConcreteScales() {
        return getListGlobal(CONCRETE_SCALE);
    }
    
    public List<ConscriptStructure> getRealisedScales() {
        return getListGlobal(REALIZED_SCALE);
    }
    
    public List<ConscriptStructure> getDatabaseDefinitions() {
        return getListGlobal(DATABASE_DEFINITION);
    }
    
    public List<ConscriptStructure> getConceptualSchemas() {
        return getListGlobal(CONCEPTUAL_SCHEMA);
    }
    
    public List<ConscriptStructure> getConceptualFiles() {
        return getListGlobal(CONCEPTUAL_FILE);
    }
    
    private List<ConscriptStructure> getListGlobal(StructureId structure) {
        if(this.parent != null) {
            return this.parent.getListGlobal(structure);
        } else {
            return getList(structure);
        }
    }

    private List<ConscriptStructure> getList(StructureId structure) {
        Map<String,ConscriptStructure> structureMap = this.structures.get(structure);
        List<ConscriptStructure> retVal = new ArrayList<ConscriptStructure>(structureMap.values());
        for (Iterator<CSCFile> iter = this.includeFiles.iterator(); iter.hasNext();) {
            CSCFile includeFile = iter.next();
            retVal.addAll(includeFile.getList(structure));
        }
        return retVal;
    }
    
    public FormalContext findFormalContext(String name) {
        return (FormalContext) findSchemaPartGlobal(FORMAL_CONTEXT, name);
    }    
    
    public LineDiagram findLineDiagram(String name) {
        return (LineDiagram) findSchemaPartGlobal(LINE_DIAGRAM, name);
    }    
    
    public StringMap findStringMap(String name) {
        return (StringMap) findSchemaPartGlobal(STRING_MAP, name);
    }    
    
    public IdentifierMap findIdentifierMap(String name) {
        return (IdentifierMap) findSchemaPartGlobal(IDENTIFIER_MAP, name);
    }    
    
    public QueryMap findQueryMap(String name) {
        return (QueryMap) findSchemaPartGlobal(QUERY_MAP, name);
    }    
    
    public AbstractScale findAbstractScale(String name) {
        return (AbstractScale) findSchemaPartGlobal(ABSTRACT_SCALE, name);
    }    
    
    public ConcreteScale findConcreteScale(String name) {
        return (ConcreteScale) findSchemaPartGlobal(CONCRETE_SCALE, name);
    }    
    
    public RealisedScale findRealisedScale(String name) {
        return (RealisedScale) findSchemaPartGlobal(REALIZED_SCALE, name);
    }    
    
    public DatabaseDefinition findDatabaseDefinition(String name) {
        return (DatabaseDefinition) findSchemaPartGlobal(DATABASE_DEFINITION, name);
    }    
    
    public ConceptualSchema findConceptualSchema(String name) {
        return (ConceptualSchema) findSchemaPartGlobal(CONCEPTUAL_SCHEMA, name);
    }    
    
    public ConceptualFile findConceptualFile(String name) {
        return (ConceptualFile) findSchemaPartGlobal(CONCEPTUAL_FILE, name);
    }    
    
    private ConscriptStructure findSchemaPartGlobal(StructureId structure, String name) {
        if(this.parent != null) {
            return this.parent.findSchemaPartGlobal(structure, name);
        } else {
            return findSchemaPart(structure, name);
        }
    }

    private ConscriptStructure findSchemaPart(StructureId structure, String name) {
        Map<String,ConscriptStructure> structureMap = this.structures.get(structure);
        ConscriptStructure part = structureMap.get(name);
        if(part != null) {
            return part;
        }
        for (Iterator<CSCFile> iter = this.includeFiles.iterator(); iter.hasNext();) {
            CSCFile includeFile = iter.next();
            part = includeFile.findSchemaPart(structure, name);
            if(part != null) {
                return part;
            }
        }
        return null;
    }

    public void checkForInitialization() throws DataFormatException {
        Collection<Map<String,ConscriptStructure>> structureMaps = this.structures.values();
        for (Iterator<Map<String,ConscriptStructure>> iter = structureMaps.iterator(); iter.hasNext();) {
            Map<String,ConscriptStructure> structureMap = iter.next();
            Collection<ConscriptStructure> structureList = structureMap.values();
            for (Iterator<ConscriptStructure> iterator = structureList.iterator(); iterator.hasNext();) {
                ConscriptStructure structure = iterator.next();
                if(!structure.isInitialized()) {
                    throw new DataFormatException("Structure '" + structure.getName() + "' of type '" +
                            structure.getClass().getName() + "' was not initialised.");
                }
            }
        }
    }
    
    public boolean hasInclude(URL includeURL) {
        for (Iterator<CSCFile> iter = this.includeFiles.iterator(); iter.hasNext();) {
            CSCFile includeFile = iter.next();
            if(includeFile.location.equals(includeURL) || includeFile.hasInclude(includeURL)) {
                return true;
            }
        }
        return false;
    }

    public void printCSC(PrintStream stream) {
        if(!this.remarks.isEmpty()) {
            stream.println("REMARK");
            for (Iterator<String> iter = this.remarks.iterator(); iter.hasNext();) {
                String remark = iter.next();
                stream.println("\"" + remark + "\";");
            }
        }

        for (Iterator<StructureId> iter = StructureId.ids.iterator(); iter.hasNext();) {
            StructureId id = iter.next();
            printStructures(id, stream);
        }
        
        for (Iterator<CSCFile> iter = this.includeFiles.iterator(); iter.hasNext();) {
            CSCFile includeFile = iter.next();
            includeFile.printCSC(stream);
        }
    }
    
    private void printStructures(StructureId structureType, PrintStream stream) {
        Map<String,ConscriptStructure> structureMap = this.structures.get(structureType);
        Collection<ConscriptStructure> values = structureMap.values();
        if(!values.isEmpty()) {
            stream.println(structureType.getIdString());
            for (Iterator<ConscriptStructure> iter = values.iterator(); iter.hasNext();) {
                ConscriptStructure element = iter.next();
                element.printCSC(stream);
                stream.println();
            }
        }
    }
}