package org.tockit.cass.javaexport;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class Properties {
    private static Model model = ModelFactory.createDefaultModel();
    
    /**
     * Some entity (package, class, method,..) contains another directly.
     * 
     * Domain are all non-atomic entities, range is universal.
     */
	public static final Property CONTAINS = model.createProperty("http://tockit.org/cass#contains");
	
	/**
	 * A method calls another directly.
	 * 
	 * Domain and range are methods.
	 */
	public static final Property CALLS = model.createProperty("http://tockit.org/cass#calls");
	
	/**
	 * Determines the type of an entity.
	 * 
	 * Domain is universal, range is defined in {@link Types}.
	 */
	public static final Property TYPE = model.createProperty("http://tockit.org/cass#type");
	
	// from here on properties are usually not asserted directly, but inferred by rules
	/**
	 * Some entity contains another directly or indirectly.
	 * 
	 * This is the transitive closure of the #CONTAINS property.
	 */
	public static final Property CONTAINS_TRANSITIVELY = model.createProperty("http://tockit.org/cass#contains_t");
	
	/**
	 * Some entity calls another directly.
	 * 
	 * This is the #CALLS property extended along the #CONTAINS_TRANSITIVELY property by the
	 * following rule: if method A calls method B, then anything that contains A calls anything
	 * that contains B.  
	 * 
	 * Domain and range is methods and anything that can contain a method.
	 */
	public static final Property CALLS_EXTENDED = model.createProperty("http://tockit.org/cass#calls_ext");
	
	/**
	 * Some entity calls another directly or indirectly.
	 * 
	 * Note that this is actually the transitive closure of #CALLS_EXTENDED, not #CALLS. To retrieve
	 * the transitive closure of #CALLS one has to restrict the domain and range to methods via the
	 * #TYPE property.
	 */
	public static final Property CALLS_TRANSITIVELY = model.createProperty("http://tockit.org/cass#calls_t");
}
