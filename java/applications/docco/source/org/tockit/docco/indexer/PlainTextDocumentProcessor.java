/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.tockit.docco.GlobalConstants;


public class PlainTextDocumentProcessor implements DocumentProcessor {

	public Document getDocument(File file) throws FileNotFoundException {
		Document doc = new Document();

		doc.add(Field.Text(GlobalConstants.FIELD_DOC_PATH, file.getPath()));

		doc.add(Field.Keyword(GlobalConstants.FIELD_DOC_DATE,
				  DateField.timeToString(file.lastModified())));

		//FileInputStream is = new FileInputStream(file);
		//Reader reader = new BufferedReader(new InputStreamReader(is));
		Reader reader = new FileReader(file);
		doc.add(Field.Text(GlobalConstants.FIELD_QUERY_BODY, reader));
		
		System.out.println(doc.toString());
		System.out.println("contents: " + doc.getField(GlobalConstants.FIELD_QUERY_BODY).stringValue());

		return doc;
	}

}
