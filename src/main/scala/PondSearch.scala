package HistogramWaterVolume

import scala.collection.mutable.ArrayBuffer

import Util._

/** Class to track the info needed to determine which Isocline IDs belong to a given Pond.
  *
  * @param pondId         Same as the IsoclineId of the local minimum Isocline used to find the Pond
  * @param corePondElems  Elements that have already cycled through initial inspection
  * @param bdryPondElems  Elements that are being inspected
  * @param maxWallHeight  An upper limit on how high a wall can be that borders the pond
  * @param pondIsoIds     Isoclines that belong to the Pond (determined just before function returns)
  * @param isPondComplete Have determined which IsoclineIds belong to this Pond
  */
class PondSearch(val pondId: IsoclineId
                 , var corePondElems: ArrayBuffer[PondElem]
                 , var bdryPondElems: ArrayBuffer[PondElem]
                 , var maxWallHeight: Height
                 , var pondIsoIds: ArrayBuffer[IsoclineId]
                 , var isPondComplete: Boolean
                )
{
  def coreAndBdryIsoIds(): ArrayBuffer[IsoclineId] = elems().map(_.isoId)

  def dprintPondSearch(): Unit = {
    // if (isVerbose) {
    dprintln(banner="INFO: "
      ,"PondSearch: pondId=%d, corePondElems=%d, bdryPondElems=%d, pondIsoIds=%d, isPondComplete=%s"
        .format(pondId, corePondElems.size, bdryPondElems.size, pondIsoIds.size, isPondComplete.toString)
    )
    Console.flush()
    // }
  }

  def elems(): ArrayBuffer[PondElem] = { corePondElems union bdryPondElems }

  def elemCount(): Int = corePondElems.size + bdryPondElems.size

  def pondElems2IsoIds(elems: ArrayBuffer[PondElem]): ArrayBuffer[IsoclineId] = {
    val isoIds: ArrayBuffer[IsoclineId] = new ArrayBuffer
    isoIds.appendAll(elems.map(elem => elem.isoId))
    isoIds
  }

  def getNbrBdryPondElems(isoInfo: IsoclineInfo): ArrayBuffer[PondElem] = {
    val coreIds: ArrayBuffer[IsoclineId] = pondElems2IsoIds(corePondElems)
    val bdryIds: ArrayBuffer[IsoclineId] = pondElems2IsoIds(bdryPondElems)
    val exclusionIds = coreIds ++ bdryIds
    val nbrBdryPondElems: ArrayBuffer[PondElem] = bdryPondElems.flatMap(bdryPondElem => {
      val nbrBdryIsos: Array[IsoclineId] = isoInfo.getIsoGroupNbrsWithExclusion(bdryIds, exclusionIds)
      val nbrBdryElems: Array[PondElem] = nbrBdryIsos.map(nbrBdryIsoId => {
        val nbrBdryIso = isoInfo.isoMap(nbrBdryIsoId)
        val nbrBdryIsShore = nbrBdryIso.isShore
        val nbrPathHeight = math.max(bdryPondElem.minMaxPathHeight, isoInfo.isoMap(nbrBdryIsoId).height)
        new PondElem(nbrBdryIsoId, nbrBdryIsShore, nbrPathHeight)
      })
      nbrBdryElems
    })
    nbrBdryPondElems
  }
}

object PondSearch {
  /**
    *
    * @param isoInfo Isoclines (level sets), with their heights and adjacency.
    * @return Collection of Isoclines with upEdges to all neighboring Isoclines.
    */
  def mkLocalMinimumIdsFromIsoclineInfo(isoInfo: IsoclineInfo): ArrayBuffer[IsoclineId] = {
    val isoMap = isoInfo.isoMap
    val isoNbrMap = isoInfo.isoNbrMap
    def allNbrsAreHigher(homeId: IsoclineId): Boolean = {
      val homeHeight: Height = isoMap(homeId).height
      val result = isoNbrMap(homeId).forall(nbrId => {
        isoMap(nbrId).height > homeHeight
      })
      result
    }
    val homeIds: Iterable[IsoclineId] = isoNbrMap.keys
    val minimumIds: ArrayBuffer[IsoclineId] = new ArrayBuffer
    minimumIds.appendAll(homeIds.filter(homeId => !isoMap(homeId).isShore && allNbrsAreHigher(homeId)))
    minimumIds
  }

  // Create PondSearch object
  def mkPondSearchFromMinimumId(name: String, isoInfo: IsoclineInfo, minimumId: IsoclineId): PondSearch = {
    val minimumHeight: Height = isoInfo.isoMap(minimumId).height
    val minimumPondElem: PondElem = new PondElem(minimumId, false, minimumHeight)
    val corePondElems: ArrayBuffer[PondElem] = new ArrayBuffer
    corePondElems.append(minimumPondElem)
    val search: PondSearch = new PondSearch(
      /* pondId: */ minimumId
      , /* corePondElems */ corePondElems
      , /* bdryPondElems */ isoInfo.isoNbrMap(minimumId).map((nbrId: IsoclineId) =>
        new PondElem(
          nbrId
          , isoInfo.isoMap(minimumId).isShore
          , math.max(isoInfo.isoMap(nbrId).height, minimumHeight)
        )
      )
      , /* maxWallHeight */ Int.MaxValue // No known maxWallHeight yet
      , /* pondIsoIds */ new ArrayBuffer
      , isPondComplete = false
    )
    search
  }
}
