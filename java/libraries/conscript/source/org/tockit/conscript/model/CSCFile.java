/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.model;

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
    private List remarks;
    
    private static class StructureId {}

    /**
     * We use a map instead of different members to allow reusing code more
     * without introspection.
     */
    private Map structures = new Hashtable();
    
    private final static StructureId FORMAL_CONTEXT = new StructureId();
    private final static StructureId LINE_DIAGRAM = new StructureId();
    private final static StructureId STRING_MAP = new StructureId();
    private final static StructureId IDENTIFIER_MAP = new StructureId();
    private final static StructureId QUERY_MAP = new StructureId();
    private final static StructureId ABSTRACT_SCALE = new StructureId();
    private final static StructureId CONCRETE_SCALE = new StructureId();
    private final static StructureId REALISED_SCALE = new StructureId();
    private final static StructureId DATABASE_DEFINITION = new StructureId();
    private final static StructureId CONCEPTUAL_SCHEMA = new StructureId();
    private final static StructureId CONCEPTUAL_FILE = new StructureId();

    private List includeFiles = new ArrayList();

    public CSCFile(URL file, CSCFile parent) {
		this.location = file;
        this.parent = parent;
        this.structures.put(FORMAL_CONTEXT, new Hashtable());
        this.structures.put(LINE_DIAGRAM, new Hashtable());
        this.structures.put(STRING_MAP, new Hashtable());
        this.structures.put(IDENTIFIER_MAP, new Hashtable());
        this.structures.put(QUERY_MAP, new Hashtable());
        this.structures.put(ABSTRACT_SCALE, new Hashtable());
        this.structures.put(CONCRETE_SCALE, new Hashtable());
        this.structures.put(REALISED_SCALE, new Hashtable());
        this.structures.put(DATABASE_DEFINITION, new Hashtable());
        this.structures.put(CONCEPTUAL_SCHEMA, new Hashtable());
        this.structures.put(CONCEPTUAL_FILE, new Hashtable());
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
    
    public List getRemarks() {
        return Collections.unmodifiableList(this.remarks);
    }
    
    public List getIncludeFiles() {
        return Collections.unmodifiableList(this.includeFiles);
    }
    
    public void addRemark(String remark) {
        this.remarks.add(remark);
    }
    
    public void add(FormalContext context) {
        Map formalContexts = (Map) this.structures.get(FORMAL_CONTEXT);
        formalContexts.put(context.getName(), context);
    }

    public void add(LineDiagram diagram) {
        Map lineDiagrams = (Map) this.structures.get(LINE_DIAGRAM);
        lineDiagrams.put(diagram.getName(), diagram);
    }

    public void add(StringMap map) {
        Map stringMaps = (Map) this.structures.get(STRING_MAP);
        stringMaps.put(map.getName(), map);
    }

    public void add(IdentifierMap map) {
        Map identifierMaps = (Map) this.structures.get(IDENTIFIER_MAP);
        identifierMaps.put(map.getName(), map);
    }

    public void add(QueryMap map) {
        Map queryMaps = (Map) this.structures.get(QUERY_MAP);
        queryMaps.put(map.getName(), map);
    }

    public void add(AbstractScale scale) {
        Map abstractScales = (Map) this.structures.get(ABSTRACT_SCALE);
        abstractScales.put(scale.getName(), scale);
    }

    public void add(ConcreteScale scale) {
        Map concreteScales = (Map) this.structures.get(CONCRETE_SCALE);
        concreteScales.put(scale.getName(), scale);
    }

    public void add(RealisedScale scale) {
        Map realisedScales = (Map) this.structures.get(REALISED_SCALE);
        realisedScales.put(scale.getName(), scale);
    }

    public void add(DatabaseDefinition dbDefinition) {
        Map dbDefs = (Map) this.structures.get(DATABASE_DEFINITION);
        dbDefs.put(dbDefinition.getName(), dbDefinition);
    }

    public void add(ConceptualSchema schema) {
        Map schemas = (Map) this.structures.get(CONCEPTUAL_SCHEMA);
        schemas.put(schema.getName(), schema);
    }

    public void add(ConceptualFile file) {
        Map files = (Map) this.structures.get(CONCEPTUAL_FILE);
        files.put(file.getName(), file);
    }
    
    public List getFormalContexts() {
        return getListGlobal(FORMAL_CONTEXT);
    }
    
    public List getLineDiagrams() {
        return getListGlobal(LINE_DIAGRAM);
    }
    
    public List getStringMaps() {
        return getListGlobal(STRING_MAP);
    }
    
    public List getIdentiferMaps() {
        return getListGlobal(IDENTIFIER_MAP);
    }
    
    public List getQueryMaps() {
        return getListGlobal(QUERY_MAP);
    }
    
    public List getAbstractScales() {
        return getListGlobal(ABSTRACT_SCALE);
    }
    
    public List getConcreteScales() {
        return getListGlobal(CONCRETE_SCALE);
    }
    
    public List getRealisedScales() {
        return getListGlobal(REALISED_SCALE);
    }
    
    public List getDatabaseDefinitions() {
        return getListGlobal(DATABASE_DEFINITION);
    }
    
    public List getConceptualSchemas() {
        return getListGlobal(CONCEPTUAL_SCHEMA);
    }
    
    public List getConceptualFiles() {
        return getListGlobal(CONCEPTUAL_FILE);
    }
    
    private List getListGlobal(StructureId structure) {
        if(this.parent != null) {
            return this.parent.getListGlobal(structure);
        } else {
            return getList(structure);
        }
    }

    private List getList(StructureId structure) {
        Map structureMap = (Map) this.structures.get(structure);
        List retVal = new ArrayList(structureMap.values());
        for (Iterator iter = this.includeFiles.iterator(); iter.hasNext();) {
            CSCFile includeFile = (CSCFile) iter.next();
            retVal.addAll(includeFile.getFormalContexts());
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
        return (RealisedScale) findSchemaPartGlobal(REALISED_SCALE, name);
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
        Map structureMap = (Map) this.structures.get(structure);
        ConscriptStructure part = (ConscriptStructure) structureMap.get(name);
        if(part != null) {
            return part;
        }
        for (Iterator iter = this.includeFiles.iterator(); iter.hasNext();) {
            CSCFile includeFile = (CSCFile) iter.next();
            part = includeFile.findSchemaPart(structure, name);
            if(part != null) {
                return part;
            }
        }
        return null;
    }

    public void checkForInitialization() throws DataFormatException {
        Collection structureMaps = this.structures.values();
        for (Iterator iter = structureMaps.iterator(); iter.hasNext();) {
            Map structureMap = (Map) iter.next();
            Collection structureList = structureMap.values();
            for (Iterator iterator = structureList.iterator(); iterator.hasNext();) {
                ConscriptStructure structure = (ConscriptStructure) iterator.next();
                if(!structure.isInitialized()) {
                    throw new DataFormatException("Structure '" + structure.getName() + "' of type '" +
                            structure.getClass().getName() + "' was not initialised.");
                }
            }
        }
    }
}