package org.tockit.cass.javaexport;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class Properties {
    private static Model model = ModelFactory.createDefaultModel();
	public static final Property CONTAINS = model.createProperty("http://tockit.org/cass/properties/contains");
	public static final Property CALLS = model.createProperty("http://tockit.org/cass/properties/calls");
	public static final Property TYPE = model.createProperty("http://tockit.org/cass/properties/type");
}
