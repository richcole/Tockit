/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.context.model;

import org.tockit.util.ListSet;


public interface ListsContext extends Context {
    ListSet getObjectList();
    ListSet getAttributeList();
}
