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
        String queryMapId = tokenizer.popCurrentToken();
        QueryMap queryMap = new QueryMap(targetFile, queryMapId);

        int line = tokenizer.getCurrentLine();
        tokenizer.consumeToken("=", targetFile);
        while (tokenizer.getCurrentLine() == line) {
            tokenizer.advance(); // skip possible remarks
        }

        while (!tokenizer.getCurrentToken().equals(";")) {
            tokenizer.consumeToken("(", targetFile);
            String clause = tokenizer.popCurrentToken();
            tokenizer.consumeToken(",", targetFile);
            String id = tokenizer.popCurrentToken();
            tokenizer.consumeToken(")", targetFile);
            queryMap.addEntry(clause, id);
        }

        tokenizer.consumeToken(";", targetFile);
    }
}