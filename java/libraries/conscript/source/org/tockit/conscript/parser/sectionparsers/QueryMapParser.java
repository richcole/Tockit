/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.conscript.parser.sectionparsers;

import java.io.IOException;
import java.util.logging.Level;

import org.tockit.conscript.model.CSCFile;
import org.tockit.conscript.model.QueryMap;
import org.tockit.conscript.parser.CSCParser;
import org.tockit.conscript.parser.CSCTokenizer;
import org.tockit.conscript.parser.DataFormatException;

class QueryMapParser extends CSCFileSectionParser {
    public String getStartToken() {
        return "QUERY_MAP";
    }

    public void parse(CSCTokenizer tokenizer, CSCFile file)
        throws IOException, DataFormatException {
        String queryMapId = tokenizer.popCurrentToken();
        QueryMap queryMap = new QueryMap(file, queryMapId);

        int line = tokenizer.getCurrentLine();
        tokenizer.consumeToken("=", file);
        while (tokenizer.getCurrentLine() == line) {
            tokenizer.advance(); // skip possible remarks
        }

        while (!tokenizer.getCurrentToken().equals(";")) {
            tokenizer.consumeToken("(", file);
            String clause = tokenizer.popCurrentToken();
            tokenizer.consumeToken(",", file);
            String id = tokenizer.popCurrentToken();
            tokenizer.consumeToken(")", file);
            queryMap.addEntry(clause, id);
        }

        tokenizer.consumeToken(";", file);

        file.add(queryMap);
        CSCParser.logger.log(Level.FINER, "Query map added: '" + queryMap.getName() + "'");
    }
}