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

import org.tockit.conscript.model.ConceptualFile;

public class CSCTokenizer {
    private BufferedReader inputReader;
    private String currentToken;
    private int currentLine = 1;
    private boolean newLineStarted = true;
    private int characterWaiting = -1;

	public CSCTokenizer(Reader in) throws IOException, DataFormatException {
	    this.inputReader = new BufferedReader(in);
	    advance();
	}
	
	public String getCurrentToken() {
		return this.currentToken;
	}

    /**
     * Advances and then return the token current before.
     * 
     * Convenience method equivalent to calling getCurrentToken() and then advance().
     * Note that this method has two effects at once and it thus not proper coding style,
     * but it is just more handy to use.
     */
    public String popCurrentToken() throws IOException, DataFormatException {
        String token = this.currentToken;
        advance();
        return token;
    }
	
	public void advance() throws IOException, DataFormatException {
        if(this.characterWaiting != -1) {
            this.currentToken = "" + (char)this.characterWaiting;
            this.characterWaiting = -1;
            CSCParser.logger.log(Level.FINEST, "Tokenizer generized token '" + this.currentToken + "'");
            return;
        }
        
        if(done()) {
            throw new IOException("CSCTokenizer.advance() called after end of file");
        }
        
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
		} else {
            advanceNormal(character);
		}
	}

    private void advanceString() throws IOException, DataFormatException {
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

    private void advanceNormal(int character) throws IOException {
        if(character == ',' || character == ')') {
            this.currentToken += (char) character;
        } else {
            while( character != -1 && !Character.isWhitespace((char)character) &&
                    character != '\"') {
                if(character == ',' || character == ')') {
                    this.characterWaiting = character;
                    break;
                }
                this.currentToken += (char) character;
                if(character == '(') {
                    break;
                }
                character = this.inputReader.read();
            }
        }
		CSCParser.logger.log(Level.FINEST, "Tokenizer found token '" + this.currentToken + "'");
    }

	public boolean done() throws IOException {
		return !this.inputReader.ready() && this.characterWaiting == -1;
	}
	
    public int getCurrentLine() {
        return this.currentLine;
    }
    
    public boolean newLineHasStarted() {
    	return this.newLineStarted;
    }

    public void consumeToken(String token, ConceptualFile file) throws IOException, DataFormatException{
        if(!this.currentToken.equals(token)) {
            throw new DataFormatException("Expected token '" + token + "' but found '" + this.currentToken + 
                                          "' in line " + getCurrentLine() + " of file " + file.getFile());
        }
        if(!done()) {
            advance();
        }
    }
}