package HistogramWaterVolume

import scala.collection.mutable.ArrayBuffer

import _root_.HistogramWaterVolume.Histo._
import _root_.HistogramWaterVolume.IsoclineInfo._
import _root_.HistogramWaterVolume.Pond._
import _root_.HistogramWaterVolume.PondInfo._
import _root_.HistogramWaterVolume.PondSearch._
import _root_.HistogramWaterVolume.Util._

object HistogramWaterVolume {
  def main(args: Array[String]) = {
    // assert(args.length == 1)
    // (1) Read histogram data from stdin
    val filename = "flat.hf3d" /* args(1) */
    val histo: Histo = mkHistoFromResourceFile(filename)

    // (2) Create maximal single-valued regions (isoclines) with isShore tags.
    // VERBOSE INFO
    val isoInfo: IsoclineInfo = mkIsoclineInfoFromHisto(histo, "main")
    val xSize = histo.xSize
    val ySize = histo.ySize
    isoInfo.dprintIsoclineIdsWithLayout(xSize, ySize, "main")
    var cellCount: Int = 0
    var isoCount: Int = 0
    isoInfo.isoMap.keys.toList.distinct.foreach(id => {
      isoCount += 1
      cellCount += isoInfo.isoMap(id).cells.length
    })
    // dprintln(banner="INFO:", "main: Histo has %d Isoclines with %d cells".format(isoCount, cellCount))

    // (3 a&b) Determine ponds (maximal regions that can hold water).
    val minimumIds: ArrayBuffer[IsoclineId] = mkLocalMinimumIdsFromIsoclineInfo(isoInfo)
    val pondMap: PondMap = mkPondMapFromLocalMinimumIds("main", isoInfo, minimumIds)

    // (4) Get summary info on ponds.
    val pondInfo: PondInfoMap = mkPondInfoFromPondMap("main", isoInfo.isoMap, pondMap)

    // (5) Report number of ponds, each with its cell counts and water volume.
    dprintPondInfo(pondInfo, "main")
  }
}
