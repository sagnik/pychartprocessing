package edu.psu.sagnik.research.vlganalysis.impl

/**
 * Created by sagnik on 9/7/16.
 */
object CurveExtractionArchitecture {
  def main(args: Array[String]) = {
    val pyLoc = "../linegraphproducer/data/0/0.svg"
    val colorsMap = ColorMap.colors
    SplitPaths(pyLoc, colorsMap.values.toSeq, fromPython = true)
    println("created atomic SVG for original plot")
    CreateCurvesColor(pyLoc.dropRight(4) + "-sps.svg", createImages = true, CreateCurvesColor.colorBasedSegmentation)
    println("created color based segmentation for atomic svg")
    MarkerDetection(pyLoc.dropRight(4) + "-sps.svg", createImages = true)
    println("created marker based segmentation for atomic svg")
  }
}
