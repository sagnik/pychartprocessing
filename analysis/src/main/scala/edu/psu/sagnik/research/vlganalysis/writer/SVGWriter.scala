package edu.psu.sagnik.research.vlganalysis.writer

import edu.psu.sagnik.research.inkscapesvgprocessing.reader.XMLReader
import edu.psu.sagnik.research.vlganalysis.model.SVGPathCurve

import scala.reflect.io.File

/**
 * Created by sagnik on 3/6/16.
 */
object SVGWriter {
  def apply(curvePaths: Seq[SVGPathCurve], curveNo: String, orgSVGLoc: String, curveDir: String): Unit = {
    val curveSVGLoc = curveDir + "/" + orgSVGLoc.substring(0, orgSVGLoc.length - 4).split("/").last + "-Curve-" + curveNo + ".svg"

    //TODO: Possible exception
    val height = (XMLReader(orgSVGLoc) \\ "svg")(0) \@ "height"
    val width = (XMLReader(orgSVGLoc) \\ "svg")(0) \@ "width"

    val svgStart = "<?xml version=\"1.0\" standalone=\"no\"?>\n\n<svg height=\"" +
      height +
      "\" width=\"" +
      width +
      "\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">" +
      "\n"

    val svgString = curvePaths.map(_.svgPath.pContent).mkString("\n")

    val svgEnd = "\n</svg>"
    File(curveSVGLoc).writeAll(svgStart + svgString + svgEnd)
  }

  def apply(curvePaths: Seq[SVGPathCurve], orgSVGLoc: String, ext: String): Unit = {
    val curveSVGLoc = orgSVGLoc.dropRight(4) + "-" + ext + ".svg"
    println(s"writing atomic SVG to $curveSVGLoc")
    //TODO: Possible exception
    val height = (XMLReader(orgSVGLoc) \\ "svg")(0) \@ "height"
    val width = (XMLReader(orgSVGLoc) \\ "svg")(0) \@ "width"

    val svgStart = "<?xml version=\"1.0\" standalone=\"no\"?>\n\n<svg height=\"" +
      height +
      "\" width=\"" +
      width +
      "\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\">" +
      "\n"

    val svgString = curvePaths.map(_.svgPath.pContent).mkString("\n")

    val svgEnd = "\n</svg>"

    File(curveSVGLoc).writeAll(svgStart + svgString + svgEnd)
  }

}
