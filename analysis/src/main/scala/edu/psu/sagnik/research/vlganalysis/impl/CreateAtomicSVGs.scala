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

  def createSVGCurvePath(path: SVGPathCurve, mC: Move, lC: Line): SVGPathCurve =
    SVGPathCurve(
      svgPath = SVGPathBB(
        path.svgPath.copy(
          pdContent = "M " +
          mC.args.head.eP.x +
          "," +
          mC.args.head.eP.y +
          " L " +
          lC.args.head.eP.x +
          "," +
          lC.args.head.eP.y,
          pContent = createSVGPathString(path.pathStyle, path.svgPath.transformOps, mC, lC, path.svgPath.id),
          pOps = Seq(mC, lC)
        )
      ),
      pathStyle = path.pathStyle
    )

  def createSVGPathString(
    p: PathStyle,
    tOps: Seq[TransformCommand], mC: Move, lC: Line, id: String
  ): String = {

    val styleString = List(
      p.fill match { case Some(f) => "fill:" + f; case _ => "fill:none" },
      p.fillRule match { case Some(f) => "fill-rule:" + f; case _ => "fill-rule:nonzero" },
      p.fillOpacity match { case Some(f) => "fill-opacity:" + f; case _ => "fill-opacity:1" },
      p.stroke match { case Some(f) => "stroke:" + f; case _ => "stroke:none" },
      p.strokeWidth match { case Some(f) => "stroke-width:" + f; case _ => "stroke-width:1" },
      p.strokeLinecap match { case Some(f) => "stroke-linecap:" + f; case _ => "stroke-linecap:butt" },
      p.strokeLinejoin match { case Some(f) => "stroke-linejoin:" + f; case _ => "stroke-linejoin:miter" },
      p.strokeMiterlimit match { case Some(f) => "stroke-miterlimit:" + f; case _ => "stroke-miterlimit:4" },
      p.strokeDasharray match { case Some(f) => "stroke-dasharray:" + f; case _ => "stroke-dasharray:none" },
      p.strokeDashoffset match { case Some(f) => "stroke-dashoffset:" + f; case _ => "stroke-dashoffset:0" },
      p.strokeOpacity match { case Some(f) => "stroke-opacity:" + f; case _ => "stroke-opacity:1" }
    ).mkString(";")

    val transformString = "matrix(1.0,0.0,0.0,1.0,0.0,0.0)"
    //this is equivalent to no transformation, this will be corrected when
    //we map this with SVGPathBB

    val dString = "M " +
      mC.args.head.eP.x +
      "," +
      mC.args.head.eP.y +
      " L " +
      lC.args.head.eP.x +
      "," +
      lC.args.head.eP.y

    "<path d=\"" +
      dString +
      "\" id=\"" +
      id +
      "\" style=\"" +
      styleString +
      "\" transform=\"" +
      transformString +
      "\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:svg=\"http://www.w3.org/2000/svg\"/>"

  }

  //tail-fucking-recursion. Take that, Python. ;)
  def splitPath(pathElems: Seq[PathCommand], path: SVGPathCurve, lep: CordPair, pathSArr: Seq[SVGPathCurve]): Seq[SVGPathCurve] =
    pathElems match {
      case Nil => pathSArr
      case pathElem :: Nil =>
        if (pathElem.isInstanceOf[Line]) {
          if (pathElem.args.isEmpty)
            pathSArr
          else if (pathElem.args.length == 1) {
            val lastEp = pathElem.getEndPoint[Line](lep, pathElem.isAbsolute, pathElem.args)
            val moveCommand = Move(isAbsolute = true, args = Seq(MovePath(lep)))
            val lineCommand = Line(isAbsolute = true, args = Seq(LinePath(lastEp)))
            val newSvgCurvePath = createSVGCurvePath(path, moveCommand, lineCommand)
            pathSArr :+ newSvgCurvePath
          } else {
            splitPath(
              pathElem.args.map(x => Line(isAbsolute = pathElem.isAbsolute, args = Seq(LinePath(x.asInstanceOf[LinePath].eP)))),
              path,
              lep,
              pathSArr
            )
          }
        } else {
          val lastEp = pathElem.getEndPoint[pathElem.type](lep, pathElem.isAbsolute, pathElem.args)
          pathSArr
        }
      case pathElem :: rest =>
        val lastEndPoint = pathElem.getEndPoint[pathElem.type](lep, pathElem.isAbsolute, pathElem.args)
        if (pathElem.isInstanceOf[Line]) {
          if (pathElem.args.isEmpty)
            pathSArr
          else if (pathElem.args.length == 1) {
            val lastEp = pathElem.getEndPoint[Line](lep, pathElem.isAbsolute, pathElem.args)
            val moveCommand = Move(isAbsolute = true, args = Seq(MovePath(lep)))
            val lineCommand = Line(isAbsolute = true, args = Seq(LinePath(lastEp)))
            val newSvgCurvePath = createSVGCurvePath(path, moveCommand, lineCommand)
            splitPath(
              rest,
              path,
              lastEndPoint,
              pathSArr :+ newSvgCurvePath
            )
          } else {
            val splitPaths = pathElem.args.map(
              x =>
                Line(isAbsolute = pathElem.isAbsolute, args = Seq(LinePath(x.asInstanceOf[LinePath].eP)))
            )
            splitPath(
              splitPaths ++ rest,
              path,
              lep,
              pathSArr
            )
          }
        } else
          splitPath(rest, path, lastEndPoint, pathSArr)

    }

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

    val spPath = noFill.flatMap(c =>
      SplitPaths.splitPath(
        c.svgPath.pOps.slice(1, c.svgPath.pOps.length),
        c,
        CordPair(c.svgPath.pOps(0).args(0).asInstanceOf[MovePath].eP.x, c.svgPath.pOps(0).args(0).asInstanceOf[MovePath].eP.y),
        Seq.empty[SVGPathCurve]
      ))
    SVGWriter(spPath, loc, "sps")
  }

  def apply(loc: String, colors: Seq[String], fromPython: Boolean = true) = {
    val cS = PyChartSVGPathExtract(loc)
    val graphPaths = cS.filter(p =>
      colors.exists(color => p.pathStyle.stroke.getOrElse("#fffffff").equalsIgnoreCase(color)))
    println(graphPaths.length)
    val (fillExists, noFill) = graphPaths.partition(x => {
      (x.pathStyle.fill match {
        case Some(fill) => true
        case _ => false
      }) &&
        ("none".equals(x.pathStyle.stroke.getOrElse("none")) ||
          "#ffffff".equals(x.pathStyle.stroke.getOrElse("#ffffff")))
    })

    val spPath = noFill.flatMap(c =>
      SplitPaths.splitPath(
        c.svgPath.pOps.slice(1, c.svgPath.pOps.length),
        c,
        CordPair(c.svgPath.pOps(0).args(0).asInstanceOf[MovePath].eP.x, c.svgPath.pOps(0).args(0).asInstanceOf[MovePath].eP.y),
        Seq.empty[SVGPathCurve]
      ))
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
    val pyLoc = "../linegraphproducer/data/1/1-square-cross-gold.svg"
    //val pyLoc = "../linegraphproducer/data/1/1.svg"
    val colorsMap = Map("indigo" -> "#4B0082", "gold" -> "#FFD700")
    SplitPaths(pyLoc, colorsMap.values.toSeq, fromPython = true)
  }

}