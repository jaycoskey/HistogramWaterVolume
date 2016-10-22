package HistogramWaterVolume

import _root_.HistogramWaterVolume.IsoclineInfo._

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class IsoclineSuite extends FunSuite {
  test("IsoclineSuite: Histo from function: size and neighbor test: four-square") {
    val xSize = 6
    val ySize = 6
    val histo: Histo = new Histo(xSize, ySize)
    def f(x: Int, y: Int): Int = {
      // TODO: Use binary representation of coords to get more compact definition.
      if ((y == 0 && x <= 2) || x == 0 || (y == 5 && x <= 1)) 0
      else if ((x == 1 && y >= 1 && y <= 4)
        || (x == 2 && y == 1)
        || (x == 3 && y <= 3)) 1
      else if ((y == 0 && x >= 4) || x == 5 || (y == 5 && x >= 3)) 2
      else if ((x == 2 && y >= 2)
        || (x == 3 && y == 4)
        || (x == 4 && y >= 1 && y <= 4)) 3
      else -1
    }
    histo.initFromFunction(f)
    val isoInfo: IsoclineInfo = mkIsoclineInfoFromHisto(histo, "four-square")

    // Isocline Map
    isoInfo.dprintIsoclineIdsWithLayout(xSize, ySize, "four-square")
    assert(isoInfo.isoMap.size == 4)

    // Isocline Neighbor Map
    isoInfo.dprintNbrs("four-square")
    assert(isoInfo.getNbrCount == 10) // Four regions, each connected to the others, except one pair: 2*C(4,2) - 2 = 10.
  }

  test("IsoclineSuite: isocline size test: \"solo\"") {
    TestHistos.soloInfo.dprintIsoclineIdsWithLayout(TestHistos.soloHisto.xSize, TestHistos.soloHisto.ySize, "solo")
    TestHistos.soloInfo.dprintNbrs("solo")
    assert(TestHistos.soloInfo.getCellCount == 1)
    assert(TestHistos.soloInfo.getIsoCount == 1)
    assert(TestHistos.soloInfo.getNbrCount == 0)
  }

  test("IsoclineSuite: isocline size test: \"same\"") {
    TestHistos.sameInfo.dprintIsoclineIdsWithLayout(TestHistos.sameHisto.xSize, TestHistos.sameHisto.ySize, "same")
    TestHistos.sameInfo.dprintNbrs("same")
    assert(TestHistos.sameInfo.getCellCount == 2)
    assert(TestHistos.sameInfo.getIsoCount == 1)
    assert(TestHistos.sameInfo.getNbrCount == 0)
  }

  test("IsoclineSuite: isocline size test: \"diff\"") {
    TestHistos.diffInfo.dprintIsoclineIdsWithLayout(TestHistos.diffHisto.xSize, TestHistos.diffHisto.ySize, "diff")
    TestHistos.diffInfo.dprintNbrs("diff")
    assert(TestHistos.diffInfo.getCellCount == 2)
    assert(TestHistos.diffInfo.getIsoCount == 2)
    assert(TestHistos.diffInfo.getNbrCount == 2)
  }

  test("IsoclineSuite: isocline size test: \"flat\"") {
    TestHistos.flatInfo.dprintIsoclineIdsWithLayout(TestHistos.flatHisto.xSize, TestHistos.flatHisto.ySize, "flat")
    TestHistos.flatInfo.dprintNbrs("flat")
    assert(TestHistos.flatInfo.getCellCount == 9)
    assert(TestHistos.flatInfo.getIsoCount == 1)
    assert(TestHistos.flatInfo.getNbrCount == 0)
  }

  test("IsoclineSuite: isocline size test: \"hook\"") {
    TestHistos.hookInfo.dprintIsoclineIdsWithLayout(TestHistos.hookHisto.xSize, TestHistos.hookHisto.ySize, "hook")
    TestHistos.hookInfo.dprintNbrs("hook")
    assert(TestHistos.hookInfo.getCellCount == 9)
    assert(TestHistos.hookInfo.getIsoCount == 2)
    assert(TestHistos.hookInfo.getNbrCount == 2)
  }

  test("IsoclineSuite: isocline size test: \"peak\"") {
    assert(TestHistos.peakInfo.getCellCount == 25)
    assert(TestHistos.peakInfo.getIsoCount == 25)
    assert(TestHistos.peakInfo.getNbrCount == 80)
  }

  test("IsoclineSuite: isocline size test: \"four\"") {
    val name = "four"
    val (xSize, ySize) = (6, 6)
    assert(TestHistos.fourInfo.getCellCount == 36)
    assert(TestHistos.fourInfo.getIsoCount == 4)
    if (Util.isVerbose) {
      println("===== IsoclineInfo for \"%s\" =====".format(name))
    }
    TestHistos.fourInfo.dprintIsoclineIdsWithLayout(xSize, ySize, name)
    TestHistos.fourInfo.dprintNbrs(name)
    assert(TestHistos.fourInfo.getNbrCount == 10)
  }

  test("IsoclineSuite: isocline size test: \"bend\"") {
    assert(TestHistos.bendInfo.getIsoCount == 17)
  }

  test("IsoclineSuite: isocline size test: \"eyes\"") {
    assert(TestHistos.eyesInfo.getIsoCount == 33)
  }
}
