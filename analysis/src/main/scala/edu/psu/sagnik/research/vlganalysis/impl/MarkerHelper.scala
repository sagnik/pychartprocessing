package edu.psu.sagnik.research.vlganalysis.impl

import edu.psu.sagnik.research.inkscapesvgprocessing.model.Rectangle
import edu.psu.sagnik.research.vlganalysis.model.SVGPathCurve

/**
 * Created by szr163 on 3/11/16.
 */
object MarkerHelper {
  def isH(x: SVGPathCurve): Boolean = { val bb = x.svgPath.bb.getOrElse(Rectangle(0f, 0f, 0f, 0f)); (bb.y1 == bb.y2) && (bb.x1 != 0 && bb.y1 != 0 && bb.x2 != 0 && bb.y2 != 0) }

  def isV(x: SVGPathCurve): Boolean = { val bb = x.svgPath.bb.getOrElse(Rectangle(0f, 0f, 0f, 0f)); (bb.x1 == bb.x2) && (bb.x1 != 0 && bb.y1 != 0 && bb.x2 != 0 && bb.y2 != 0) }

  def isHV(x: SVGPathCurve): Boolean = isH(x) || isV(x)

  def hvTouches(p1: SVGPathCurve, p2: SVGPathCurve): Boolean =
    if (p1.equals(p2)) false
    else if (!isHV(p1) || !isHV(p2)) false
    else if (isH(p1) && isH(p2)) false
    else if (isV(p1) && isV(p2)) false
    //at this point we know the path has a bounding box, so getOrElse is not needed.
    else {
      val bbH = if (isH(p1)) p1.svgPath.bb.get else p2.svgPath.bb.get
      val bbV = if (isV(p1)) p1.svgPath.bb.get else p2.svgPath.bb.get
      (bbH.x1 == bbV.x1) || (bbH.x2 == bbV.x1)
    }

  def hvIntersects(p1: SVGPathCurve, p2: SVGPathCurve): Boolean =
    if (p1.equals(p2)) false
    else if (!isHV(p1) || !isHV(p2)) false
    else if (isH(p1) && isH(p2)) false
    else if (isV(p1) && isV(p2)) false
    //at this point we know the path has a bounding box, so getOrElse is not needed.
    else {
      val bbH = if (isH(p1)) p1.svgPath.bb.get else p2.svgPath.bb.get
      val bbV = if (isV(p1)) p1.svgPath.bb.get else p2.svgPath.bb.get
      !((bbH.x1 == bbV.x1) || (bbH.x2 == bbV.x1)) && Rectangle.rectInterSects(bbH, bbV)
    }

  def pathIntersects(p1: SVGPathCurve, p2: SVGPathCurve): Boolean =
    if (p1.equals(p2)) false
    else {
      val bb1 = p1.svgPath.bb.getOrElse(Rectangle(0f, 0f, 0f, 0f))
      val bb2 = p2.svgPath.bb.getOrElse(Rectangle(0f, 0f, 0f, 0f))
      if (Rectangle(0f, 0f, 0f, 0f).equals(bb1) || Rectangle(0f, 0f, 0f, 0f).equals(bb2))
        false
      else
        Rectangle.rectInterSects(bb1, bb2)
    }

  def pathSeqIntersects(p1s: Seq[SVGPathCurve], p2s: Seq[SVGPathCurve]): Int =
    p1s.foldLeft(0)((r, c) => r + p2s.count(pathIntersects(_, c)))

  def nonHVTouches(p1: SVGPathCurve, p2: SVGPathCurve): Boolean =
    if (p1.equals(p2)) false
    else if (isHV(p1) || isHV(p2)) false
    else {
      val bb1 = p1.svgPath.bb.getOrElse(Rectangle(0f, 0f, 0f, 0f))
      val bb2 = p2.svgPath.bb.getOrElse(Rectangle(0f, 0f, 0f, 0f))
      if (Rectangle(0f, 0f, 0f, 0f).equals(bb1) || Rectangle(0f, 0f, 0f, 0f).equals(bb2))
        false
      else
        Rectangle.rectInterSects(bb1, bb2) //TODO: check correctness
    }

