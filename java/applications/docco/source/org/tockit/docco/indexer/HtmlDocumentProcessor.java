/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.htmlparser.NodeReader;
import org.htmlparser.Parser;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.visitors.ObjectFindingVisitor;
import org.htmlparser.visitors.TagFindingVisitor;
import org.htmlparser.visitors.TextExtractingVisitor;
import org.tockit.docco.GlobalConstants;

public class HtmlDocumentProcessor implements DocumentProcessor {

	public Document getDocument(File file) throws Exception {
		Document doc = new Document();
		
		FileReader fileReader = new FileReader(file);
		BufferedReader br = new BufferedReader(fileReader);
		NodeReader nodeReader = new NodeReader(br, 2048);
		Parser parser = new Parser(nodeReader);
		
		TextExtractingVisitor visitor = new TextExtractingVisitor();
		ObjectFindingVisitor metaTagVisitor = new ObjectFindingVisitor(MetaTag.class, true);
		
		ObjectFindingVisitor titleVisitor = new ObjectFindingVisitor(TitleTag.class, true);

		String[] metaTags = {"META","TITLE"};
		TagFindingVisitor tagVisitor = new TagFindingVisitor(metaTags,true);

		parser.registerScanners();

		parser.visitAllNodesWith(visitor);
		String extractedText = visitor.getExtractedText();

//		parser.visitAllNodesWith(metaTagVisitor);
//		System.out.println("*********************");
//		Node[] metaNodes = metaTagVisitor.getTags();
//		for (int i=0; i < metaNodes.length; i++) {
//		  MetaTag curTag = (MetaTag) metaNodes[i];
//		  System.out.println("Meta tag: " + curTag);
//		}		
//		System.out.println("*********************");
//
//		parser.visitAllNodesWith(titleVisitor);
//		Node[] titleNodes = titleVisitor.getTags();
//		for (int i=0; i < titleNodes.length; i++) {
//		  TitleTag curTag = (TitleTag) titleNodes[i];
//		  System.out.println("Title tag: " + curTag);
//		}
//		
//		parser.visitAllNodesWith(tagVisitor);
//		Node[] tags = tagVisitor.getTags(0);
//		System.out.println("meta tags: " + tags.length);
//		Node[] tags2 = tagVisitor.getTags(1);
//		System.out.println("title tags: " + tags.length);		

		doc.add(Field.Text(GlobalConstants.FIELD_QUERY_BODY, extractedText));		
		return doc;
	}
}
