package edu.psu.sagnik.research.vlganalysis.impl

import edu.psu.sagnik.research.inkscapesvgprocessing.pathparser.model.{ CordPair, MovePath }
import edu.psu.sagnik.research.vlganalysis.writer.SVGWriter

/**
 * Created by schoudhury on 8/17/16.
 */

// will split a
object SplitPlot extends App {
  val loc = "../linegraphproducer/data/1/1.svg"
  val cS = PyChartSVGPathExtract(loc)
  val pathIdsFromDefs = cS
    .filter {
      _.svgPath.id.contains("-use-")
    }
    .map(_.svgPath.id.split("-us").head)

  val pathsFromNonDefs = cS
    .filterNot(p => pathIdsFromDefs.exists(x => x.equals(p.svgPath.id)))

  val texts = pathsFromNonDefs
    .filter(p => p.svgPath.groups.exists(_.id.contains("text")))

  val legendBoundary = pathsFromNonDefs
    .filter(p => p.svgPath.groups.exists(_.id.contains("legend")))

  val patch = pathsFromNonDefs
    .filter(p => p.svgPath.groups.exists(_.id.contains("patch")))

  //pathsFromNonDefs.foreach(p => println(s"[path id]: ${p.svgPath.id} [group id]: ${p.svgPath.groups.map(_.id)}"))
  SVGWriter(legendBoundary, loc, "patch")

}
