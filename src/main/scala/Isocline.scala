package HistogramWaterVolume

import Util._

import scala.collection.mutable.ArrayBuffer

/** Represents a maximal block of neighboring cells, and their adjacency.
  *
  * @param id      Initially numbered from 0 to xSize*ySize-1, then merged.
  * @param cells   List of adjacent cells that share the same height.
  * @param height  Height of the cells in this Isocline.
  * @param isShore Whether or not this Isocline touches the edge of the range of cells.
  *
  *                Constructed and merged within getIsoclineInfoFromHisto()
  */
class Isocline(val id: IsoclineId, var cells: ArrayBuffer[Coords], val height: Height, var isShore: Boolean)
