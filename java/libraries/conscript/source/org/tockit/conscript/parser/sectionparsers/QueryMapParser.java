/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.IOException;

import org.tockit.conscript.model.ConceptualFile;
import org.tockit.conscript.model.QueryMap;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class QueryMapParser extends CSCFileSectionParser {
    public String getStartToken() {
        return "QUERY_MAP";
    }

    public void parse(CSCTokenizer tokenizer, ConceptualFile targetFile)
        throws IOException, DataFormatException {
        QueryMap retval = new QueryMap();
        retval.setIdentifier(tokenizer.getCurrentToken());
        tokenizer.advance();

        int line = tokenizer.getCurrentLine();
        consumeToken(tokenizer, "=");
        while (tokenizer.getCurrentLine() == line) {
            tokenizer.advance(); // skip possible remarks
        }

        /// @todo tupels should be send as a couple of tokens
        while (!tokenizer.getCurrentToken().equals(";")) {
            String tupel = tokenizer.getCurrentString();
            tokenizer.advance();
            tupel = tupel.substring(1, tupel.length() - 1);
            int commaPos = tupel.indexOf(',');
            String clause = tupel.substring(0, commaPos).trim();
            clause = clause.substring(1, clause.length() - 1);
            String id = tupel.substring(commaPos + 1).trim();
            retval.getMap().put(id, clause);
        }

        consumeToken(tokenizer, ";");
    }
}