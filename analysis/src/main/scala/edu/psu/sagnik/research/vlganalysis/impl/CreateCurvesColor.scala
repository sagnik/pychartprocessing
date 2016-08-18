package edu.psu.sagnik.research.vlganalysis.impl

import java.io.File

import edu.psu.sagnik.research.inkscapesvgprocessing.pathparser.model.{ CordPair, MovePath }
import edu.psu.sagnik.research.inkscapesvgprocessing.reader.XMLReader
import edu.psu.sagnik.research.vlganalysis.model.{ SVGCurve, SVGPathCurve }
import edu.psu.sagnik.research.vlganalysis.writer.{ PNGWriter, SVGWriter }
import org.apache.commons.io.FileUtils

import scala.language.postfixOps
/**
 * Created by sagnik on 3/5/16.
 */
object CreateCurvesColor {

  val reverseColorMaps = Map("#4B0082" -> "indigo", "#FFD700" -> "gold")

  def colorBasedSegmentation(curvePaths: Seq[SVGPathCurve]): Seq[SVGCurve] =
    curvePaths.groupBy(x => x.pathStyle.stroke.getOrElse("none")).toSeq.zipWithIndex.map { case (d, index) => SVGCurve(index.toString, d._2) }

  def featureBasedSegmentation(curvePaths: Seq[SVGPathCurve]): Seq[SVGCurve] =
    curvePaths.groupBy(x => x.pathStyle).toSeq.zipWithIndex.map { case (d, index) => SVGCurve(index.toString, d._2) }

  def apply(loc: String, createImages: Boolean, segementationFunction: (Seq[SVGPathCurve]) => Seq[SVGCurve]) = {
    import PathHelpers._
    val svgPaths =
      if (loc.contains("-sps")) //this SVG has already paths split
        SVGPathExtract(loc, sps = true)
      else
        SVGPathExtract(loc, sps = false).flatMap(
          c =>
            splitPath(
              c.svgPath.pOps.slice(1, c.svgPath.pOps.length),
              c,
              CordPair(c.svgPath.pOps.head.args.head.asInstanceOf[MovePath].eP.x, c.svgPath.pOps.head.args.head.asInstanceOf[MovePath].eP.y),
              Seq.empty[SVGPathCurve]
            )
        )
    val (fillExists, noFill) = svgPaths.partition(x => {
      (x.pathStyle.fill match {
        case Some(fill) => true
        case _ => false
      }) && ("none".equals(x.pathStyle.stroke.getOrElse("none")) || "#ffffff".equals(x.pathStyle.stroke.getOrElse("#ffffff")))
    })

    //TODO: possible exceptions
    val height = if (((XMLReader(loc) \\ "svg").head \@ "height").contains("pt"))
      ((XMLReader(loc) \\ "svg").head \@ "height").dropRight(2).toFloat
    else
      ((XMLReader(loc) \\ "svg").head \@ "height").toFloat

    val width = if (((XMLReader(loc) \\ "svg").head \@ "width").contains("pt"))
      ((XMLReader(loc) \\ "svg").head \@ "width").dropRight(2).toFloat
    else
      ((XMLReader(loc) \\ "svg").head \@ "width").toFloat

    val (axes, tics, curvePaths) = SeparateAxesGridTickPaths(noFill, width, height)

    val curveGroups = segementationFunction(curvePaths)

    if (createImages) {
      val curveDir = new File(loc.substring(0, loc.length - 4))
      val dirResult = if (!curveDir.exists) curveDir.mkdir
      else {
        FileUtils.deleteDirectory(curveDir); curveDir.mkdir
      }

      if (dirResult) {
        curveGroups foreach { x =>
          println(s"Creating SVG for curve ${x.id}")
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
      val dirResult = if (!curveDir.exists) curveDir.mkdir
      else {
        FileUtils.deleteDirectory(curveDir); curveDir.mkdir
      }
      if (dirResult) {
        curveGroups foreach {
          x =>
            println(s"Creating SVG for curve ${x.id}")
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
          "../linegraphproducer/data/1/1-sps.svg"
        )
    CreateCurvesColor(loc, createImages = true, colorBasedSegmentation)

  }

}
