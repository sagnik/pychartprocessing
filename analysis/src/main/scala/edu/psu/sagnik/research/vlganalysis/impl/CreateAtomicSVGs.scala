package edu.psu.sagnik.research.vlganalysis.impl

import edu.psu.sagnik.research.inkscapesvgprocessing.impl.SVGPathBB
import edu.psu.sagnik.research.inkscapesvgprocessing.pathparser.model._
import edu.psu.sagnik.research.inkscapesvgprocessing.transformparser.model.TransformCommand
import edu.psu.sagnik.research.inkscapesvgprocessing.writer.model.PathStyle
import edu.psu.sagnik.research.vlganalysis.model.SVGPathCurve
import edu.psu.sagnik.research.vlganalysis.writer.SVGWriter

/**
 * Created by sagnik on 7/13/16.
 */
object SplitPaths {

  def apply(loc: String) = {
    val cS = SVGPathExtract(loc, false)
    //Seq(svgpathCurves(0)).foreach(x=>println(x.svgPath.id,x.svgPath.pdContent,x.svgPath.pOps))

    val (fillExists, noFill) = cS.partition(x => {
      (x.pathStyle.fill match {
        case Some(fill) => true
        case _ => false
      }) &&
        ("none".equals(x.pathStyle.stroke.getOrElse("none")) ||
          "#ffffff".equals(x.pathStyle.stroke.getOrElse("#ffffff")))
    })

    import PathHelpers._
    val spPath = noFill
      .filterNot(_.svgPath.pOps.isEmpty)
      .flatMap { c =>
        splitPath(
          c.svgPath.pOps.tail, //first command is always move
          c,
          CordPair(c.svgPath.pOps.head.args.head.asInstanceOf[MovePath].eP.x, c.svgPath.pOps.head.args.head.asInstanceOf[MovePath].eP.y),
          Seq.empty[SVGPathCurve]
        )
      }
    SVGWriter(spPath, loc, "sps")
  }

  def apply(loc: String, colors: Seq[String], fromPython: Boolean = true) = {
    val cS = PyChartSVGPathExtract(loc)
    val pathIdsFromDefs =
      cS
        .filter {
          _.svgPath.id.contains("-use-")
        }
        .map(_.svgPath.id.split("-us").head)

    val pathsfromNonDefs = cS
      .filterNot(p => pathIdsFromDefs.exists(x => x.equals(p.svgPath.id)))
      .filter(p => colors.exists(c => p.pathStyle.stroke.getOrElse("#ffffff").equalsIgnoreCase(c)))

    val graphPaths = pathsfromNonDefs
    val (fillExists, noFill) = graphPaths.partition(x => {
      (x.pathStyle.fill match {
        case Some(fill) => true
        case _ => false
      }) &&
        ("none".equals(x.pathStyle.stroke.getOrElse("none")) ||
          "#ffffff".equals(x.pathStyle.stroke.getOrElse("#ffffff")))
    })

    import PathHelpers._
    val spPath = noFill
      .filterNot(_.svgPath.pOps.isEmpty)
      .flatMap { c =>
        splitPathPyChartSVGs(
          c.svgPath.pOps.tail, //first command is always move
          c,
          CordPair(c.svgPath.pOps.head.args.head.asInstanceOf[MovePath].eP.x, c.svgPath.pOps.head.args.head.asInstanceOf[MovePath].eP.y)
        )
      }

    SVGWriter(spPath, loc, "sps")

  }

}

object TestSplitPaths {
  def main(args: Array[String]): Unit = {
    //val loc="src/test/resources/hassan-Figure-2.svg"
    //val loc="data/10.1.1.164.2702-Figure-2.svg"
    //val loc="data/10.1.1.100.3286-Figure-9.svg"1
    //val loc="data/10.1.1.104.3077-Figure-1.svg"
    //val loc="src/test/resources/10.1.1.108.5575-Figure-16.svg"
    //val loc = "src/test/resources/10.1.1.113.223-Figure-10.svg"
    val pyLoc = "../linegraphproducer/data/2/1.svg"
    //val pyLoc = "../linegraphproducer/data/1/1.svg"
    val colorsMap = Map("indigo" -> "#4B0082", "gold" -> "#FFD700")
    SplitPaths(pyLoc, colorsMap.values.toSeq, fromPython = true)
  }

}