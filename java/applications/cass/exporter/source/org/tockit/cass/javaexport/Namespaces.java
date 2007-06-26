package org.tockit.cass.javaexport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class containing constants for all the namespaces of the CASS RDF model.
 *
 * The main method prints the prefix mappings in SPARQL syntax.
 */
public class Namespaces {
	public static final String CASS_TYPES = "http://tockit.org/cass/entityTypes#";
	public static final String CASS_PROPERTIES = "http://tockit.org/cass/properties#";
	public static final String PACKAGES = "http://tockit.org/cass/packages#";
	public static final String TYPES = "http://tockit.org/cass/types#";
	public static final String METHODS = "http://tockit.org/cass/methods#";
	
	public static final Map PREFIX_MAPPING = new HashMap() {{
		put("cassType", CASS_TYPES);
		put("cassProp", CASS_PROPERTIES);
		put("package", PACKAGES);
		put("type", TYPES);
		put("method", METHODS);
	}};
	
	public static void main(String[] args) {
		for (Iterator iter = PREFIX_MAPPING.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			System.out.println("PREFIX " + entry.getKey() + ": <" + entry.getValue() + ">");
		}
	}
}
