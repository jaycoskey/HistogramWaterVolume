package HistogramWaterVolume

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HistoSuite extends FunSuite {
  test("HistoSuite: Init by function test") {
    val xSize = 3
    val ySize = 3
    var histo: Histo = new Histo(xSize, ySize)
    histo.initFromFunction((x: Int, y: Int) => x + y)
    var sum = 0
    for (y <- 0 until ySize)
      for (x <- 0 until xSize)
        sum += histo.getHeight(x, y)
    assert(sum == 18)
  }
}
