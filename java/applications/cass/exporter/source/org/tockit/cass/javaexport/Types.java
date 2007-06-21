package org.tockit.cass.javaexport;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Class containing constants for all the types in the CASS RDF model.
 */
public class Types {
    private static Model model = ModelFactory.createDefaultModel();
	public static final Resource PACKAGE = model.createResource(Namespaces.CASS_TYPES + "package");
	public static final Resource INTERFACE = model.createProperty(Namespaces.CASS_TYPES + "interface");
	public static final Resource CLASS = model.createProperty(Namespaces.CASS_TYPES + "class");
	public static final Resource METHOD = model.createProperty(Namespaces.CASS_TYPES + "method");
	public static final Resource COMPILATION_UNIT = model.createProperty(Namespaces.CASS_TYPES + "compilationUnit");
}
