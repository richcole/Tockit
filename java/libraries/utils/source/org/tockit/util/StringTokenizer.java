package org.tockit.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * A class splitting a string into substring along separating characters.
 * 
 * This is similar to #java.util.StringTokenizer, but offers quotes and
 * escaping. Additionally empty tokens (two or more separators in a row)
 * will not be skipped. The class is instantiated and then used as any
 * other Iterator implementation.
 */
public class StringTokenizer implements Iterator {
    private String string;
    private char separator;
    private char quote;
    private char escape;
    private int nextChar;
        
    /**
     * Constructs a new string tokenizer.
     * 
     * The quote character can be used to group sections into tokens even if
     * they contain the separator.
     * 
     * The escape character can be used to enforce the addition of a character
     * to a token -- both outside and inside of quotes.
     * 
     * Example:
     *    StringTokenizer tokenizer = new StringTokenizer(
     *                                         "one,'two,three','four&'five'&,,&&six",
     *                                         ',',
     *                                         '\'',
     *                                         '&');
     * 
     * will find the tokens:
     * - "one"
     * - "two,three"
     * - "four'five,
     * - "&six"
     * 
     * @param string the string to be tokenized
     * @param separator the character separating two tokens
     * @param quote the character denoting regions that do not get tokenized
     * @param escape a character that forces the next character to be added to
     *               the current token
     */
    public StringTokenizer(String string, char separator, char quote, char escape) {
        this.string = string;
        this.separator = separator;
        this.quote = quote;
        this.escape = escape;
        this.nextChar = 0;
    }

    /** 
     * Implements Iterator interface by throwing an exception.
     * 
     * @throws UnsupportedOperationException always
     */
    public void remove() {
        throw new UnsupportedOperationException("This iterator can not remove anthing");
    }

    /**
     * Checks if more tokens are available.
     * 
     * @return true iff there is at least one token left
     */
    public boolean hasNext() {
        return this.nextChar < this.string.length();
    }

    /**
     * Returns the next token and advances the pointer.
     * 
     * @return the next token
     */
    public Object next() {
        StringBuffer retVal = new StringBuffer();
        boolean inQuotes = false;
        do {
            char curChar = this.string.charAt(this.nextChar);
            nextChar++;
            if((curChar == this.separator) && !inQuotes) {
                break;
            } else if(curChar == this.quote) {
                inQuotes = !inQuotes;
            } else if(curChar == this.escape) {
                retVal.append(this.string.charAt(this.nextChar));
                nextChar++;
            } else {
                retVal.append(curChar);
            }
        } while (this.nextChar < this.string.length());
        return retVal.toString();
    }
    
    /**
     * Returns the next token as string.
     * 
     * This is doing the same as next() just with a different
     * return value.
     * 
     * @return the next token
     */
    public String nextToken() {
        return (String) next();
    }
    
    /**
     * Returns all tokens found as array.
     * 
     * @return all tokens found in the original string
     */
    public String[] tokenizeAll() {
        List result = new ArrayList();
        while(this.hasNext()) {
            result.add(next());
        }
        return (String[]) result.toArray(new String[] {});
    }
}