  def isLeftOrRightCaret(b1: Rectangle, b2: Rectangle): Boolean =
    !(Rectangle(0f, 0f, 0f, 0f).equals(b1) || Rectangle(0f, 0f, 0f, 0f).equals(b2)) &&
      ((b1.y2 == b2.y1) || (b1.y1 == b2.y2)) &&
      (b1.x1 == b2.x1) &&
      (b1.x2 == b2.x2)

  def isUpOrDownCaret(b1: Rectangle, b2: Rectangle): Boolean =
    !(Rectangle(0f, 0f, 0f, 0f).equals(b1) || Rectangle(0f, 0f, 0f, 0f).equals(b2)) &&
      ((b1.x1 == b2.x2) || (b1.x2 == b2.x1)) &&
      (b1.y1 == b2.y1) &&
      (b1.y2 == b2.y2)

  def isLeftCaret(b1: Rectangle, b2: Rectangle, bV: Rectangle): Boolean =
    !(Rectangle(0f, 0f, 0f, 0f).equals(b1) || Rectangle(0f, 0f, 0f, 0f).equals(b2)) &&
      ((b1.y2 == b2.y1) || (b1.y1 == b2.y2)) &&
      (b1.x1 == b2.x1) &&
      (b1.x2 == b2.x2) &&
      (b1.x2 == bV.x1)

  def isRightCaret(b1: Rectangle, b2: Rectangle, bV: Rectangle): Boolean =
    !(Rectangle(0f, 0f, 0f, 0f).equals(b1) || Rectangle(0f, 0f, 0f, 0f).equals(b2)) &&
      ((b1.y2 == b2.y1) || (b1.y1 == b2.y2)) &&
      (b1.x1 == b2.x1) &&
      (b1.x2 == b2.x2) &&
      (b1.x1 == bV.x1)

  def isUpCaret(b1: Rectangle, b2: Rectangle, bH: Rectangle): Boolean =
    !(Rectangle(0f, 0f, 0f, 0f).equals(b1) || Rectangle(0f, 0f, 0f, 0f).equals(b2)) &&
      ((b1.x1 == b2.x2) || (b1.x2 == b2.x1)) &&
      (b1.y1 == b2.y1) &&
      (b1.y2 == b2.y2) &&
      (b1.y2 == bH.y1)

  def isDownCaret(b1: Rectangle, b2: Rectangle, bH: Rectangle): Boolean =
    !(Rectangle(0f, 0f, 0f, 0f).equals(b1) || Rectangle(0f, 0f, 0f, 0f).equals(b2)) &&
      ((b1.x1 == b2.x2) || (b1.x2 == b2.x1)) &&
      (b1.y1 == b2.y1) &&
      (b1.y2 == b2.y2) &&
      (b1.y1 == bH.y1)

  def isCaret(cs: List[SVGPathCurve], hvLine: SVGPathCurve, dir: String): Boolean =
    if (cs.length != 2) false
    else {
      val bbs = cs.map(x => x.svgPath.bb.getOrElse(Rectangle(0f, 0f, 0f, 0f)))
      if ("left".equals(dir)) isLeftCaret(bbs.head, bbs.tail.head, hvLine.svgPath.bb.getOrElse(Rectangle(0f, 0f, 0f, 0f)))
      else if ("right".equals(dir)) isRightCaret(bbs.head, bbs.tail.head, hvLine.svgPath.bb.getOrElse(Rectangle(0f, 0f, 0f, 0f)))
      else if ("up".equals(dir)) isUpCaret(bbs.head, bbs.tail.head, hvLine.svgPath.bb.getOrElse(Rectangle(0f, 0f, 0f, 0f)))
      else if ("down".equals(dir)) isDownCaret(bbs.head, bbs.tail.head, hvLine.svgPath.bb.getOrElse(Rectangle(0f, 0f, 0f, 0f)))
      else false
    }

  def isCaret(cs: List[SVGPathCurve], hvLine: SVGPathCurve): Boolean =
    if (cs.length != 2) false
    else isCaret(cs, hvLine, "left") || isCaret(cs, hvLine, "right") || isCaret(cs, hvLine, "up") || isCaret(cs, hvLine, "down")

  //def isCaret(cs: Seq[SVGPathCurve]): Boolean = isCaret(cs.toList)

  def isCaret(p1: SVGPathCurve, p2: SVGPathCurve, hvLine: SVGPathCurve, dir: String): Boolean = isCaret(List(p1, p2), hvLine, dir)

