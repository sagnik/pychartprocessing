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

  lazy val pathDStringFromPath = (pops: Seq[PathCommand]) =>
    pops.flatMap {
      pop => //TODO: assuming absolute coordinates here.
        pop match {
          case popM: Move => Some("M " + popM.args.map(arg => s"${arg.eP.x},${arg.eP.y}").mkString(" "))
          case popL: Line => Some("L " + popL.args.map(arg => s"${arg.eP.x},${arg.eP.y}").mkString(" "))
          case popQ: QBC => Some("Q " + popQ.args.map(arg => s"${arg.cP1.x},${arg.cP1.y} ${arg.eP.x},${arg.eP.y}").mkString(" "))
          case popZ: Close => Some("z")
          case _ => ???
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
    val styleString = getPathStyle(pStyle)

    "<path d=\"" +
      pathDString +
      "\" id=\"" +
      pathID +
      "\" style=\"" +
      styleString +
      "\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:svg=\"http://www.w3.org/2000/svg\"/>"

  }

  lazy val pathDStringFromSingleArgCommand = (command: PathCommand) => {
    require(command.isAbsolute, "A <relative> path command in pyChart SVG, aborting.")
    require(command.args.length == 1, "A path command in pyChart SVG has multiple arguments, aborting.")
    command match {
      case cM: Move =>
        s"M ${cM.args.head.eP.x},${cM.args.head.eP.y} "
      case cL: Line =>
        s"L ${cL.args.head.eP.x},${cL.args.head.eP.y} "
      case cQ: QBC =>
        s"Q ${cQ.args.head.cP1.x},${cQ.args.head.cP1.y} ${cQ.args.head.eP.x},${cQ.args.head.eP.y}"
      case cZ: Close =>
        s"z"
      case x =>
        println(s"path command to dString not implemented for this type: ${x.getClass}")
        ???
    }
  }

  def createSVGPathString(
    p: PathStyle,
    tOps: Seq[TransformCommand], mC: Move, otherCommand: PathCommand, id: String
  ): String = {

    val styleString = getPathStyle(p)

    val transformString = "matrix(1.0,0.0,0.0,1.0,0.0,0.0)"
    //this is equivalent to no transformation, this will be corrected when
    //we map this with SVGPathBB

    val dString = s"${pathDStringFromSingleArgCommand(mC)}${pathDStringFromSingleArgCommand(otherCommand)}"

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

  //we are making sure that the `other command` is not unknown. So far, we know line and
  //quadratic Bezier curves appear.
  def createSVGCurvePath(path: SVGPathCurve, mC: Move, otherCommand: PathCommand): SVGPathCurve = otherCommand match {
    case otherCommand: Line =>
      SVGPathCurve(
        svgPath = SVGPathBB(
          path.svgPath.copy(
            pdContent = s"${
            pathDStringFromSingleArgCommand(mC)
          } ${
            pathDStringFromSingleArgCommand(otherCommand)
          }",
            pContent = createSVGPathString(path.pathStyle, path.svgPath.transformOps, mC, otherCommand, path.svgPath.id),
            pOps = Seq(mC, otherCommand)
          )
        ),
        pathStyle = path.pathStyle
      )
    case otherCommand: QBC =>
      SVGPathCurve(
        svgPath = SVGPathBB(
          path.svgPath.copy(
            pdContent = s"${
            pathDStringFromSingleArgCommand(mC)
          } ${
            pathDStringFromSingleArgCommand(otherCommand)
          }",
            pContent = createSVGPathString(path.pathStyle, path.svgPath.transformOps, mC, otherCommand, path.svgPath.id),
            pOps = Seq(mC, otherCommand)
          )
        ),
        pathStyle = path.pathStyle
      )
    case _ =>
      println(s" SVGPathCurve creation for paths of type ${otherCommand.getClass} not implemented")
      ???
  }

  /*
  * SVGs coming from PyChart have two properties: 1. Each command has exactly one argument
  * (as opposed to multiple arguments) and 2. Commands are always absolute. Therefore we should
  * process them faster.
  */

  def splitPathPyChartSVGs(pathElems: Seq[PathCommand], path: SVGPathCurve, firstEndPoint: CordPair): Seq[SVGPathCurve] =
    pathElems.foldLeft { (firstEndPoint, Seq.empty[SVGPathCurve]) } {
      case ((endPoint, accum), pathCommand) => {
        require(pathCommand.isAbsolute, "A <relative> path command in pyChart SVG, aborting.")
        require(pathCommand.args.length <= 1, s"A path command ${pathCommand.args} in " +
          s"path ${path.svgPath.pdContent} " +
          s"in a pyChart SVG has multiple arguments, aborting.")
        if (pathCommand.args.isEmpty)
          (endPoint, accum)
        else {
          val newEP = pathCommand match {
            case pathCommand: Line =>
              pathCommand.getEndPoint[Line](endPoint, pathCommand.isAbsolute, pathCommand.args)
            case pathCommand: Move =>
              pathCommand.getEndPoint[Move](endPoint, pathCommand.isAbsolute, pathCommand.args)
            case pathCommand: QBC =>
              pathCommand.getEndPoint[QBC](endPoint, pathCommand.isAbsolute, pathCommand.args)
            case _ =>
              println("not implemented path command encountered while splitting pyChart SVG.")
              ???
          }
          val moveCommand = Move(isAbsolute = true, args = Seq(MovePath(endPoint)))
          pathCommand match {
            case pathCommand: Move => (newEP, accum)
            case _ => (newEP, accum :+ createSVGCurvePath(path, moveCommand, pathCommand))
          }
        }
      }
    }
      ._2

  /*
  * This is for paths that are coming from generic SVGs, i.e. not PyChart SVGs.
  * Limitations: Does not handle paths with any command other than move and line command.
  * for SVGs coming from pycharts, see `splitPathPyChartSVGs`
  *
  * */
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
