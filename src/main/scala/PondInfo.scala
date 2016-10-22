package HistogramWaterVolume

import Util._

object PondInfo {
  /**
    *
    * @param pondInfo Summary info (cellCount and volume) of all Ponds
    * @param name     Name of Histo for print statements
    */
  def dprintPondInfo(pondInfo: PondInfoMap, name: String): Unit = {
    if (isVerbose) {
      dprintln(banner = "INFO:", s"Pond Report for $name")
      pondInfo.keys.foreach(pondId => {
        val (cellCount, volume): (Int, Int) = pondInfo(pondId)
        dprintln(banner = "INFO:", s"\tPond ID #%d: cellCount=$cellCount, volume=$volume")
      })
    }
  }

  /**
    *
    */
  def mkPondInfoFromPondMap(name: String, isoMap: IsoclineMap, pondMap: PondMap): PondInfoMap = {
    val pondInfo: PondInfoMap = new PondInfoMap()
    pondMap.keys.foreach((pondId: PondId) => {
      val pond: Pond = pondMap(pondId)
      val cellCount: Int = pond.isoclines.map(isoId => isoMap(isoId).cells.size).sum
      val wallHeight = pond.wallHeight
      val volume: Int = pond.isoclines.map(isoId => {
        val iso = isoMap(isoId)
        val depth = wallHeight - iso.height
        iso.cells.size * depth
      }).sum
      pondInfo(pondId) = (cellCount, volume)
    })
    pondInfo
  }
}
