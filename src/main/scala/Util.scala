package HistogramWaterVolume

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Util {
  // ========================================
  // Debug support
  // ========================================
  type Coords = (Int, Int)
  type Height = Int
  type WallHeight = Height
  type Depth = Int
  type Id = Int

  type IsoclineId = Id
  type IsoclineMap = mutable.HashMap[IsoclineId, Isocline]
  type IsoclineNbrMap = mutable.HashMap[IsoclineId, ArrayBuffer[IsoclineId]]

  type PondId = Id
  type PondMap = mutable.HashMap[PondId, Pond]
  type PondInfoMap = mutable.HashMap[PondId, (Int, Int)]

  class HwvException(msg: String) extends Exception(msg) {}

  def assertWrapper(cond: Boolean, funcName: => String, msg: String) = {
    if (!cond) {
      throw new HwvException(s"$funcName: $msg")
    }
  }

  def dprint(banner: String, msg: String) = {
    print(banner ++ msg)
    Console.flush()
  }

  def dprintln(banner: String, msg: String) = {
    println(banner ++ msg)
    Console.flush()
  }

  var isVerbose: Boolean = false
}
