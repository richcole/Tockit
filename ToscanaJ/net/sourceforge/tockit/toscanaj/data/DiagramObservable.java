package net.sourceforge.tockit.toscanaj.data;

import net.sourceforge.tockit.toscanaj.diagram.DiagramObserver;

/**
 * Abstract class for model
 */

public interface DiagramObservable{
  /**
   * Method to add observer
   */
  public void addObserver(DiagramObserver diagramObserver);

  public void emitChangeSignal();
}