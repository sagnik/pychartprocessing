package edu.psu.sagnik.research.vlganalysis.impl

import edu.psu.sagnik.research.inkscapesvgprocessing.impl.{GroupExtract, SVGPathBB}
import edu.psu.sagnik.research.inkscapesvgprocessing.model.{PathGroups, SVGGroup, SVGPath}
import edu.psu.sagnik.research.inkscapesvgprocessing.pathparser.impl.SVGPathfromDString
import edu.psu.sagnik.research.inkscapesvgprocessing.pathparser.model._
import edu.psu.sagnik.research.inkscapesvgprocessing.reader.XMLReader
import edu.psu.sagnik.research.inkscapesvgprocessing.transformparser.impl.TransformParser
import edu.psu.sagnik.research.inkscapesvgprocessing.writer.model.PathStyle
import edu.psu.sagnik.research.vlganalysis.model.{SVGPathCurve, SVGPathXML}

import scala.util.Try
import scala.xml.{Node, NodeSeq}

/**
  * Created by sagnik on 7/29/16.
  */
object PyChartSVGPathExtract {

  def apply(fileLoc:String)={
    val orgPaths=getPaths(XMLReader(fileLoc),GroupExtract.apply(fileLoc))
    //orgPaths.foreach(x=>println(x.svgPath.id))
    val usePaths=getDefPaths(XMLReader(fileLoc),orgPaths)

    orgPaths ++ usePaths
  }

  def getPaths(xmlContent:scala.xml.Elem, svgGroups:Seq[SVGGroup]):Seq[SVGPathCurve]=
    pathGroups(
      xmlContent \ "g",
      Map.empty[SVGPath,Seq[SVGGroup]],
      groupNoIdCounter = 0,
      pathNoIdCounter = 0,
      Map.empty[String,String]
    )
      .map{
        case (path,groups)=>
          path.copy(
            groups=groups
          )
      }
      .map {
        svgPath =>
          SVGPathBB(svgPath)
      }
      .map {
        svgPath =>
          SVGPathCurve(
            svgPath = svgPath,
            pathStyle = SVGPathExtract.getPathStyleObject(xml.XML.loadString(svgPath.pContent))
          )
      }
      .toSeq

  //some paths, especially the ones creating markers, come from def elements that are used
  //later. The paths that are in `use` command are already extracted by getPaths. Here
  // we will see if they are used somehow.

  lazy val changePOps= (pops:Seq[PathCommand],x:Float,y:Float)=>
    pops.flatMap{
      pop =>
        pop match {
          case pop: Move =>
            Some(
              Move(
                isAbsolute = pop.isAbsolute,
                args = pop.args.map(
                  popM => popM.copy(
                    eP=CordPair(x = popM.eP.x + x, y = popM.eP.y + y)
                  )
                )
              )
            )
          case pop: Line =>
            Some(
              Line(
                isAbsolute = pop.isAbsolute,
                args = pop.args.map(
                  popL =>
                    popL.copy(
                      eP=CordPair(x = popL.eP.x + x, y = popL.eP.y + y)
                    )
                )
              )
            )
          case _ =>
            throw new Exception("can't convert path used in <def> command")
            None //TODO:what happens when the def path has something other than move or lines
        }
    }

  lazy val pathDStringFromPath= (pops:Seq[PathCommand]) => pops.flatMap{
    pop=>
      pop match{
        case popM: Move => Some(
          "M "+popM.args.map(x=>x.eP.toString+","+x.eP.y.toString).mkString(" ")
        )
        case popL: Line => Some(
          "M "+popL.args.map(x=>x.eP.toString+","+x.eP.y.toString).mkString(" ")
        )
        case _ => None
      }
  }.mkString(" ")

  lazy val pathStringFromStyleAndPathDString=(pStyle:PathStyle,pathDString:String,pathID:String)=>{
  val styleStart=" style=\""
  val styles=List(
    pStyle.fill match {case Some(f) => "fill:"+f; case _ => "fill:none"},
    pStyle.fillRule match {case Some(f) => "fill-rule:"+f; case _ => "fill-rule:nonzero"},
    pStyle.fillOpacity match {case Some(f) => "fill-opacity:"+f; case _ => "fill-opacity:1"},
    pStyle.stroke match {case Some(f) => "stroke:"+f; case _ => "stroke:none"},
    pStyle.strokeWidth match {case Some(f) => "stroke-width:"+f; case _ => "stroke-width:1"},
    pStyle.strokeLinecap match {case Some(f) => "stroke-linecap:"+f; case _ => "stroke-linecap:butt"},
    pStyle.strokeLinejoin match {case Some(f) => "stroke-linejoin:"+f; case _ => "stroke-linejoin:miter"},
    pStyle.strokeMiterlimit match {case Some(f) => "stroke-miterlimit:"+f; case _ => "stroke-miterlimit:4"},
    pStyle.strokeDasharray match {case Some(f) => "stroke-dasharray:"+f; case _ => "stroke-dasharray:none"},
    pStyle.strokeDashoffset match {case Some(f) => "stroke-dashoffset:"+f; case _ => "stroke-dashoffset:0"},
    pStyle.strokeOpacity match {case Some(f) => "stroke-opacity:"+f; case _ => "stroke-opacity:1"}
  ).mkString(";")

    val styleEnd="\""
    val styleString=styleStart+styles+styleEnd

  "<path d=\"" +
    pathDStringFromPath +
    "\"" +
    "id=\"" +
    pathID+
    "\""+
    styleString+
    "xmlns=\"http://www.w3.org/2000/svg\" xmlns:svg=\"http://www.w3.org/2000/svg\"/>"

}


lazy val svgPathfromUsed= (p:SVGPath,pStyle:PathStyle,x:Float,y:Float,idIndex:Int) => {
    val changedPOps=changePOps(p.pOps:Seq[PathCommand],x,y)
    val changedPathDString=pathDStringFromPath(changedPOps)
    val changedPathContent=pathStringFromStyleAndPathDString(pStyle,changedPathDString,p.id)
    SVGPathBB(
      p.copy(
        id=p.id+"-use-"+idIndex.toString,
        pdContent=changedPathDString,
        pContent=changedPathContent,
        pOps = changedPOps
      )
    )
  }

