package edu.psu.sagnik.research.vlganalysis.impl

import edu.psu.sagnik.research.inkscapesvgprocessing.impl.{ GroupExtract, SVGPathBB }
import edu.psu.sagnik.research.inkscapesvgprocessing.model.{ SVGGroup, SVGPath }
import edu.psu.sagnik.research.inkscapesvgprocessing.pathparser.impl.SVGPathfromDString
import edu.psu.sagnik.research.inkscapesvgprocessing.reader.XMLReader
import edu.psu.sagnik.research.inkscapesvgprocessing.transformparser.impl.TransformParser
import edu.psu.sagnik.research.inkscapesvgprocessing.writer.model.PathStyle
import edu.psu.sagnik.research.vlganalysis.model._

import scala.xml.NodeSeq

/**
 * Created by szr163 on 11/8/15.
 */

object SVGPathExtract {

  def apply(fileLoc: String, sps: Boolean) =
    if (sps)
      getPaths(XMLReader(fileLoc))
    else
      getPaths(XMLReader(fileLoc), GroupExtract.apply(fileLoc))

  def getPaths(xmlContent: scala.xml.Elem): Seq[SVGPathCurve] = {
    //This is a path splitted file as created by edu.psu.sagnik.research.vlganalysis.impl.SplitPaths.
    //it doesn't have any group.
    //(xmlContent \\ "path").foreach(x => println(x))
    (xmlContent \\ "path").map(x =>
      SVGPathXML(
        svgPath = SVGPath(
          id = x \@ "id",
          pdContent = x \@ "d",
          pOps = SVGPathfromDString.getPathCommands(x.attribute("d") match { case Some(con) => con.text case _ => "" }),
          pContent = x.toString,
          groups = Seq.empty[SVGGroup],
          transformOps = TransformParser(x \@ "transform"),
          bb = None
        ),
        styleXML = x
      ))
      .map(
        x =>
          SVGPathXML(
            svgPath = SVGPathBB(x.svgPath),
            styleXML = x.styleXML
          )
      )
      .map(x =>
        getPathStyleObject(x))
      .filterNot(a =>
        ("#ffffff".equals(a.pathStyle.fill.getOrElse("#000000")) && "none".equals(a.pathStyle.stroke.getOrElse("none"))) || //the path has no color, either from fill or stroke
          ("#ffffff".equals(a.pathStyle.stroke.getOrElse("none")) && "#none".equals(a.pathStyle.fill.getOrElse("#000000"))))

  }

  def getPaths(xmlContent: scala.xml.Elem, svgGroups: Seq[SVGGroup]): Seq[SVGPathCurve] = {
    //There's exactly one group with a translate operation, but that might not have an ID.
    val gid = if (((xmlContent \ "g") \@ "id").isEmpty) "g1" else ((xmlContent \ "g") \@ "id")
    val gtContent = (xmlContent \ "g") \@ "transform"
    val gContent = (xmlContent \ "g").toString
    val transformOps = TransformParser((xmlContent \ "g") \@ "transform")
    ((xmlContent \ "g") \ "path").map(x =>
      SVGPathXML(
        svgPath = SVGPath(
          id = x \@ "id",
          pdContent = x \@ "d",
          pOps = SVGPathfromDString.getPathCommands(x.attribute("d") match { case Some(con) => con.text case _ => "" }),
          pContent = x.toString,
          groups = List(SVGGroup(
            id = gid,
            gtContent = gtContent,
            gContent = gContent,
            transformOps = transformOps
          )).toIndexedSeq,
          transformOps = TransformParser(x \@ "transform"),
          bb = None
        ),
        styleXML = x
      ))
      .map(x => SVGPathXML(svgPath = SVGPathBB(x.svgPath), styleXML = x.styleXML)).map(x => getPathStyleObject(x))
      .filterNot(a =>
        ("#ffffff".equals(a.pathStyle.fill.getOrElse("#000000")) && "none".equals(a.pathStyle.stroke.getOrElse("none"))) || //the path has no color, either from fill or stroke
          ("#ffffff".equals(a.pathStyle.stroke.getOrElse("none")) && "none".equals(a.pathStyle.fill.getOrElse("#000000"))))
  }

  def getPathStyleObject(x: SVGPathXML): SVGPathCurve = {
    SVGPathCurve(
      svgPath = x.svgPath,
      pathStyle = getPathStyleObject(x.styleXML)
    )
  }

  def getPathStyleObject(styleXML: NodeSeq): PathStyle = {
    PathStyle(
      fill = returnPatternFillOrStroke(styleXML \@ "style", "fill"),
      fillRule = returnPattern(styleXML \@ "style", "fill-rule"),
      fillOpacity = returnPattern(styleXML \@ "style", "fill-opacity"),
      stroke = returnPatternFillOrStroke(styleXML \@ "style", "stroke"),
      strokeWidth = returnPattern(styleXML \@ "style", "stroke-width"),
      strokeLinecap = returnPattern(styleXML \@ "style", "stroke-linecap"),
      strokeLinejoin = returnPattern(styleXML \@ "style", "stroke-linejoin"),
      strokeMiterlimit = returnPattern(styleXML \@ "style", "stroke-miterlimit"),
      strokeDasharray = returnPattern(styleXML \@ "style", "stroke-dasharray"),
      strokeDashoffset = returnPattern(styleXML \@ "style", "stroke-dashoffset"),
      strokeOpacity = returnPattern(styleXML \@ "style", "stroke-opacity")
    )
  }
  val returnPattern = (pC: String, s: String) =>
    if (!pC.split(";").exists(x => x.contains(s))) None
    else Some(pC.split(";").filter(x => x.contains(s)).head.split(":")(1))

  val returnPatternFillOrStroke = (pC: String, s: String) =>
    if (!pC.split(";").exists(x => x.contains(s))) None
    else if ("none".equalsIgnoreCase(pC.split(";").filter(x => x.contains(s)).head.split(":")(1)))
      None
    else if ("#ffffff".equalsIgnoreCase(pC.split(";").filter(x => x.contains(s)).head.split(":")(1)))
      None
    else
      Some(pC.split(";").filter(x => x.contains(s)).head.split(":")(1))

}
