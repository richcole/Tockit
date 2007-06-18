package org.tockit.cass.javaexport;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class Types {
    private static Model model = ModelFactory.createDefaultModel();
	public static final Resource PACKAGE = model.createResource("http://tockit.org/cass/types/package");
	public static final Resource INTERFACE = model.createProperty("http://tockit.org/cass/types/interface");
	public static final Resource CLASS = model.createProperty("http://tockit.org/cass/types/class");
	public static final Resource METHOD = model.createProperty("http://tockit.org/cass/types/method");
	public static final Resource COMPILATION_UNIT = model.createProperty("http://tockit.org/cass/types/compilationUnit");
}
