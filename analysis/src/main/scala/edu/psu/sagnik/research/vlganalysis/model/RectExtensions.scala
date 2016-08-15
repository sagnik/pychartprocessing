package edu.psu.sagnik.research.vlganalysis.model

import edu.psu.sagnik.research.inkscapesvgprocessing.model.Rectangle

/**
 * Created by sagnik on 7/29/16.
 */
object RectExtensions {
  def rectTouchesCorners(r1: Rectangle, r2: Rectangle): Boolean =
    if (r1.equals(r2)) true
    else {
      if (rectInsideStringent(r1, r2) || rectInsideStringent(r2, r1)) false
      else Rectangle.rectDistance(r1, r2) < 2f
    }

  def rectInsideStringent(in: Rectangle, out: Rectangle): Boolean =
    (in.x1 > out.x1 && in.y1 > out.y1 && in.x2 < out.x2 && in.y2 < out.y2) //all points are inside

}
