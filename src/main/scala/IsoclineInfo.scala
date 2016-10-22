package HistogramWaterVolume

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

import Util._

class IsoclineInfo(var xSize: Int,
                   var ySize: Int,
                   var lookup: IsoclineLookup,
                   var isoMap: IsoclineMap,
                   var isoNbrMap: IsoclineNbrMap) {
  def dprintIsoclineCellsById(): Unit = {
    if (isVerbose) {
      dprintln(banner = "DEBUG", "List of cells by the %d Isocline ID(s):".format(isoMap.keys.size))
      Console.flush()
      isoMap.keys.toList.sorted.foreach(id => {
        print(s"ID# $id: ")
        Console.flush()
        isoMap(id).cells.foreach(cell => print(s"$cell"))
        println("")
        Console.flush()
      })
    }
  }

  def dprintIsoclineInfoReport(name: String, xSize: Int, ySize: Int, header: String) = {
    if (isVerbose) {
      dprintln(banner = "DEBUG", header)
      dprintLookup()
      println("----------")
      dprintIsoclineIdsWithLayout(xSize, ySize, name)
      println("----------")
      dprintIsoclineCellsById()
      println("----------")
      dprintNbrs(name)
      println("----------")
      // val _: Char = Console.in.read.toChar
    }
  }

  def dprintLookup(): Unit = lookup.dprintLookup()

  def dprintNbrIds(header: String, homeId: IsoclineId) = {
    if (isVerbose) {
      dprintln(banner = "INFO: ", header + " (baseIds):")
      isoNbrMap(homeId).foreach { nbrId => print(s"$nbrId  ") }
      println("")
      Console.flush()

      dprintln(banner = "INFO:", header + " (termIds):")
      isoNbrMap(homeId).filter(_ != homeId).foreach(id => print("%s  ".format(lookup.base2Term(id))))
      println("")
      Console.flush()
    }
  }

  def dprintNbrsByHomeId(header: String, homeId: IsoclineId, nbrs: ArrayBuffer[IsoclineId]): Unit = {
    if (isVerbose) {
      dprint(banner = "INFO:", header)
      nbrs.foreach(nbrId => print(s"$nbrId  "))
      println("")
      Console.flush()
    }
  }

  def getAllIsoNbrs(homeIds: ArrayBuffer[IsoclineId]): ArrayBuffer[IsoclineId] = {
    val neighbors: ArrayBuffer[IsoclineId] = new ArrayBuffer
    homeIds.foreach(homeId => {
      neighbors.appendAll(isoNbrMap(homeId))
    })
    val nbrs: ArrayBuffer[IsoclineId] = new ArrayBuffer
    nbrs.appendAll(neighbors.toList.distinct.filterNot(neighborId => homeIds.contains(neighborId)))
    nbrs
  }

  def getCellCount(): Int = {
    var cellCount: Int = 0 // isoMap.keys.map(id => isoMap(id).cells.size).sum
    isoMap.keys.foreach(id => {
      cellCount += isoMap(id).cells.size
    })
    cellCount
  }

  def getIsoCount(): Int = isoMap.size

  /** This double-counts neighboring relationships: once for each neighbor.
    *
    */
  def getNbrCount(): Int = {
    val nbrCount: Int = isoNbrMap.keys.toList.distinct.map(id => isoNbrMap(id).length).sum
    nbrCount
  }

  def getIsoNbrs(isoId: IsoclineId): Array[IsoclineId] = {
    isoNbrMap.keys.toList.distinct.toArray
  }

  def getIsoNbrsWithExclusion(isoId: IsoclineId, exclusion: ArrayBuffer[IsoclineId]): Array[IsoclineId] = {
    getIsoNbrs(isoId).filterNot(id => exclusion.contains(id))
  }

  def getIsoGroupNbrs(isoIds: ArrayBuffer[IsoclineId]): Array[IsoclineId] = {
    val empty: Array[IsoclineId] = new Array(0)
    val result = isoIds.map(id => getIsoNbrs(id)).fold(empty)(_ union _)
    result.toList.distinct.toArray
  }

  def getIsoGroupNbrsWithExclusion(isoIds: ArrayBuffer[IsoclineId], exclusion: ArrayBuffer[IsoclineId]): Array[IsoclineId] = {
    getIsoGroupNbrs(isoIds).filterNot(id => exclusion.contains(id))
  }

  /** Merge two Isoclines into the smaller ID.  Modifies lookup, isoMap, isoNbrMap.
    * - The values field in isoNbrMap is not updated here; that's done later.
    *
    * @param id1 an ID to be merged
    * @param id2 another ID to be merged
    *
    */
  def mergeIsoclines(x1: Int, y1: Int, id1: IsoclineId, x2: Int, y2: Int, id2: IsoclineId): Unit = {
    lazy val funcName = "mergeIsoclines"
    assertWrapper(lookup.size > 0, funcName
      , "Cannot merge Isoclines, because no Isocline IDs are recorded in IsoclineLookup object"
    )
    assertWrapper(isoMap.nonEmpty, funcName
      , "Cannot merge Isoclines, because no Isocline IDs are recorded in IsoclineMap object"
    )
    if (id1 == id2) {
      throw new IllegalArgumentException(s"Cannot merge Isoclines with the same ID ($id1)")
    }
    val initialCellCount = isoMap(id1).cells.size + isoMap(id2).cells.size
    val (_, _, oldId, newX, newY, newId) =
      if (id1 < id2) {
        (x1, y1, id1, x2, y2, id2)
      } else {
        (x2, y2, id2, x1, y1, id1)
      }
    // dprintln(banner="INFO:", "mergeIsoclines: Merging Isocline #%d into #%d".format(newId, oldId))

    // Update lookup and delete old keys
    if (isVerbose) {
      dprintln(banner = "INFO:", s"Remapping ID #$newId into ID #$oldId")
    }
    lookup.setBaseVal(newId, oldId)
    lookup.setBaseId(newX, newY, oldId)

    // Merge isoMaps
    mergeIsoMaps(oldId, newId)

    val finalCellCount = isoMap(oldId).cells.size
    assertWrapper(finalCellCount == initialCellCount, funcName
      , "Merged cell count (%d) does not match initial cell count (%d)"
        .format(finalCellCount, initialCellCount)
    )

    mergeIsoNbrMaps(oldId, newId)
  }

  def mergeIsoMaps(oldId: IsoclineId, newId: IsoclineId): Unit = {
    val isoOld: Isocline = isoMap(oldId)
    val isoNew: Isocline = isoMap(newId)
    isoOld.cells ++= isoNew.cells
    isoOld.isShore = isoOld.isShore || isoNew.isShore
    isoMap -= newId
  }

  /**
    * Merge isoNbrMaps
    *
    * isoNbrMap.getOrElse(oldId, ArrayBuffer.empty)?
    * assert(isoNbrMap.contains(oldId))?
    */
  def mergeIsoNbrMaps(oldId: IsoclineId, newId: IsoclineId): Unit = {
    val oldNbrs: ArrayBuffer[IsoclineId] = isoNbrMap(oldId).filter(id => id != newId)
    val newNbrs: ArrayBuffer[IsoclineId] =
      isoNbrMap.getOrElse(newId, ArrayBuffer.empty).filter(nbrId => nbrId != oldId) // No Isocline is its own nbr.
    dprintNbrsByHomeId(
      "Neighbors (unedited) of old ID #%d (count=%d): ".format(oldId, oldNbrs.size)
      , oldId, oldNbrs
    )
    dprintNbrsByHomeId(
      "Neighbors (edited) of new ID #%d (count=%d): ".format(newId, newNbrs.size)
      , newId, newNbrs
    )
    val unionNbrs: List[IsoclineId] = (oldNbrs ++ newNbrs).toList.distinct
    isoNbrMap(oldId) = new ArrayBuffer
    isoNbrMap(oldId).appendAll(unionNbrs)
    isoNbrMap -= newId
    dprintNbrIds(s"Merged neighbor IDs for #$oldId: ", oldId)
  }

  def dprintIsoclineIdsWithLayout(xSize: Int, ySize: Int, name: String): Unit = {
    if (isVerbose) {
      println("")
      dprintln(banner = "INFO:", "List of Isoclines for Histo \"%s\":".format(name))
      for (y <- 0 until ySize) {
        for (x <- 0 until xSize) {
          print("%3d ".format(lookup.termId(x, y)))
        }
        println("")
      }
      Console.flush()
    }
  }

  def dprintNbrs(name: String): Unit = {
    if (isVerbose) {
      dprintln(banner = "INFO:", "List of neighbors for Histo \"%s\":".format(name))
      isoNbrMap.keys.toList.sorted.distinct.foreach(id => {
        if (isoNbrMap(id).nonEmpty) {
          dprint(banner = "INFO:", s"$id: ")
          isoNbrMap(id).foreach(nbrId => {
            print(s"$nbrId  ")
          })
          println("")
        }
      })
      Console.flush()
    }
  }

  def updateIsoNbrMap(): Unit = {
    val oldToNewIdMap: mutable.HashMap[IsoclineId, IsoclineId] = new mutable.HashMap
    (0 until (xSize * ySize)).foreach(id => {
      oldToNewIdMap(id) = lookup.termVal(id)
    })
    def getNewIsoId(oldId: IsoclineId): IsoclineId = oldToNewIdMap(oldId)
    val homeKeys = isoNbrMap.keys
    homeKeys.foreach((homeId: IsoclineId) => {
      val newNbrIds =
        isoNbrMap(homeId)
          .map(nbrId => getNewIsoId(nbrId))
          .distinct
          .filter(id => id != homeId)
      isoNbrMap(homeId).clear
      isoNbrMap(homeId).appendAll(newNbrIds)
    })
  }
}

