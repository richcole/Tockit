/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.util.tests;

import org.tockit.util.StringTokenizer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StringTokenizerTest extends TestCase {
    private final String[] RESULT = new String[] {"one","two,three","four\'five,","&six"};
    private StringTokenizer tokenizer;
    
    public StringTokenizerTest(String s) {
        super(s);
    }
    
    public static Test suite() {
        return new TestSuite(StringTokenizerTest.class);
    }
    
    public void testIterator() {
        assertEquals(RESULT[0], tokenizer.nextToken());
        assertEquals(RESULT[1], tokenizer.nextToken());
        assertEquals(RESULT[2], tokenizer.nextToken());
        assertEquals(RESULT[3], tokenizer.nextToken());
    }
    
    public void testTokenArray() {
        String[] allTokens = tokenizer.tokenizeAll();
        assertEquals(RESULT.length, allTokens.length);
        for (int i = 0; i < allTokens.length; i++) {
            assertEquals(RESULT[i], allTokens[i]);
        }
    }
    
    protected void setUp() throws Exception {
        this.tokenizer = new StringTokenizer(
                "one,'two,three','four&'five'&,,&&six",
                ',',
                '\'',
                '&');
    }
}
