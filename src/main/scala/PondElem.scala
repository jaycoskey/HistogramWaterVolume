package HistogramWaterVolume

import Util._

/** Represents an Isocline that might belong to a Pond being constructed.
  * The term "Elem" is used instead of "Iso" to avoid confusion.
  *
  * @param isoId            The ID of this Isocline.
  * @param isShore          Whether or not this iso
  * @param minMaxPathHeight Take the max height along a path from the minimumIso this Isocline,
  *                         then take the minimum of that value along all such paths.
  *                         For a PondIso that isShore, the pond's wallHeight cannot be any higher,
  *                         because any higher water level would flow to the shore.
  */
class PondElem(val isoId: IsoclineId, val isShore: Boolean, val minMaxPathHeight: Height)