object IsoclineInfo {
  def dprintIsoNbrMap(isoNbrMap: IsoclineNbrMap): Unit = {
    if (isVerbose) {
      for (id <- isoNbrMap.keys.toList.sorted.distinct) {
        print(s"$id => ")
        isoNbrMap(id).foreach(nbrId => print(s"$nbrId  "))
        println("")
        Console.flush()
      }
    }
  }

  def insertNbrMapLink(map: IsoclineNbrMap, home: IsoclineId, nbr: IsoclineId): Unit = {
    if (!map.contains(home)) {
      map(home) = new ArrayBuffer[IsoclineId]
    }
    map(home).append(nbr)
  }

  def insertNbrMapLinkPair(map: IsoclineNbrMap, id1: IsoclineId, id2: IsoclineId): Unit = {
    insertNbrMapLink(map, id1, id2)
    insertNbrMapLink(map, id2, id1)
  }

  def isoNbrMapTest(xSize: Int, ySize: Int, isoNbrMap: IsoclineNbrMap): Unit = {
    lazy val funcName = "isoNbrMapTest"
    // isoNbrMap home ID count check
    val homeIdCount: Int = isoNbrMap.size
    val expectedHomeIdCount: Int =
      if (xSize <= 1 && ySize <= 1) 0
      else xSize * ySize
    assertWrapper(homeIdCount == expectedHomeIdCount, funcName
      , "Observed home ID count (%d) does not match expected count (%d)"
        .format(homeIdCount, expectedHomeIdCount)
    )

    // isoNbrMap nbr ID count check
    var nbrCount: Int = 0 // isoNbrMap.keys.map(id => isoNbrMap(id).length).sum
    isoNbrMap.keys.foreach(id => {
      nbrCount += isoNbrMap(id).size
    })
    val expectedNbrCount = 2 * (2 * xSize * ySize - xSize - ySize)
    // dprintIsoNbrMap(isoNbrMap)
    assertWrapper(nbrCount == expectedNbrCount, funcName
      , "Observed neighbor ID count (%d) does not match expected count (%d)"
        .format(nbrCount, expectedNbrCount)
    )
  }

