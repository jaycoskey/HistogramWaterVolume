package HistogramWaterVolume

import Util._

class IsoclineLookup(val xSize: Int, val ySize: Int) {
  var baseValues: Array[IsoclineId] = (0 until (xSize * ySize)).toArray
  val size: Int = baseValues.length

  /**
    *
    * @param id0 The ID of an Isocline that might have merged with another Isoclines.
    * @return The "terminal" ID, taking into account merges with other Isoclines.
    *         If values[] = { 0, 1, 0, 2 }
    *         then base2Term(3) = 0, since 3 -> 2 -> 0
    */
  def base2Term(id0: Id): Id = {
    def iter(id0: Id, id: Id): Id = {
      val idNext: Id = baseValues(id)
      if (idNext == id) id
      else iter(id0, idNext)
    }
    iter(id0, id0)
  }

  def baseId(x: Int, y: Int): Id = {
    baseValues(xSize * y + x)
  }

  def baseVal(i: Int): Id = baseValues(i)

  def dprintLookup(): Unit = {
    if (isVerbose) {
      dprint(banner = "INFO:", "Lookup: ")
      (0 until (xSize * ySize)).foreach(i => {
        print("%d => %d; ".format(i, baseValues(i)))
      })
      println("")
      Console.flush()
    }
  }

  def setBaseId(x: Int, y: Int, id: Id): Unit = {
    baseValues(xSize * y + x) = id
  }

  def setBaseVal(i: Int, id: Id): Unit = {
    baseValues(i) = id
  }

  def termId(x: Int, y: Int): Id = {
    val id0 = baseValues(xSize * y + x)
    base2Term(id0)
  }

  def termVal(i: Int): Id = base2Term(baseValues(i))
}
