//package org.apache.lucene.demo;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Lucene" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Lucene", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;

import query.HitReference;
import query.HitReferencesSet;
import query.QueryEngine;

import docsearcher.DocSearch;

class SearchFiles {
	private String indexLocation = DocSearch.indexDir + "/test";
	private String defaultQueryField = "body";
	private QueryEngine queryEngine;
	
	public SearchFiles()  throws IOException {
		this.queryEngine = new QueryEngine(this.indexLocation, this.defaultQueryField, new StandardAnalyzer());
	}
	
	public HitReferencesSet query (String queryString) throws IOException, ParseException{
		return queryEngine.executeQuery(queryString);
	}
	
	public void stop() throws IOException {
		this.queryEngine.finishQueries();
	}
	
  
	public static void printResults(HitReferencesSet hits) throws IOException {
		System.out.println(hits.size() + " total matching documents");
		
		Iterator it = hits.iterator();
		int count = 1;
		while (it.hasNext()) {
			HitReference hitRef = (HitReference) it.next();
			String path = hitRef.getDocument().get("path");
			if (path != null) {
				  System.out.println(count + ". " + path);
			} else {
			  String url = hitRef.getDocument().get("url");
			  if (url != null) {
				System.out.println(count + ". " + url);
				System.out.println("   - " + hitRef.getDocument().get("title"));
			  } else {
				System.out.println(count + ". " + "No path nor URL for this document");
			  }
			}
			System.out.println("\tscore: " + hitRef.getScore());
			count++;	
		}	
	}
	
	public void repetativeQuery () throws IOException, ParseException {
		BufferedReader in =
			new BufferedReader(new InputStreamReader(System.in));
		
		while (true) {
			System.out.print("Query: ");
			String line = in.readLine();
				
			if (line.length() == -1)
				break;
	
			queryEngine.breakQueryIntoTerms(line);
			
			//HitReferencesSet hits =	query(line);
			//printResults(hits);
		}
		stop();
	}
	
	public void singleQuery ()  throws IOException, ParseException {
		BufferedReader in =
			new BufferedReader(new InputStreamReader(System.in));
		
		System.out.print("Query: ");
		String line = in.readLine();
			
		if (line.length() == -1)
			return;

		HitReferencesSet hits =	query(line);
		//printResults(hits);

		stop();
	}

	public static void main (String[] args) {
		boolean repeatQuery = false;
		if (args.length > 0) {
			if (args[0].equals("repeatQuery")) {
				repeatQuery = true;
			}
			if ((args[0].equals("help")) || (args[0].equals("?")) ) {
				System.out.println("SearchFiles [repeatQuery | help | ? ]");
				System.exit(0);
			}
		}

		try {
			SearchFiles sf = new SearchFiles();
			if (repeatQuery) {
				sf.repetativeQuery();
			}
			else {
				sf.singleQuery();
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(
				" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

}
