/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.docco.indexer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
 */
public class MultivalentDocumentProcessor implements DocumentProcessor {
	private URI uri;
	
	private List authors;
	private String title;
	private String summary;
	private Date modDate;
	private String keywords;

	public void readDocument(File file) throws IOException {
		this.uri = file.getCanonicalFile().toURI();	
	}

	public DocumentContent getDocumentContent() throws IOException, DocumentProcessingException {
		// @todo make use of mime types...? 
		String mimeType = null;
		
		StringBuffer stringBuffer = new StringBuffer(20 * 1000);

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
				this.authors = new LinkedList();
				this.authors.add(doc.getAttr("author"));
			}
			
			if (doc.getAttr("title") != null) {
				this.title = doc.getAttr("title");
			}

			if (doc.getAttr("summary") != null) {
				this.summary = doc.getAttr("summary");
			}
			
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
	
			mediaAdapter.closeInputStream();
			return new DocumentContent(stringBuffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentProcessingException(e);
		}
	}

	public List getAuthors() {
		return this.authors;
	}

	public String getTitle() {
		return this.title;
	}

	public String getSummary() {
		return this.summary;
	}

	public Date getModificationDate() {
		return this.modDate;
	}

	public String getKeywords() {
		return this.keywords;
	}
	
	private void extractBody(Node n, StringBuffer sb) {
	  if (n==null) return;    // in case of bug in media adaptor

	  if (n.breakBefore()) sb.append("\n");

	  if (n.isLeaf()) {
		  if (n instanceof LeafText) {
			  sb.append(n.getName());     // if -ascii, strain out Unicode (>=256? >=128?)
		  }

	  } else { 
	  	  //assert 
	  	  n.isStruct();
		  INode p = (INode)n;
		  for (int i=0,imax=p.size(); i<imax; i++) {
			  extractBody(p.childAt(i), sb);
			  sb.append(" ");
		  }

	  }

	  if (n.breakAfter() || n instanceof FixedI) sb.append("\n");
	}
	

}
