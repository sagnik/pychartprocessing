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
            mC.args(0).eP.x +
            "," +
            mC.args(0).eP.y +
            " L " +
            lC.args(0).eP.x +
            "," +
            lC.args(0).eP.y,
          pContent = createSVGPathString(path.pathStyle, path.svgPath.transformOps, mC, lC, path.svgPath.id),
          pOps = Seq(mC, lC)
        )
      ),
      pathStyle = path.pathStyle
    )

  def createSVGPathString(s: PathStyle, tOps: Seq[TransformCommand], mC: Move, lC: Line, id: String): String = {

    val fill=s.fill
    //val fillRule=s.fillRule match {case Some(x)=>"fill-rule:"+x+";" case _ => ""}
    //val fillOpacity=s.fillOpacity match {case Some(x)=>"fill-opacity:"+x+";" case _ => ""}
    val stroke=s.stroke
    val strokeWidth=s.strokeWidth
    val strokeLinecap=s.strokeLinecap match {case Some(x)=>"stroke-linecap:"+x+";" case _ => ""}
    val strokeLinejoin=s.strokeLinejoin match {case Some(x)=>"stroke-linejoin:"+x+";" case _ => ""}
    val strokeMiterlimit=s.strokeMiterlimit match {case Some(x)=>"stroke-miterlimit:"+x+";" case _ => ""}
    val strokeDasharray=s.strokeDasharray match {case Some(x)=>"stroke-dasharray:"+x+";" case _ => ""}
    val strokeDashoffset=s.strokeDashoffset match {case Some(x)=>"stroke-dashoffset:"+x+";" case _ => ""}
    val strokeOpacity=s.strokeOpacity match {case Some(x)=>"stroke-opacity:"+x+";" case _ => ""}

    val styleString =
      fill+fillRule+fillOpacity+stroke+strokeWidth+strokeLinecap+strokeLinejoin+strokeMiterlimit+strokeDasharray+strokeDashoffset+strokeOpacity

    val transformString = "matrix(1.0,0.0,0.0,0.0,1.0,0.0)" //this is equivalent to no transformation, this will be corrected when
    //we map this with SVGPathBB

    val dString = "M " +
      mC.args(0).eP.x +
      "," +
      mC.args(0).eP.y +
      " L " +
      lC.args(0).eP.x +
      "," +
      lC.args(0).eP.y

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
            val lastEp = pathElem.getEndPoint[Line](lep,pathElem.isAbsolute,pathElem.args)
            val moveCommand = Move(isAbsolute = true, args = Seq(MovePath(lep)))
            val lineCommand = Line(isAbsolute = true, args = Seq(LinePath(lastEp)))
            val newSvgCurvePath = createSVGCurvePath(path, moveCommand, lineCommand)
            pathSArr :+ newSvgCurvePath
          }
          else {
            splitPath(
              pathElem.args.map(x => Line(isAbsolute = pathElem.isAbsolute, args = Seq(LinePath(x.asInstanceOf[LinePath].eP)))),
              path,
              lep,
              pathSArr
            )
          }
        }
        else {
          val lastEp = pathElem.getEndPoint[pathElem.type](lep,pathElem.isAbsolute,pathElem.args)
          pathSArr
        }
      case pathElem :: rest => {
        val lastEndPoint = pathElem.getEndPoint[pathElem.type](lep,pathElem.isAbsolute,pathElem.args)
        if (pathElem.isInstanceOf[Line]) {
          if (pathElem.args.isEmpty)
            pathSArr
          else if (pathElem.args.length == 1) {
            val lastEp = pathElem.getEndPoint[Line](lep,pathElem.isAbsolute,pathElem.args)
            val moveCommand = Move(isAbsolute = true, args = Seq(MovePath(lep)))
            val lineCommand = Line(isAbsolute = true, args = Seq(LinePath(lastEp)))
            val newSvgCurvePath = createSVGCurvePath(path, moveCommand, lineCommand)
            splitPath(
              rest,
              path,
              lastEndPoint,
              pathSArr :+ newSvgCurvePath
            )
          }
          else {
            val splitPaths = pathElem.args.map(x => Line(isAbsolute = pathElem.isAbsolute, args = Seq(LinePath(x.asInstanceOf[LinePath].eP))))
            splitPath(
              splitPaths ++ rest,
              path,
              lep,
              pathSArr
            )
          }
        }
        else
          splitPath(rest, path, lastEndPoint, pathSArr)

      }
    }

  def apply(loc:String)={
    val cS= SVGPathExtract(loc,false)
    //Seq(svgpathCurves(0)).foreach(x=>println(x.svgPath.id,x.svgPath.pdContent,x.svgPath.pOps))
    val (fillExists,noFill)=cS.partition(x=> {
      (x.pathStyle.fill match{
        case Some(fill) => true
        case _ => false
      }) && ("none".equals(x.pathStyle.stroke.getOrElse("none")) || "#ffffff".equals(x.pathStyle.stroke.getOrElse("#ffffff")))
    }
    )

    val spPath=noFill.flatMap(c =>
      SplitPaths.splitPath(
        c.svgPath.pOps.slice(1, c.svgPath.pOps.length),
        c,
        CordPair(c.svgPath.pOps(0).args(0).asInstanceOf[MovePath].eP.x, c.svgPath.pOps(0).args(0).asInstanceOf[MovePath].eP.y),
        Seq.empty[SVGPathCurve]
      ))
    SVGWriter(spPath,loc,"sps")
  }
}

object TestSplitPaths{
  def main(args: Array[String]):Unit= {
    //val loc="src/test/resources/hassan-Figure-2.svg"
    //val loc="data/10.1.1.164.2702-Figure-2.svg"
    //val loc="data/10.1.1.100.3286-Figure-9.svg"
    //val loc="data/10.1.1.104.3077-Figure-1.svg"
    //val loc="src/test/resources/10.1.1.108.5575-Figure-16.svg"
    val loc="src/test/resources/10.1.1.113.223-Figure-10.svg"
    SplitPaths(loc)
  }

}