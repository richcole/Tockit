package net.sourceforge.tockit.toscanaj.data;

import net.sourceforge.tockit.toscanaj.data.ConceptualSchema;

/**
 * A factory for creating instances of the Concept interface.
 *
 * What this interface offers are methods for creating concepts from the data
 * given. It is abstracting the way the data is stored -- there is a uniform
 * access to the data if it is in memory, in a relational database or whereever.
 */
public class ConceptFactory {
    /**
     * The conceptual schema we use.
     */
    ConceptualSchema schema;

    /**
     * Creates a new factory operating on the given conceptual schema.
     */
    ConceptFactory(ConceptualSchema schema) {
        this.schema = schema;
    }

    /**
     * Creates a concept for a simple (i.e. non-nested) diagram.
     *
     * The lattice and the point are identified using the identity relation on
     * Java objects, this could be numbers, strings, whatever.
     */
    Concept getConcept(Object lattice, Object point) {
        /// TODO: implement this
    }
}