package net.sourceforge.tockit.toscanaj.data;

import net.sourceforge.tockit.toscanaj.diagram.LabelObserver;

import java.awt.geom.Point2D;

/**
 * Abstract class for model
 */

public interface LabelObservable{
  /**
   * Method to add observer
   */
  public void addObserver(LabelObserver diagramObserver);

  public void emitChangeSignal(Point2D point2D);
}