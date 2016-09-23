package edu.psu.sagnik.research.vlganalysis.test

import edu.psu.sagnik.research.inkscapesvgprocessing.model.Rectangle
import edu.psu.sagnik.research.vlganalysis.impl.CreateCurvesColor
import org.scalatest.FunSpec

/**
 * Created by sagnik on 9/23/16.
 */
class CaretTest extends FunSpec {
  describe("takes two rectangles and sees if we can correctly predict whether they are a ``caret`` of particular style") {
    it("takes two rectangles and sees if we can correctly predict whether they are a ``caret`` of particular style") {
      val rA = Rectangle(0f, 0f, 10f, 10f)
      val rB = Rectangle(0f, 10f, 10f, 20f)
      val rC = Rectangle(10f, 10f, 20f, 20f)
      val rD = Rectangle(10f, 0f, 20f, 10f)

      val vR1 = Rectangle(0f, 0f, 0f, 20f)
      val vR2 = Rectangle(10f, 0f, 10f, 20f)
      val vR3 = Rectangle(20f, 0f, 20f, 20f)
      val hR1 = Rectangle(0f, 0f, 20f, 0f)
      val hR2 = Rectangle(0f, 10f, 20f, 10f)
      val hR3 = Rectangle(0f, 20f, 20f, 20f)

      import edu.psu.sagnik.research.vlganalysis.impl.MarkerHelper._
      assert(isLeftCaret(rC, rD, vR3))
      assert(!isRightCaret(rC, rD, vR3))
      assert(isLeftCaret(rD, rC, vR3))
      assert(!isRightCaret(rD, rC, vR3))
      assert(!isLeftCaret(rA, rD, vR3))

      assert(isUpCaret(rB, rC, hR3))
      assert(!isDownCaret(rB, rC, hR3))
      assert(isDownCaret(rA, rD, hR1))

    }
  }

}
