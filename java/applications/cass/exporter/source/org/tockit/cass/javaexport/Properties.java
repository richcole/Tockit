package org.tockit.cass.javaexport;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

/**
 * Class containing constants for all the properties of the CASS RDF model.
 */
public class Properties {
    private static Model model = ModelFactory.createDefaultModel();
    
    /**
     * Some entity (package, class, method,..) contains another directly.
     * 
     * Domain are all non-atomic entities, range is universal.
     */
	public static final Property CONTAINS = model.createProperty(Namespaces.CASS_PROPERTIES + "contains");
	
	/**
	 * A method calls another directly.
	 * 
	 * Domain and range are methods.
	 */
	public static final Property CALLS = model.createProperty(Namespaces.CASS_PROPERTIES + "calls");
	
	/**
	 * Determines the type of an entity.
	 * 
	 * Domain is universal, range is defined in {@link Types}.
	 */
	public static final Property TYPE = model.createProperty(Namespaces.CASS_PROPERTIES + "type");
	
	// from here on properties are usually not asserted directly, but inferred by rules
	/**
	 * Some entity contains another directly or indirectly.
	 * 
	 * This is the transitive closure of the #CONTAINS property.
	 */
	public static final Property CONTAINS_TRANSITIVELY = model.createProperty(Namespaces.CASS_PROPERTIES + "contains_t");
		
	/**
	 * Some entity calls another directly or indirectly.
	 * 
	 * This is the transitive closure of #CALLS.
	 */
	public static final Property CALLS_TRANSITIVELY = model.createProperty(Namespaces.CASS_PROPERTIES + "calls_t");
	
	/**
	 * Some entity calls another directly.
	 * 
	 * This is the #CALLS_TRANSITIVELY property extended along the #CONTAINS_TRANSITIVELY 
	 * property by the following rule: if method A calls method B, then anything that contains 
	 * A calls anything that contains B.  
	 * 
	 * Domain and range is methods and anything that can contain a method.
	 */
	public static final Property CALLS_EXTENDED = model.createProperty(Namespaces.CASS_PROPERTIES + "calls_ext");

	/**
	 * Some entity depends on another.
	 * 
	 * This is a transitive closure of the union of some other relations, such as the callgraph and type
	 * usage. TODO: define properly.
	 */
	public static final Property DEPENDS_TRANSITIVELY = model.createProperty(Namespaces.CASS_PROPERTIES + "depends_t");
}
