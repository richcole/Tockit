/*
 * Copyright Peter Becker, 2004 (http://www.peterbecker.de)
 */
package org.tockit.swing.undo;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * Extends Swing's UndoManager class to handle the Action part.
 * 
 * In addition to the standard UndoManager functionality, this class 
 * provides Swing Actions for undo and redo and manages their state.
 */
public class ExtendedUndoManager extends UndoManager {
	class UndoAction extends AbstractAction {
        public UndoAction() {
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z ,ActionEvent.CTRL_MASK));
        }
        
		public void actionPerformed(ActionEvent e) {
			ExtendedUndoManager.this.undo();
		}
	}

	class RedoAction extends AbstractAction {
        public RedoAction() {
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y ,ActionEvent.CTRL_MASK));
        }
        
		public void actionPerformed(ActionEvent e) {
			ExtendedUndoManager.this.redo();
		}
	}
	
	private Action undoAction = new UndoAction(); 
	private Action redoAction = new RedoAction(); 
	
	public ExtendedUndoManager() {
		super();
		updateActions();
	}
	
	public Action getUndoAction() {
		return undoAction;
	}

	public Action getRedoAction() {
		return redoAction;
	}
	
	private void updateActions() {
		undoAction.setEnabled(canUndo());
		undoAction.putValue(Action.NAME, getUndoPresentationName());
		redoAction.setEnabled(canRedo());
		redoAction.putValue(Action.NAME, getRedoPresentationName());
	}
	
	@Override
	public synchronized boolean addEdit(UndoableEdit anEdit) {
		boolean result = super.addEdit(anEdit);
		updateActions();
		return result;
	}
	
	@Override
	public synchronized void discardAllEdits() {
		super.discardAllEdits();
		updateActions();
	}
	
	@Override
	public synchronized void redo() throws CannotRedoException {
		super.redo();
		updateActions();
	}

	@Override
	public synchronized void undo() throws CannotUndoException {
		super.undo();
		updateActions();
	}
}