  def createsCaret(xs: Seq[SVGPathCurve], hvLine: SVGPathCurve, dir: String): Boolean =
    (xs.map(a => a.pathStyle).distinct.length == 1) &&
      !xs.exists(a => isHV(a)) &&
      isCaret(xs.toList, hvLine, dir)

  /************************* actual shapes *****************************************************/

  def createsCross(xs: Seq[SVGPathCurve]): Boolean =
    xs.length == 2 && //the paths are drawn with same style
      (xs.map(a => a.pathStyle).distinct.length == 1) &&
      !xs.exists(isHV(_)) && //there's no HV line
      xs(0).svgPath.bb.equals(xs(1).svgPath.bb)

  def createsPlus(xs: Seq[SVGPathCurve]): Boolean =
    xs.length == 2 &&
      (xs.map(a => a.pathStyle).distinct.length == 1) &&
      xs.forall(isHV(_)) //both lines are HV line

  def createsTriangle(xs: Seq[SVGPathCurve]): Boolean =
    createsLeftTriangle(xs) || createsRightTriangle(xs) || createsUpTriangle(xs) || createsDownTriangle(xs)

  def createsLeftTriangle(xs: Seq[SVGPathCurve]): Boolean =
    xs.length == 3 &&
      (xs.map(a => a.pathStyle).distinct.length == 1) &&
      (xs.count(isHV(_)) == 1) && //there's exactly one HV line
      isCaret(cs = xs.filter(!isHV(_)).toList, hvLine = xs.filter(isHV(_)).head, "left")

  def createsRightTriangle(xs: Seq[SVGPathCurve]): Boolean =
    xs.length == 3 &&
      (xs.map(a => a.pathStyle).distinct.length == 1) &&
      (xs.count(isHV(_)) == 1) && //there's exactly one HV line
      isCaret(cs = xs.filter(!isHV(_)).toList, hvLine = xs.filter(isHV(_)).head, "right")

  def createsUpTriangle(xs: Seq[SVGPathCurve]): Boolean =
    xs.length == 3 &&
      (xs.map(a => a.pathStyle).distinct.length == 1) &&
      (xs.count(isHV(_)) == 1) && //there's exactly one HV line
      isCaret(cs = xs.filter(!isHV(_)).toList, hvLine = xs.filter(isHV(_)).head, "up")

  def createsDownTriangle(xs: Seq[SVGPathCurve]): Boolean =
    xs.length == 3 &&
      (xs.map(a => a.pathStyle).distinct.length == 1) &&
      (xs.count(isHV(_)) == 1) && //there's exactly one HV line
      isCaret(cs = xs.filter(!isHV(_)).toList, hvLine = xs.filter(isHV(_)).head, "down")

  def createsSquare(xs: Seq[SVGPathCurve]): Boolean =
    xs.length == 4 &&
      (xs.map(a => a.pathStyle).distinct.length == 1) &&
      xs.forall(isHV(_)) && //there's no non HV line
      xs.forall(a => xs.count(y => hvTouches(a, y)) == 2)

  def createsStar(xs: Seq[SVGPathCurve]): Boolean =
    xs.length == 4 &&
      (xs.map(a => a.pathStyle).distinct.length == 1) &&
      xs.count(a => isHV(a)) == 2 && //two of the paths are non hV and two of them are HV
      xs.forall(x => xs.count(y => pathIntersects(x, y)) == 3) && //each path intersect with every other path
      xs.filter(x => !isHV(x))(0).svgPath.bb.equals(xs.filter(x => !isHV(x))(1).svgPath.bb) //non hv paths have the same bb.

  //TODO: better algorithm for diamond
  def createsDiamond(xs: Seq[SVGPathCurve]): Boolean =
    xs.length == 4 &&
      (xs.map(a => a.pathStyle).distinct.length == 1) &&
      !xs.exists(isHV(_)) &&
      xs.combinations(2).count { c => isLeftOrRightCaret(c.head.svgPath.bb.get, c.tail.head.svgPath.bb.get) } == 2 &&
      xs.combinations(2).count { c => isUpOrDownCaret(c.head.svgPath.bb.get, c.tail.head.svgPath.bb.get) } == 2

}
