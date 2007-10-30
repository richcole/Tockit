/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package org.tockit.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RelationImportPanel extends JPanel {
    private static class CharacterSelector {
        private char character;
        private String displayName;
        
        public CharacterSelector(char character, String displayName) {
            this.character = character;
            this.displayName = displayName;
        }
        
        public char getCharacter() {
            return this.character;
        }
        
        @Override
		public String toString() {
            return this.displayName;
        }
    }
    
    private static final CharacterSelector[] SEPARATORS = new CharacterSelector[]{
            new CharacterSelector('\t', "Tab"),
            new CharacterSelector(',', "Comma"),
            new CharacterSelector(';', "Semicolon"),
            new CharacterSelector(' ', "Space")
    };
    
    private static final CharacterSelector[] QUOTES = new CharacterSelector[]{
            new CharacterSelector('\"', "Double Quote"),
            new CharacterSelector('\'', "Single Quote"),
            new CharacterSelector('\000', "None (\\000)")
    };
    
    private static final CharacterSelector[] ESCAPES = new CharacterSelector[]{
            new CharacterSelector('\\', "Backslash"),
            new CharacterSelector('$', "Dollar"),
            new CharacterSelector('%', "Percent"),
            new CharacterSelector('!', "Exclamation Mark"),
            new CharacterSelector('\000', "None (\\000)")
    };
    
    private JComboBox separatorComboBox = new JComboBox(SEPARATORS);
    private JComboBox quoteComboBox = new JComboBox(QUOTES);
    private JComboBox escapeComboBox = new JComboBox(ESCAPES);
    private JCheckBox headerCheckBox = new JCheckBox("First line is header", true);

    public RelationImportPanel() {
        this.setLayout(new GridBagLayout());
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = GridBagConstraints.RELATIVE;
        labelConstraints.weightx = 0;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.insets = new Insets(0,0,0,5);
        
        GridBagConstraints comboConstraints = new GridBagConstraints();
        comboConstraints.gridx = 1;
        comboConstraints.gridy = GridBagConstraints.RELATIVE;
        comboConstraints.weightx = 1;
        comboConstraints.fill = GridBagConstraints.HORIZONTAL;
                
        GridBagConstraints checkBoxConstraints = new GridBagConstraints();
        checkBoxConstraints.gridx = 0;
        checkBoxConstraints.gridwidth = 2;
        checkBoxConstraints.weightx = 1;
        checkBoxConstraints.anchor = GridBagConstraints.EAST;
        checkBoxConstraints.insets = new Insets(10,0,0,0);
        
        this.add(new JLabel("Separator:"), labelConstraints);
        this.add(this.separatorComboBox, comboConstraints);
        this.add(new JLabel("Quote:"), labelConstraints);
        this.add(this.quoteComboBox, comboConstraints);
        this.add(new JLabel("Escape:"), labelConstraints);
        this.add(this.escapeComboBox, comboConstraints);
        this.add(this.headerCheckBox, checkBoxConstraints);
    }
    
    public char getSeparator() {
        return ((CharacterSelector) this.separatorComboBox.getSelectedItem()).getCharacter();
    }
    
    public char getQuote() {
        return ((CharacterSelector) this.quoteComboBox.getSelectedItem()).getCharacter();
    }
    
    public char getEscape() {
        return ((CharacterSelector) this.escapeComboBox.getSelectedItem()).getCharacter();
    }
    
    public boolean firstLineIsHeader() {
        return this.headerCheckBox.isSelected();
    }
}
