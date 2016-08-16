package edu.psu.sagnik.research.vlganalysis.impl

import edu.psu.sagnik.research.inkscapesvgprocessing.impl.SVGPathBB
import edu.psu.sagnik.research.inkscapesvgprocessing.pathparser.model.{ LinePath, _ }
import edu.psu.sagnik.research.inkscapesvgprocessing.transformparser.model.TransformCommand
import edu.psu.sagnik.research.inkscapesvgprocessing.writer.model.PathStyle
import edu.psu.sagnik.research.vlganalysis.model.SVGPathCurve

/**
 * Created by schoudhury on 8/16/16.
 */
object PathHelpers {

  lazy val pathDStringFromPath = (pops: Seq[PathCommand]) => pops.flatMap {
    pop =>
      pop match {
        case popM: Move => Some(
          "M " + popM.args.map(x => x.eP.toString + "," + x.eP.y.toString).mkString(" ")
        )
        case popL: Line => Some(
          "M " + popL.args.map(x => x.eP.toString + "," + x.eP.y.toString).mkString(" ")
        )
        case _ => None
      }
  }.mkString(" ")

  lazy val getPathStyle = (pStyle: PathStyle) =>
    List(
      pStyle.fill match { case Some(f) => "fill:" + f; case _ => "fill:none" },
      pStyle.fillRule match { case Some(f) => "fill-rule:" + f; case _ => "fill-rule:nonzero" },
      pStyle.fillOpacity match { case Some(f) => "fill-opacity:" + f; case _ => "fill-opacity:1" },
      pStyle.stroke match { case Some(f) => "stroke:" + f; case _ => "stroke:none" },
      pStyle.strokeWidth match { case Some(f) => "stroke-width:" + f; case _ => "stroke-width:1" },
      pStyle.strokeLinecap match { case Some(f) => "stroke-linecap:" + f; case _ => "stroke-linecap:butt" },
      pStyle.strokeLinejoin match { case Some(f) => "stroke-linejoin:" + f; case _ => "stroke-linejoin:miter" },
      pStyle.strokeMiterlimit match { case Some(f) => "stroke-miterlimit:" + f; case _ => "stroke-miterlimit:4" },
      pStyle.strokeDasharray match { case Some(f) => "stroke-dasharray:" + f; case _ => "stroke-dasharray:none" },
      pStyle.strokeDashoffset match { case Some(f) => "stroke-dashoffset:" + f; case _ => "stroke-dashoffset:0" },
      pStyle.strokeOpacity match { case Some(f) => "stroke-opacity:" + f; case _ => "stroke-opacity:1" }
    ).mkString(";")

  lazy val pathStringFromStyleAndPathDString = (pStyle: PathStyle, pathDString: String, pathID: String) => {
    val styleStart = " style=\""
    val styles = getPathStyle(pStyle)
    val styleEnd = "\""
    val styleString = styleStart + styles + styleEnd

    "<path d=\"" +
      pathDStringFromPath +
      "\"" +
      "id=\"" +
      pathID +
      "\"" +
      styleString +
      "xmlns=\"http://www.w3.org/2000/svg\" xmlns:svg=\"http://www.w3.org/2000/svg\"/>"

  }

  def createSVGPathString(
    p: PathStyle,
    tOps: Seq[TransformCommand], mC: Move, lC: Line, id: String
  ): String = {

    val styleString = getPathStyle(p)

    val transformString = "matrix(1.0,0.0,0.0,1.0,0.0,0.0)"
    //this is equivalent to no transformation, this will be corrected when
    //we map this with SVGPathBB

    val dString = pathDStringFromPath(Seq(mC, lC))

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

}
