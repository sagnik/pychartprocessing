package edu.psu.sagnik.research.vlganalysis.impl

import java.io.File

import edu.psu.sagnik.research.inkscapesvgprocessing.pathparser.model.{ CordPair, MovePath }
import edu.psu.sagnik.research.inkscapesvgprocessing.reader.XMLReader
import edu.psu.sagnik.research.vlganalysis.impl
import edu.psu.sagnik.research.vlganalysis.model.{ SVGCurve, SVGPathCurve }
import edu.psu.sagnik.research.vlganalysis.writer.{ PNGWriter, SVGWriter }
import org.apache.commons.io.FileUtils

import scala.language.postfixOps
/**
 * Created by sagnik on 3/5/16.
 */
object CreateCurvesColor {

  def colorBasedSegmentation(curvePaths: Seq[SVGPathCurve]): Seq[SVGCurve] =
    curvePaths.groupBy(x =>
      x.pathStyle.stroke.getOrElse("none"))
      .toSeq
      .zipWithIndex
      .map {
        case (d, index) => SVGCurve(index.toString, d._2)
      }

  def featureBasedSegmentation(curvePaths: Seq[SVGPathCurve]): Seq[SVGCurve] =
    curvePaths.groupBy(x => x.pathStyle).toSeq.zipWithIndex.map { case (d, index) => SVGCurve(index.toString, d._2) }

  def apply(loc: String, createImages: Boolean, segementationFunction: (Seq[SVGPathCurve]) => Seq[SVGCurve]) = {
    import PathHelpers._
    val svgPaths = {
      if (loc.contains("-sps")) {
        //this SVG has already paths split
        println(s"the paths already split, don't need to split them")
        SVGPathExtract(loc, sps = true)

      } else {
        SplitPaths(loc, fromPython = true)
        println(s"paths splitted ${loc.dropRight(4) + "-sps.svg"}")
        SVGPathExtract(loc.dropRight(4) + "-sps.svg", sps = true)
      }
    }
      .filterNot(path =>
        path.pathStyle.fill.isEmpty && path.pathStyle.stroke.isEmpty)

    //svgPaths.foreach { x => println(x.pathStyle.fill + " : " + x.pathStyle.stroke) }

    //TODO: possible exceptions
    val height = if (((XMLReader(loc) \\ "svg").head \@ "height").contains("pt"))
      ((XMLReader(loc) \\ "svg").head \@ "height").dropRight(2).toFloat
    else
      ((XMLReader(loc) \\ "svg").head \@ "height").toFloat

    val width = if (((XMLReader(loc) \\ "svg").head \@ "width").contains("pt"))
      ((XMLReader(loc) \\ "svg").head \@ "width").dropRight(2).toFloat
    else
      ((XMLReader(loc) \\ "svg").head \@ "width").toFloat

    val (axes, tics, curvePaths) = SeparateAxesGridTickPaths(svgPaths, width, height)

    val curveGroups = segementationFunction(curvePaths)

    if (createImages) {
      val curveDir = new File(loc.substring(0, loc.length - 4))
      val dirResult = if (!curveDir.exists) curveDir.mkdir
      else {
        FileUtils.deleteDirectory(curveDir)
        curveDir.mkdir
      }

      if (dirResult) {
        curveGroups foreach { x =>
          SVGWriter(x.paths, x.id, loc, curveDir.getAbsolutePath)
          PNGWriter(x.id, loc, curveDir.getAbsolutePath)
        }
      } else {
        println("Couldn't create directory to store Curve SVG files, exiting.")
      }
      curveGroups
    } else
      curveGroups
  }

  def apply(
    loc: String,
    curvePaths: Seq[SVGPathCurve],
    createImages: Boolean,
    segementationFunction: (Seq[SVGPathCurve]) => Seq[SVGCurve]
  ) = {
    val curveGroups = segementationFunction(curvePaths)
    if (createImages) {
      val curveDir = new File(loc.substring(0, loc.length - 4))
      val dirResult = if (!curveDir.exists) curveDir.mkdir else true
      /*else {
        FileUtils.deleteDirectory(curveDir); curveDir.mkdir
      }*/
      if (dirResult) {
        curveGroups foreach {
          x =>
            SVGWriter(x.paths, x.id, loc, curveDir.getAbsolutePath)
        }
      } else {
        println("Couldn't create directory to store Curve SVG files, exiting.")
      }
      curveGroups
    } else
      curveGroups
  }

  def main(args: Array[String]): Unit = {
    val loc =
      args.headOption.
        getOrElse(
          "src/test/resources/19-sps.svg"
        )
    CreateCurvesColor(loc, createImages = true, colorBasedSegmentation)

  }

}
