package edu.psu.sagnik.research.vlganalysis.writer

import java.io.FileOutputStream
import java.nio.file.Paths

import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.batik.transcoder.{ TranscoderInput, TranscoderOutput }

/**
 * Created by schoudhury on 8/17/16.
 */
object PNGWriter {

  def apply(curveNo: String, orgSVGLoc: String, curveDir: String) = {
    val svgLoc = curveDir + "/" + orgSVGLoc.dropRight(4).split("/").last + "-Curve-" + curveNo + ".svg"
    val pngLoc = curveDir + "/" + orgSVGLoc.dropRight(4).split("/").last + "-Curve-" + curveNo + ".png"

    val svg_URI_input = Paths.get(svgLoc).toUri.toURL.toString
    val input_svg_image = new TranscoderInput(svg_URI_input)
    val png_ostream = new FileOutputStream(pngLoc)
    val output_png_image = new TranscoderOutput(png_ostream)
    val my_converter = new PNGTranscoder()
    my_converter.transcode(input_svg_image, output_png_image)
    println(s"written to $pngLoc")
  }

  def apply(orgSVGLoc: String, ext: String) = {
    val svgLoc = orgSVGLoc.dropRight(4) + "-" + ext + ".svg"
    val pngLoc = orgSVGLoc.dropRight(4) + "-" + ext + ".png"
    val svg_URI_input = Paths.get(svgLoc).toUri.toURL.toString
    val input_svg_image = new TranscoderInput(svg_URI_input)
    val png_ostream = new FileOutputStream(pngLoc)
    val output_png_image = new TranscoderOutput(png_ostream)
    val my_converter = new PNGTranscoder()
    my_converter.transcode(input_svg_image, output_png_image)
    println(s"written to $pngLoc")
  }
}
