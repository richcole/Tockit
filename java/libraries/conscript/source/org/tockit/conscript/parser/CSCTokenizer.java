/*
 * Created on 9/04/2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.tockit.conscript.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CSCTokenizer {
    private BufferedReader inputReader;
    private String currentToken;
    private int currentLine = 1;
    private boolean newLineStarted = true;

	public CSCTokenizer(File file) throws IOException, DataFormatException {
	    this.inputReader = new BufferedReader(new FileReader(file));
	    advance();
	}
	
	public String getCurrentToken() {
		return this.currentToken;
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
    }


    public void advanceNormal(int character) throws IOException {
        while( character != -1 && !Character.isWhitespace((char)character) &&
                character != '\"' && character != '(' ) {
            this.currentToken += (char) character;
            character = this.inputReader.read();
        }
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
}