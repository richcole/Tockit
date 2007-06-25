package org.tockit.cass.javaexport;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Class containing constants for all the types in the CASS RDF model.
 */
public class Types {
    private static Model model = ModelFactory.createDefaultModel();
    
    /**
     * Represents a Java package.
     */
	public static final Resource PACKAGE = model.createResource(Namespaces.CASS_TYPES + "package");
	
	/**
	 * Represents a Java Interface.
	 */
	public static final Resource INTERFACE = model.createProperty(Namespaces.CASS_TYPES + "interface");
	
	/**
	 * Represents a Java class.
	 */
	public static final Resource CLASS = model.createProperty(Namespaces.CASS_TYPES + "class");
	
	/**
	 * Represents a Java type.
	 * 
	 * Java types are classes, interfaces, arrays, primitives.
	 */
	public static final Resource TYPE = model.createProperty(Namespaces.CASS_TYPES + "type");
	
	/**
	 * Represents a method.
	 */
	public static final Resource METHOD = model.createProperty(Namespaces.CASS_TYPES + "method");
}
