package edu.psu.sagnik.research.vlganalysis.reader

import java.io.File

/**
 * Created by szr163 on 11/8/15.
 */
object XMLReader {
  def apply(fileLoc:String)=scala.xml.XML.loadFile(new File(fileLoc))

}
