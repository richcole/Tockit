/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $ID$
 */
package org.tockit.swing.preferences;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class PreferencePanel extends JTabbedPane {
    private static final Dimension FIELD_SIZE = new Dimension(60,20);

    private HashMap changes = new HashMap();

    public PreferencePanel(ConfigurationSection[] sections, Component parent) {
        for (int i = 0; i < sections.length; i++) {
            ConfigurationSection section = sections[i];
            addTab(section.getName(), createSectionPane(section, parent));
        }
    }

    private Component createSectionPane(ConfigurationSection section, Component parent) {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        
        for (int i = 0; i < section.getSubsections().length; i++) {
            ConfigurationSubsection subsection = section.getSubsections()[i];
            panel.add(createSubsectionPane(subsection, parent), constraints);
        }
        
        constraints.weighty = 1;
        panel.add(new JPanel(), constraints);
        
        return panel;
    }

    private Component createSubsectionPane(ConfigurationSubsection subsection, Component parent) {
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.weightx = 1;
        labelConstraints.insets = new Insets(0,5,5,5);
        
        GridBagConstraints toolConstraints = new GridBagConstraints();
        toolConstraints.gridy = 0;
        toolConstraints.gridy = 0;
        toolConstraints.anchor = GridBagConstraints.WEST;
        toolConstraints.fill = GridBagConstraints.HORIZONTAL;
        toolConstraints.weightx = 0;
        toolConstraints.insets = labelConstraints.insets;

        for (int i = 0; i < subsection.getEntries().length; i++) {
            ConfigurationEntry entry = subsection.getEntries()[i];
            panel.add(new JLabel(entry.getName()), labelConstraints);
            panel.add(createEntryTool(entry, parent), toolConstraints);
            labelConstraints.gridy++;
            toolConstraints.gridy++;
        }
        
        labelConstraints.weighty = 1;
        panel.add(new JPanel(), labelConstraints);
        
        panel.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.DARK_GRAY), subsection.getName()));
        
        return panel;
    }

    private Component createEntryTool(final ConfigurationEntry entry, final Component parent) {
        final ExtendedPreferences prefs = entry.getNode();
        if(entry.getType() == ConfigurationType.BOOLEAN) {
            final JCheckBox checkbox = new JCheckBox();
            checkbox.setSelected(prefs.getBoolean(entry.getKey(), false));
            checkbox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    changes.put(entry, new Boolean(checkbox.isSelected()));
                }
            });
            return checkbox;
        } else if(entry.getType() == ConfigurationType.COLOR) {
            final JButton button = new JButton();
            button.setMinimumSize(new Dimension(70,20));
            button.setPreferredSize(new Dimension(70,20));
            Color color = prefs.getColor(entry.getKey(), null);
            if(color != null) {
                button.setBackground(color);
            }
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Color color = prefs.getColor(entry.getKey(), null);
                    Color newColor = JColorChooser.showDialog(parent, "Select new color", color);
                    if(newColor != null) {
                        button.setBackground(newColor);
                        changes.put(entry, newColor);
                    }
                }}
            );
            return button;
        } else if(entry.getType() == ConfigurationType.DOUBLE) {
            final JFormattedTextField field = new JFormattedTextField(NumberFormat.getNumberInstance());
            field.setHorizontalAlignment(JTextField.RIGHT);
            field.setText("" + prefs.getDouble(entry.getKey(), 0));
            field.setInputVerifier(new InputVerifier() {
                public boolean verify(JComponent input) {
                    try {
                        Double value = new Double(field.getText());
                        prefs.putDouble(entry.getKey(), value.doubleValue());
                        return true;
                    } catch (NumberFormatException nfe) {
                        return false;
                    }
                }
            });
            field.setMinimumSize(FIELD_SIZE);
            field.setPreferredSize(FIELD_SIZE);
            return field;
        } else if(entry.getType() == ConfigurationType.INTEGER) {
            final JFormattedTextField field = new JFormattedTextField(NumberFormat.getIntegerInstance());
            field.setHorizontalAlignment(JTextField.RIGHT);
            field.setText("" + prefs.getInt(entry.getKey(), 0));
            field.setInputVerifier(new InputVerifier() {
                public boolean verify(JComponent input) {
                    try {
                        Integer value = new Integer(field.getText());
                        prefs.putInt(entry.getKey(), value.intValue());
                        return true;
                    } catch (NumberFormatException nfe) {
                        return false;
                    }
                }
            });
            field.setMinimumSize(FIELD_SIZE);
            field.setPreferredSize(FIELD_SIZE);
            return field;
        } else if(entry.getType() == ConfigurationType.STRING) {
            final JTextField field = new JTextField();
            field.setHorizontalAlignment(JTextField.RIGHT);
            field.setText(prefs.get(entry.getKey(), ""));
            field.setInputVerifier(new InputVerifier() {
                public boolean verify(JComponent input) {
                    prefs.put(entry.getKey(), field.getText());
                    return true;
                }
            });
            field.setMinimumSize(FIELD_SIZE);
            field.setPreferredSize(FIELD_SIZE);
            return field;
        } else if(entry.getType() == ConfigurationType.FONT_FAMILY) {
            return createCombobox(entry, GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        } else if(entry.getType() instanceof ConfigurationType.Enumeration) {
            return createCombobox(entry, ((ConfigurationType.Enumeration)entry.getType()).getValues());
        } else {
            throw new RuntimeException("Unknown ConfigurationType");
        }
    }
    
    private JComboBox createCombobox(final ConfigurationEntry entry, String[] items) {
        final ExtendedPreferences prefs = entry.getNode();
        final JComboBox comboBox = new JComboBox(items);
        comboBox.setSelectedItem(prefs.get(entry.getKey(), null));
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                prefs.put(entry.getKey(), (String) comboBox.getSelectedItem());
            }
        });
        return comboBox;
    }

    public void applyChanges() {
        for (Iterator iter = this.changes.keySet().iterator(); iter.hasNext(); ) {
            ConfigurationEntry entry = (ConfigurationEntry) iter.next();
            ExtendedPreferences prefs = entry.getNode();
            if(entry.getType() == ConfigurationType.BOOLEAN) {
                Boolean bool = (Boolean) this.changes.get(entry);
                prefs.putBoolean(entry.getKey(), bool.booleanValue());
            } else if(entry.getType() == ConfigurationType.COLOR) {
                Color color = (Color) this.changes.get(entry);
                prefs.putColor(entry.getKey(), color);
            } else if(entry.getType() == ConfigurationType.DOUBLE) {
                Double doub = (Double) this.changes.get(entry);
                prefs.putDouble(entry.getKey(), doub.doubleValue());
            } else if(entry.getType() == ConfigurationType.INTEGER) {
                Integer integer = (Integer) this.changes.get(entry);
                prefs.putInt(entry.getKey(), integer.intValue());
            } else if(entry.getType() == ConfigurationType.STRING) {
                String value = (String) this.changes.get(entry);
                prefs.put(entry.getKey(), value);
            } else if(entry.getType() == ConfigurationType.FONT_FAMILY) {
                String value = (String) this.changes.get(entry);
                prefs.put(entry.getKey(), value);
            } else if(entry.getType() instanceof ConfigurationType.Enumeration) {
                String value = (String) this.changes.get(entry);
                prefs.put(entry.getKey(), value);
            } else {
                throw new RuntimeException("Unknown ConfigurationType");
            }
        }
    }
}
