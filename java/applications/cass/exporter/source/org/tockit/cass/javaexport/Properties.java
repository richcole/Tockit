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
	
	/**
	 * A method has a type as parameter.
	 * 
	 * Domain are the methods, range are the types (Java classes and interfaces).
	 */
	public static final Property HAS_PARAMETER = model.createProperty(Namespaces.CASS_PROPERTIES + "hasParameter");
	
	/**
	 * A method has a type as parameter directly or indirectly.
	 * 
	 * This extends {@link #HAS_PARAMETER} by adding the base types of arrays, i.e. a standard main method
	 * would have this property not only bound to "java.lang.String[]", but also to "java.lang.String". 
	 * 
	 * Domain are the methods, range are the types (Java classes and interfaces).
	 */
	public static final Property HAS_PARAMETER_EXTENDED = model.createProperty(Namespaces.CASS_PROPERTIES + "hasParameter_ext");

	/**
	 * A method has a type as return value.
	 * 
	 * Domain are the methods, range are the types (Java classes and interfaces).
	 */
	public static final Property HAS_RETURN_TYPE = model.createProperty(Namespaces.CASS_PROPERTIES + "hasReturnValue");
	
	/**
	 * A method has a type as parameter directly or indirectly.
	 * 
	 * This extends {@link #HAS_RETURN_TYPE} by adding the base types of arrays, i.e. a method returning
	 * "java.lang.String[]" would also be bound to "java.lang.String". 
	 * 
	 * Domain are the methods, range are the types (Java classes and interfaces).
	 */
	public static final Property HAS_RETURN_TYPE_EXTENDED = model.createProperty(Namespaces.CASS_PROPERTIES + "hasReturnType_ext");

	/**
	 * A type uses another type as field.
	 * 
	 * Domain and range are the types (Java classes and interfaces).
	 */
	public static final Property HAS_FIELD_TYPE = model.createProperty(Namespaces.CASS_PROPERTIES + "hasFieldType");
	
	/**
	 * A type extends another type.
	 * 
	 * Domain and range are the types (Java classes and interfaces), but only with matching pairs (i.e.
	 * the relation is subset of the union of the two separate cross-products, not the cross-product of
	 * the union). 
	 */
	public static final Property EXTENDS = model.createProperty(Namespaces.CASS_PROPERTIES + "extends");
	
	/**
	 * A type implements an interface.
	 * 
	 * Note that this does not match the Java keyword "implements" but also includes the supertype relation
	 * between interfaces (i.e. "extends" used on interfaces).
	 * 
	 * Domain are the types, range are the interfaces.
	 */
	public static final Property IMPLEMENTS = model.createProperty(Namespaces.CASS_PROPERTIES + "implements");
	
	/**
	 * A type uses another type as field directly or indirectly.
	 * 
	 * This extends {@link #HAS_FIELD_TYPE} by adding the base types of arrays, i.e. a type using
	 * "java.lang.String[]" would also be bound to "java.lang.String". 
	 * 
	 * Domain and range are the types (Java classes and interfaces).
	 */
	public static final Property HAS_FIELD_TYPE_EXTENDED = model.createProperty(Namespaces.CASS_PROPERTIES + "hasFieldType_ext");

	// from here on properties are usually not asserted directly, but inferred by rules
	/**
	 * Some entity contains another directly or indirectly.
	 * 
	 * This is the transitive and reflexive closure of the {@link #CONTAINS} property.
	 */
	public static final Property CONTAINS_CLOSURE = model.createProperty(Namespaces.CASS_PROPERTIES + "contains_tr");
		
	/**
	 * Some entity calls another directly or indirectly.
	 * 
	 * This is the transitive and reflexive closure of {@link #CALLS}.
	 */
	public static final Property CALLS_CLOSURE = model.createProperty(Namespaces.CASS_PROPERTIES + "calls_tr");
	
	/**
	 * Some entity calls another directly.
	 * 
	 * This is the {@link #CALLS_CLOSURE} property extended along the {@link #CONTAINS_CLOSURE} 
	 * property by the following rule: if method A calls method B, then anything that contains 
	 * A calls anything that contains B.  
	 * 
	 * Domain and range is methods and anything that can contain a method.
	 */
	public static final Property CALLS_EXTENDED = model.createProperty(Namespaces.CASS_PROPERTIES + "calls_ext");

	/**
	 * Some entity depends on another.
	 * 
	 * This is a transitive and reflexive closure of the union of some other relations, such as the callgraph and type
	 * usage. TODO: define properly, maybe split into direct and transitive version
	 */
	public static final Property DEPENDS_TRANSITIVELY = model.createProperty(Namespaces.CASS_PROPERTIES + "depends_tr");

	/**
	 * The generic type hierachy.
	 * 
	 * This is any sub-type relationship, i.e. the union of {@link #EXTENDS} and {@link #IMPLEMENTS}.
	 * 
	 * Domain and range are the types (Java classes and interfaces). 
	 */
	public static final Property DERIVED_FROM = model.createProperty(Namespaces.CASS_PROPERTIES + "derivedFrom");

	/**
	 * A type extends another type directly or indirectly.
	 * 
	 * This is the transitive and reflexive closure of the {@link #EXTENDS} relation.
	 * 
	 * Domain and range are the types (Java classes and interfaces), but only with matching pairs (i.e.
	 * the relation is subset of the union of the two separate cross-products, not the cross-product of
	 * the union). 
	 */
	public static final Property EXTENDS_CLOSURE = model.createProperty(Namespaces.CASS_PROPERTIES + "extends_tr");
	
	/**
	 * A class implements an interface directly or indirectly.
	 * 
	 * This is the join of the {@link #IMPLEMENTS} relation with the {@link #EXTENDS_CLOSURE} relation.
	 * 
	 * Domain are the types, range are the interfaces.
	 */
	public static final Property IMPLEMENTS_CLOSURE = model.createProperty(Namespaces.CASS_PROPERTIES + "implements_t");
	
	/**
	 * The generic type hierachy.
	 * 
	 * This is the transitive and reflexive closure of {@link #DERIVED_FROM} or in other words the union of 
	 * {@link #EXTENDS_CLOSURE} and {@link #IMPLEMENTS_CLOSURE}.
	 * 
	 * Domain and range are the types (Java classes and interfaces). 
	 */
	public static final Property DERIVED_FROM_CLOSURE = model.createProperty(Namespaces.CASS_PROPERTIES + "derivedFrom_tr");
}
