/*
 * Created on 9/04/2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.tockit.conscript.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;

public class CSCTokenizer {
    private BufferedReader inputReader;
    private String currentToken;
    private int currentLine = 1;
    private boolean newLineStarted = true;

	public CSCTokenizer(Reader in) throws IOException, DataFormatException {
	    this.inputReader = new BufferedReader(in);
	    advance();
	}
	
	public String getCurrentToken() {
		return this.currentToken;
	}
	
	/**
	 * Returns the current token as a string with all escape codes resolved.
	 */
	public String getCurrentString() {
		return resolveEscapes(this.currentToken);
	}
	
	public void advance() throws IOException, DataFormatException {
		int character;
		this.newLineStarted = false;
		do {
			character = this.inputReader.read();
			if(character == '\n') {
				this.currentLine += 1;
				this.newLineStarted = true;
			}
		} while(Character.isWhitespace((char)character));
		this.currentToken = "";
		if(character == '\"') {
			advanceString();
		} else if (character == '(') {
			advanceTupel();
		} else {
            advanceNormal(character);
		}
	}

    public void advanceString() throws IOException, DataFormatException {
        int character = this.inputReader.read();
        int startLine = this.currentLine;
        while( character != -1 && character != '\"' ) {
            this.currentToken += (char) character;
            character = this.inputReader.read();
            if(character == '\\') {
            	this.currentToken += (char) this.inputReader.read();
                character = this.inputReader.read();
            }                	
        }
        if(character != '\"') {
            throw new DataFormatException("Open quote from line " + startLine + " not matched.");
        }
        CSCParser.logger.log(Level.FINEST, "Tokenizer found string '" + this.currentToken + "'");
    }

    public void advanceTupel() throws IOException, DataFormatException {
        int character = '(';
        int startLine = this.currentLine;
        do {
            this.currentToken += (char) character;
            character = this.inputReader.read();
        } while( character != -1 && character != ')' );
        if(character != ')') {
        	throw new DataFormatException("Open parenthesis from line " + startLine + " not matched.");
        } else {
        	this.currentToken += ")";
        }
		CSCParser.logger.log(Level.FINEST, "Tokenizer found tupel '" + this.currentToken + "'");
    }


    public void advanceNormal(int character) throws IOException {
        while( character != -1 && !Character.isWhitespace((char)character) &&
                character != '\"' && character != '(' ) {
            this.currentToken += (char) character;
            character = this.inputReader.read();
        }
		CSCParser.logger.log(Level.FINEST, "Tokenizer found token '" + this.currentToken + "'");
    }

	public boolean done() throws IOException {
		return !this.inputReader.ready();
	}
	
    public int getCurrentLine() {
        return this.currentLine;
    }
    
    public boolean newLineHasStarted() {
    	return this.newLineStarted;
    }

	private static String resolveEscapes(String input) {
		String output = "";
		for(int i = 0; i < input.length(); i++) {
			char curChar = input.charAt(i);
			if(curChar == '\\') {
				i++;
				output += input.charAt(i);
			} else {
				output += curChar;
			}
		}
		return output;
	}
}