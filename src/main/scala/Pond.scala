package HistogramWaterVolume

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

import PondSearch._
import Util._

// ========================================
// Pond
// ========================================

// cellCount & volume

/**
  *
  * @param id         Unique ID.
  * @param isoclines  Collection of Isoclines that belong to this Pond.
  * @param wallHeight The min height of neighboring Isoclines.
  */
class Pond(val id: PondId, val isoclines: ArrayBuffer[IsoclineId], val wallHeight: WallHeight) {
  def containsIsoId(id: IsoclineId): Boolean = {
    isoclines.contains(id)
  }
}

object Pond {
  val dummyPond = new Pond(0, new ArrayBuffer[IsoclineId](), 0)

  // TODO: Complete this work in progress
  def mkPondFromMinimumId(name: String, isoInfo: IsoclineInfo, minimumId: IsoclineId): Pond = {
    val search = mkPondSearchFromMinimumId(name, isoInfo, minimumId)

    // Iterate the inspection of the next round of neighbors of the current set of cells being inspected.
    while (!search.isPondComplete) {
      val initialElemCount = search.elemCount
      println("INFO: %s: Work in progress: Top of loop: Elems: %d = %d + %d".format(
        name, search.elemCount(), search.corePondElems.size, search.bdryPondElems.size)
      )
      val coreIsoIds: ArrayBuffer[IsoclineId] = search.pondElems2IsoIds(search.corePondElems)
      search.dprintPondSearch()

      //
      // Extend the boundary of the Pond's possible Isoclines by finding neighbors of the Isoclines on the boundary.
      //
      val nbrBdryPondElemMap: mutable.HashMap[IsoclineId, ArrayBuffer[PondElem]] = new mutable.HashMap
      search.bdryPondElems.foreach((bdryPondElem: PondElem) => {
        val bdryIsoId = bdryPondElem.isoId
        val nbrBdryIsoIds = isoInfo.getIsoNbrsWithExclusion(bdryIsoId, coreIsoIds)
        nbrBdryIsoIds.foreach(nbrBdryIsoId => {
          val nbrBdryIso = isoInfo.isoMap(nbrBdryIsoId)
          val nbrBdryIsShore: Boolean = nbrBdryIso.isShore
          val nbrBdryMinMaxWallHeight: Height = math.max(bdryPondElem.minMaxPathHeight, nbrBdryIso.height)
          // TODO: Handle case in which there is another nbrBdryIso with the same ID and a lower value of minMaxPathHeight.
          //   - In other words, where another different path gets you to the same endpoint, wuth with a lower maxPathHeight.
          // TODO: Handle case in which nbrBdryIsShore
          // if (nbrBdryIsShore) { ... }
          if (!nbrBdryPondElemMap.contains(nbrBdryIsoId)) { nbrBdryPondElemMap(nbrBdryIsoId) = new ArrayBuffer[PondElem] }
          nbrBdryPondElemMap(nbrBdryIsoId).append(new PondElem(nbrBdryIsoId, nbrBdryIsShore, nbrBdryMinMaxWallHeight))
        })
      })

      //
      // Move the boundary Isoclines to the interior, and move the newest Isoclines to the boundary.
      //
      search.corePondElems ++= search.bdryPondElems
      search.bdryPondElems.clear()
      nbrBdryPondElemMap.keys.foreach(nbrBdryIsoId => {
        nbrBdryPondElemMap(nbrBdryIsoId).foreach(pondElem => {
          // TODO: Move this test into the body of the above loop
          if (!search.coreAndBdryIsoIds().contains(pondElem.isoId)) {
            search.bdryPondElems.append(pondElem)
          }
        })
      })

      // TODO: Finalize Pond Isoclines and determine wall height
      //
      // Debugging info
      //
      val finalElemCount = search.elemCount
      println("INFO: %s: Work in progress: End of loop: Elems: %d = %d + %d".format(
        name, search.elemCount(), search.corePondElems.size, search.bdryPondElems.size)
      )
      assert(finalElemCount <= isoInfo.xSize * isoInfo.ySize)
      if (search.bdryPondElems.isEmpty || finalElemCount <= initialElemCount) {
        search.pondIsoIds = coreIsoIds  // TODO: Replace with correct IsoIds
        search.isPondComplete = true
      }
    }

    // val pond = new Pond(pondId, isoclines, wallHeight)
    // pond
    dummyPond
  }

  /** Use minima as starting point to find Ponds from local minima.
    *
    * @param isoInfo    Isoclines (level sets), with their heights and adjacency.
    * @param minimumIds Local minima---each pond has at least one.
    * @return Collection of Ponds in Histo.
    *
    *         Strategy: Find the Pond contained by each minimum by searching along the graph of neighboring Isoclines
    *         until either the entire grid has been searched, or a connection to the shore has been found.
    *         Such a connection bounds the maximum height of the Pond, which allows limiting of the search.
    */
  def mkPondMapFromLocalMinimumIds(name: String, isoInfo: IsoclineInfo, minimumIds: ArrayBuffer[IsoclineId]): PondMap = {
    val pondMap: PondMap = mutable.HashMap()
    val candidateIds: mutable.Stack[IsoclineId] = new mutable.Stack
    candidateIds.pushAll(minimumIds)  // Mutable copy of minimumIds
    while (candidateIds.nonEmpty) {
      val candidateId: IsoclineId = candidateIds.pop
      val nextPond = mkPondFromMinimumId(name, isoInfo, candidateId)
      // candidateIds = candidateIds.filterNot(id => nextPond.containsIsoId(id))
      pondMap(candidateId) = nextPond
    }
    pondMap
  }
}
