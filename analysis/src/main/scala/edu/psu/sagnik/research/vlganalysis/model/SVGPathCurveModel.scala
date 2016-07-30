package edu.psu.sagnik.research.vlganalysis.model

import edu.psu.sagnik.research.inkscapesvgprocessing.model.SVGPath
import edu.psu.sagnik.research.inkscapesvgprocessing.writer.model.PathStyle

import scala.xml.NodeSeq


/**
 * Created by sagnik on 3/4/16.
 */

//TODO: we are using string for all style elements, while they can be other data structures such as array.
// See https://www.w3.org/TR/SVG/painting.html#FillProperties and https://www.w3.org/TR/SVG/painting.html#StrokeProperties



case class SVGPathXML(svgPath:SVGPath,styleXML:NodeSeq)
//TODO: We need to add a sequence of "(x,y) points" that is painted by this curve.
case class SVGPathCurve(svgPath:SVGPath,pathStyle:PathStyle)

case class SVGCurve(id:String,paths:Seq[SVGPathCurve])