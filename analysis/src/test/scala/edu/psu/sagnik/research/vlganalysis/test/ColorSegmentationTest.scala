package edu.psu.sagnik.research.vlganalysis.test

import edu.psu.sagnik.research.inkscapesvgprocessing.pathparser.impl.SVGPathfromDString
import edu.psu.sagnik.research.inkscapesvgprocessing.reader.XMLReader
import edu.psu.sagnik.research.vlganalysis.impl.CreateCurvesColor
import org.scalatest.FunSpec

/**
 * Created by sagnik on 8/14/16.
 */
class ColorSegmentationTest extends FunSpec {

  lazy val getPathCommands = (fileLoc: String) => (XMLReader(fileLoc) \\ "path").map {
    pathString =>
      {
        (pathString \@ "d")
      }
  }.toSet

  describe("takes an SVG linegraph created with two functions and checks if the segmentation is correct") {
    it("Curves separated by the algorithm should have same dStrings.") {
      import DataLocations._
      import edu.psu.sagnik.research.vlganalysis.impl.CreateCurvesColor._
      CreateCurvesColor(combinedAtomicSVG, createImages = true, colorBasedSegmentation)

      val curve0SVGPathsGold = getPathCommands(curve0AtomicSVGGold)
      val curve0SVGPathsCreated = getPathCommands(curve0AtomicSVGCreated)

      val curve1SVGPathsGold = getPathCommands(curve1AtomicSVGGold)
      //curve1SVGPathsGold.foreach(println)
      //println("-------------------------------------")
      val curve1SVGPathsCreated = getPathCommands(curve1AtomicSVGCreated)
      //curve1SVGPathsCreated.foreach(println)

      assert(curve0SVGPathsGold.equals(curve0SVGPathsCreated))
      assert(curve1SVGPathsGold.equals(curve1SVGPathsCreated))

    }
  }

}