  /** Iterate over Histo, accumulating a collection of Isoclines and adjacencies.
    *
    * @param histo Histogram
    * @return Isoclines and their adjacency to each other.
    *
    *         Isocline IDs start at 0 and increment by 1.
    *         Isocline cells are merged greedily (cf. quick-union) and referenced by the larger ID.
    */
  def mkIsoclineInfoFromHisto(histo: Histo, name: String): IsoclineInfo = {
    val funcName = "getIsoclineInfoFromHisto"
    val xSize = histo.xSize
    val ySize = histo.ySize

    // ========================================
    // Initialize lookup
    // ========================================
    val lookup: IsoclineLookup = new IsoclineLookup(xSize, ySize)

    // ========================================
    // Initialize isoMap
    // ========================================
    val isoMap: IsoclineMap = new IsoclineMap
    for (y <- 0 until ySize) {
      for (x <- 0 until xSize) {
        val isShore = y == 0 || y == ySize - 1 || x == 0 || x == xSize - 1
        val cells: ArrayBuffer[(Int, Int)] = new ArrayBuffer
        cells.append((x, y))
        isoMap(y * xSize + x) = new Isocline(y * xSize + x, cells, histo.getHeight(x, y), isShore)
      }
    }
    // dprintln(banner="INFO:", "Dimensions: xSize=%d, ySize=%d".format(xSize, ySize))

    // ========================================
    // Initialize isoNbrMap
    // ========================================
    val isoNbrMap: IsoclineNbrMap = mkInitialIsoclineNbrMapFromLookup(xSize, ySize, lookup)

    // ========================================
    // Initialize isoInfo
    // ========================================
    val isoInfo: IsoclineInfo = new IsoclineInfo(xSize, ySize, lookup, isoMap, isoNbrMap)
    if (isVerbose) {
      dprintln(banner = "INFO:", "Cell count: expected=%d; observed=%d".format(xSize * ySize, isoInfo.getCellCount()))
    }
    assertWrapper(isoInfo.getCellCount() == xSize * ySize, funcName
      , "Observed inital cell count (%d) does not match expected value (%d)"
        .format(isoInfo.getCellCount(), xSize * ySize)
    )

    if (isVerbose) {
      dprintln(banner = "INFO:", s"===== Initial IsoclineInfo: $name =====")
    }
    isoInfo.dprintIsoclineIdsWithLayout(xSize, ySize, name)
    isoInfo.dprintNbrs(name)

    // ========================================
    // Iterate over Histo to merge Isoclines
    // Along the way, accumulate IsoclineInfo = IsoclineMap (collection of Isoclines) + IsoclineNbrMap (adjacencies).
    // ========================================
    for (y <- 0 until ySize) {
      for (x <- 0 until xSize) {
        val h = histo.getHeight(x, y)
        lazy val upH = histo.getHeight(x, y - 1)
        lazy val leftH = histo.getHeight(x - 1, y)
        var id = lookup.termId(x, y)
        lazy val upId = lookup.termId(x, y - 1)
        lazy val leftId = lookup.termId(x - 1, y)

        if (y > 0 && id != upId && h == upH) {
          isoInfo.mergeIsoclines(x, y - 1, upId, x, y, id)
          id = isoInfo.lookup.termId(x, y) // Update id after merge
        }
        if (x > 0 && id != leftId && h == leftH) {
          isoInfo.mergeIsoclines(x - 1, y, leftId, x, y, id)
        }
        isoInfo.dprintIsoclineInfoReport(
          name, xSize, ySize
          , "===== IsoclineInfo: \"%s\" @ (%d, %d) =====".format(name, x, y)
        )
      }
    }

    // ========================================
    // Canonicalize (update isoNbrMap)
    // Re-number Isocline IDs via IsoclineLookup to account for early lazy neighbor ID recording a la quick-union.
    // ========================================
    isoInfo.updateIsoNbrMap()
    assertWrapper(isoInfo.getNbrCount < 4 * isoInfo.getCellCount(), funcName
      , "Isocline neighbor count (%d) higher than upper bound (%d)"
        .format(isoInfo.getNbrCount(), 4 * isoInfo.getCellCount())
    )

    // ASSERT that isoMap and isoNbrMap share the same set of keys
    if (isoMap.keys.size > 1) {
      val mapIds = isoMap.keys.toSet
      val mapNbrIds = isoNbrMap.keys.toSet
      val symDiffIds = (mapIds diff mapNbrIds) union (mapNbrIds diff mapIds)
      assertWrapper(symDiffIds.isEmpty, funcName
        , "Set of neighbor Isocline IDs differs from expected set")
    }
    isoInfo
  }

  def mkInitialIsoclineNbrMapFromLookup(xSize: Int, ySize: Int, lookup: IsoclineLookup): IsoclineNbrMap = {
    // Initialize isoNbrMap with every cell linked to its 4-connected neighbors
    val isoNbrMap = new IsoclineNbrMap
    for (y <- 0 until ySize) {
      for (x <- 0 until xSize) {
        val id: Id = lookup.termId(x, y)
        if (y > 0) {
          val upId: Id = lookup.termId(x, y - 1)
          // dprintln(banner="INFO:", "%d => %d && %d => %d".format(id, upId, upId, id))
          insertNbrMapLinkPair(isoNbrMap, id, upId)
        }
        if (x > 0) {
          val leftId = lookup.termId(x - 1, y)
          // dprintln(banner="INFO:", "%d => %d && %d => %d".format(id, leftId, leftId, id))
          insertNbrMapLinkPair(isoNbrMap, id, leftId)
        }
      }
    }
    isoNbrMapTest(xSize, ySize, isoNbrMap)
    isoNbrMap
  }
}