  def getDefPaths(xmlContent:scala.xml.Elem, svgPaths:Seq[SVGPathCurve]):Seq[SVGPathCurve]={
    val useCommands= xmlContent \\ "use"
    //println(s"useCommands length ${useCommands.size}")
    import scala.util.{Try,Success,Failure}
    useCommands.zipWithIndex.flatMap{
      case (useCommand,index) =>
        val x=Try((useCommand \@ "x").toFloat) match {case Success(xExists)=> xExists; case Failure(e)=> 0f}
        val y=Try((useCommand \@ "y").toFloat) match {case Success(yExists)=> yExists; case Failure(e)=> 0f}
        val referringID=useCommand \@ "{http://www.w3.org/1999/xlink}href"
        if (0f.equals(x) || 0f.equals(y) || "".equals(referringID)) //TODO: for markers, we are assuming that x and y are both present here.
          None
        else {
          val svgPathsUsed=svgPaths.filter(x=>referringID.substring(1).equals(x.svgPath.id))
          if (svgPathsUsed.size==0) throw new Exception(s"no referred path in <def> with the referring id ${referringID} in <use>")
          else if (svgPathsUsed.size>1) throw new Exception(s"multiple referred paths in <def> with the same referring id ${referringID} in <use>")
          Some(
            svgPathsUsed.head.copy(
              svgPath=svgPathfromUsed(
                svgPathsUsed.head.svgPath,
                svgPathsUsed.head.pathStyle,
                x,
                y,
                index
              )
            )
          )
        }
    }
  }

  def pathGroups(tlGs: NodeSeq,
                 parentMap:Map[SVGPath,
                   Seq[SVGGroup]],
                 groupNoIdCounter:Int,
                 pathNoIdCounter:Int,
                 dStringMap:Map[String,String]
                ):Map[SVGPath,Seq[SVGGroup]]=

    if (tlGs.isEmpty) parentMap
    else {
      var gCounter=groupNoIdCounter
      var pCounter=pathNoIdCounter
      var pathsMappedbyDString=dStringMap

      val newGId = tlGs.head.attribute("id") match {
        case Some(idExists) => idExists.text
        case _ =>
          gCounter+=1
          "noID"+gCounter.toString
      }
      val newtlGs = tlGs.head \ "g"
      val parentMapThistlgS =
        newtlGs.map(x => {
          val childId=
            x.attribute("id") match {
              case Some(idExists) => idExists.text
              case _ =>
                gCounter+=1
                "noID" + gCounter.toString
            }

          val group=
            SVGGroup(
              id= childId,
              gtContent = x \@ "transform",
              gContent= x.toString,
              transformOps = TransformParser(x \@ "transform")
            )

          val paths=
            (x \\ "path").map(
              pathNode=>
                SVGPath(
                  pathNode.attribute("id") match {
                    case Some(idExists) =>
                      val pdContent=pathNode.attribute("d") match {case Some(con)=>con.text case _ => ""}
                      pathsMappedbyDString += (pdContent -> idExists.text)
                      idExists.text
                    case _ =>
                      val pdContent=pathNode.attribute("d") match {case Some(con)=>con.text case _ => ""}
                      val pathCounter=
                        dStringMap.get(pdContent) match{
                          case Some(existingPCounter) =>  existingPCounter //this path was seen before
                          case _ =>
                            pCounter+=1
                            pCounter.toString
                        }
                      pathsMappedbyDString += (pdContent -> pCounter.toString)
                      "noID"+pathCounter
                  },
                  pdContent = pathNode.attribute("d") match {case Some(con)=>con.text case _ => ""},
                  pContent=pathNode.toString(),
                  pOps = SVGPathfromDString.getPathCommands(pathNode.attribute("d") match {case Some(con)=>con.text case _ => ""}),
                  groups=List.empty[SVGGroup],
                  transformOps = TransformParser(pathNode \@ "transform"),
                  bb=None
                )
            )

          val combinedMap=
            combineMaps(
              parentMap,
              paths.map(x=>(x,Seq(group))).toMap //this is grouping by id, which is weird.
            )

          combinedMap
        }
        )


      val newParentMap= parentMapThistlgS.foldLeft(parentMap){case (accum,toBeMerged) => combineMaps(accum,toBeMerged)}

      pathGroups(tlGs.tail ++ newtlGs, newParentMap,gCounter,pCounter,pathsMappedbyDString)
    }

  lazy val combineMaps = (accum:Map[SVGPath,Seq[SVGGroup]],toBeMerged:Map[SVGPath,Seq[SVGGroup]]) =>
  {
    val comb=accum.toList ++ toBeMerged.toList
    comb.groupBy(_._1).map{case (k,v) => (k -> v.map(_._2).flatten.distinct) }

  }

}


//def styleFromPathString



