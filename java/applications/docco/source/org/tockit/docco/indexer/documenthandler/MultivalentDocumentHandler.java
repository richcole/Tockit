/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer.documenthandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import org.tockit.docco.indexer.DocumentContent;
import org.tockit.docco.indexer.DocumentSummary;

import phelps.lang.Integers;
import phelps.net.CachedInputStream;

import multivalent.Behavior;
import multivalent.Cache;
import multivalent.Document;
import multivalent.INode;
import multivalent.MediaAdaptor;
import multivalent.Multivalent;
import multivalent.Node;
import multivalent.node.FixedI;
import multivalent.node.LeafText;


/**
 * Should be able to parse scanned paper, PDF, HTML, UNIX manual pages, TeX DVI.
 * 
 * this parser is largerly based on tool.ExtractText code from Multivalent project
 * (http://www.cs.berkeley.edu/~phelps/Multivalent/), the only addition to the core 
 * code is ability to exract metadata.
 * 
 * 
 * 
 * Multivalent license:
 * 
 * Parts copyright (c) 1996 The Regents of the University of California.
 * All rights reserved.
 * 
 * Permission is hereby granted, without written agreement and without
 * license or royalty fees, to use, copy, modify, and distribute this
 * software and its documentation for any purpose, provided that the
 * above copyright notice and the following two paragraphs appear in
 * all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 */
public class MultivalentDocumentHandler implements DocumentHandler {
	
	public DocumentSummary parseDocument(File file) throws IOException, DocumentHandlerException {
		URI uri = file.getCanonicalFile().toURI();	
		
		DocumentSummary result = new DocumentSummary();
		
		// @todo make use of mime types...? 
		String mimeType = null;
		
		Document doc = new Document("top", null, null);
		Cache cache = Multivalent.getInstance().getCache();
		String genre = cache.getGenre(mimeType, uri.toString(), null);
		if ("RawImage".equals(genre) || genre == "ASCII"/*null*/) return null;  // defaulted to intern()'ed ASCII

		// media adaptors are obligated to perform outside of a browser context too
		MediaAdaptor mediaAdapter = (MediaAdaptor)Behavior.getInstance(genre, genre, null, null, null);
		File infile = "file".equals(uri.getScheme())? new File(uri.getPath()): null;
		InputStream is = new CachedInputStream(uri.toURL().openStream(), infile, null);
		mediaAdapter.setInputStream(is);
		mediaAdapter.setHints(MediaAdaptor.HINT_NO_IMAGE | MediaAdaptor.HINT_NO_SHAPE | MediaAdaptor.HINT_EXACT | MediaAdaptor.HINT_NO_TRANSCLUSIONS);    // only want text

		mediaAdapter.docURI = uri;
		
		try {
			mediaAdapter.parse(doc);

			if (doc.getAttr("author") != null) {
				List authors = new LinkedList();
				authors.add(doc.getAttr("author"));
				result.authors = authors;
			}
			
			if (doc.getAttr("title") != null) {
				result.title = doc.getAttr("title");
			}

			if (doc.getAttr("summary") != null) {
				result.summary = doc.getAttr("summary");
			}

			if (doc.getAttr("keywords") != null) {
				result.summary = doc.getAttr("keywords");
			}

			result.content = getDocumentContent(doc, mediaAdapter);

			mediaAdapter.closeInputStream();
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentHandlerException("Caught Exception", e);
		}
	}



	private DocumentContent getDocumentContent(Document doc, MediaAdaptor mediaAdapter)
										throws Exception, IOException {
		StringBuffer stringBuffer = new StringBuffer(20 * 1000);
		
		int page = Math.max(1, Integers.parseInt(doc.getAttr(Document.ATTR_PAGE), -1)), pagecnt = Integers.parseInt(doc.getAttr(Document.ATTR_PAGECOUNT), -1);
		if (pagecnt<=0) {   // single long scroll
			extractBody(doc, stringBuffer);
		
		} else for (int i=page,imax=page+pagecnt; i<imax; i++) {    // multipage
			doc.clear();
			doc.putAttr(Document.ATTR_PAGE, Integer.toString(i));
			doc.putAttr(Document.ATTR_PAGECOUNT, Integer.toString(pagecnt));    // zapped by doc.clear()
			mediaAdapter.parse(doc);
			extractBody(doc, stringBuffer);
		}
		return new DocumentContent(stringBuffer.toString());
	}
	
	private void extractBody(Node n, StringBuffer sb) {
	  if (n==null) return;    // in case of bug in media adaptor

	  if (n.breakBefore()) sb.append("\n");

	  if (n.isLeaf()) {
		  if (n instanceof LeafText) {
			  sb.append(n.getName());     // if -ascii, strain out Unicode (>=256? >=128?)
		  }

	  } else { 
		  //assert(n.isStruct());
	  	  if (! n.isStruct()) {
	  	  	throw new RuntimeException("Assertion failure");
	  	  }
		  INode p = (INode)n;
		  for (int i=0,imax=p.size(); i<imax; i++) {
			  extractBody(p.childAt(i), sb);
			  sb.append(" ");
		  }

	  }

	  if (n.breakAfter() || n instanceof FixedI) sb.append("\n");
	}

	public String getDisplayName() {
		return "Handler based on Multivalent tool, capable of parsing " + 
			"HTML, PDF, scanned paper, UNIX manual pages, TeX DVI";
	}

	

}
