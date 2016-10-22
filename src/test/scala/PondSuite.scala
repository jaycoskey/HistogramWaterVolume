package HistogramWaterVolume

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import _root_.HistogramWaterVolume.Util._

@RunWith(classOf[JUnitRunner])
class PondSuite extends FunSuite {
  test("PondSuite: pond count test: \"flat\"") {
    val pondCount = TestHistos.flatPondInfoMap.keys.size
    assert(pondCount == 0)
  }

  test("PondSuite: pond count test: \"peak\"") {
    val pondCount = TestHistos.peakPondInfoMap.keys.size
    assert(pondCount == 0)
  }

  test("PondSuite: pond count test: \"four\"") {
    val pondCount = TestHistos.fourPondInfoMap.keys.size
    assert(pondCount == 0)
  }

  test("PondSuite: minima count test: \"bend\"") {
    val minima = TestHistos.bendMinima
    assert(minima.size == 1)
  }

  test("PondSuite: pond count test: \"bend\"") {
    val pondCount = TestHistos.bendPondInfoMap.keys.size
    assert(pondCount == 1)
  }

  test("PondSuite: pond cell count test: \"bend\"") {
    val pondIds = TestHistos.bendPondInfoMap.keys
    val pondCount = pondIds.size
    // assert(pondCount == 1)
    if (pondCount == 1) {
      val pondId: PondId = pondIds.toList.head
      val cellCount = TestHistos.bendPondInfoMap(pondId)._1
      assert(cellCount == 3)
    }
  }
  test("PondSuite: pond volume test: \"bend\"") {
    val pondIds = TestHistos.bendPondInfoMap.keys
    val pondCount = pondIds.size
    // assert(pondCount == 1)
    if (pondCount == 1) {
      val pondId: PondId = pondIds.toList.head
      val volume = TestHistos.bendPondInfoMap(pondId)._2
      assert(volume == 5)
    }
  }

  test("PondSuite: pond minima count test: \"eyes\"") {
    val minima = TestHistos.eyesMinima
    assert(minima.size == 8)
  }

  test("PondSuite: pond size and volume test: \"eyes\"") {
    val pondCount = TestHistos.eyesPondInfoMap.keys.size
    assert(pondCount == 2)
    val sortedPondIds = TestHistos.eyesPondInfoMap.keys.toList.sortBy(TestHistos.eyesPondInfoMap(_)._1)
    assert(TestHistos.eyesPondInfoMap(sortedPondIds(0)) == (47, 78))  // "eyes" pond
    assert(TestHistos.eyesPondInfoMap(sortedPondIds(1)) == (23, 42))  // spiral pond
  }
}
