package net.sourceforge.tockit.toscanaj.canvas;

import java.awt.Graphics;

/**
 * abstract class to draw 2D graph items. CanvasItem will have a concept of depth
 * for re-drawing, and hold the point that an item is positioned.
 */

public abstract class CanvasItem {

  /**
   * draw method called to draw canvas item
   */
  public abstract void draw(Graphics g);
}