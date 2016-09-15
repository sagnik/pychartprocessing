package edu.psu.sagnik.research.vlganalysis.impl

/**
 * Created by sagnik on 9/7/16.
 */
object CurveExtractionArchitecture {
  def main(args: Array[String]) = {
    val pyLocBase = s"../linegraphproducer/data/"
    //val colorsMap = ColorMap.colors
    (19 until 20).foreach { index =>
      //val pyLoc = pyLocBase + s"$index/$index.svg"
      val pyLoc = "src/test/resources/19.svg"
      println(s"working with $pyLoc")
      SplitPaths(pyLoc, fromPython = true)
      println(s"created atomic SVG for original plot ${pyLoc.dropRight(4) + "-sps.svg"}")
      CreateCurvesColor(
        pyLoc.dropRight(4) + "-sps.svg",
        createImages = true,
        CreateCurvesColor.colorBasedSegmentation
      )
      println("created color based segmentation for atomic svg")
      MarkerDetection(pyLoc.dropRight(4) + "-sps.svg", createImages = true)
      println("created marker based segmentation for atomic svg")
    }
  }
}
