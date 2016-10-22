package HistogramWaterVolume

import scala.collection.mutable.ArrayBuffer

import Histo._
import IsoclineInfo._
import Pond._
import PondInfo._
import PondSearch._
import Util._

object TestHistos {
  lazy val soloHisto: Histo                    = mkHistoFromResourceFile("solo.hf3d")
  lazy val soloInfo: IsoclineInfo              = mkIsoclineInfoFromHisto(soloHisto, "solo")
  lazy val soloMinima: ArrayBuffer[IsoclineId] = mkLocalMinimumIdsFromIsoclineInfo(soloInfo)
  lazy val soloPondMap: PondMap                = mkPondMapFromLocalMinimumIds("solo", soloInfo, soloMinima)
  lazy val soloPondInfoMap: PondInfoMap        = mkPondInfoFromPondMap("solo", soloInfo.isoMap, soloPondMap)

  lazy val sameHisto: Histo                    = mkHistoFromResourceFile("same.hf3d")
  lazy val sameInfo: IsoclineInfo              = mkIsoclineInfoFromHisto(sameHisto, "same")
  lazy val sameMinima: ArrayBuffer[IsoclineId] = mkLocalMinimumIdsFromIsoclineInfo(sameInfo)
  lazy val samePondMap: PondMap                = mkPondMapFromLocalMinimumIds("same", sameInfo, sameMinima)
  lazy val samePondInfoMap: PondInfoMap        = mkPondInfoFromPondMap("same", sameInfo.isoMap, samePondMap)

  lazy val diffHisto: Histo                    = mkHistoFromResourceFile("diff.hf3d")
  lazy val diffInfo: IsoclineInfo              = mkIsoclineInfoFromHisto(diffHisto, "diff")
  lazy val diffMinima: ArrayBuffer[IsoclineId] = mkLocalMinimumIdsFromIsoclineInfo(diffInfo)
  lazy val diffPondMap: PondMap                = mkPondMapFromLocalMinimumIds("diff", diffInfo, diffMinima)
  lazy val diffPondInfoMap: PondInfoMap        = mkPondInfoFromPondMap("diff", diffInfo.isoMap, diffPondMap)

  lazy val flatHisto: Histo                    = mkHistoFromResourceFile("flat.hf3d")
  lazy val flatInfo: IsoclineInfo              = mkIsoclineInfoFromHisto(flatHisto, "flat")
  lazy val flatMinima: ArrayBuffer[IsoclineId] = mkLocalMinimumIdsFromIsoclineInfo(flatInfo)
  lazy val flatPondMap: PondMap                = mkPondMapFromLocalMinimumIds("flat", flatInfo, flatMinima)
  lazy val flatPondInfoMap: PondInfoMap        = mkPondInfoFromPondMap("flat", flatInfo.isoMap, flatPondMap)

  lazy val hookHisto: Histo                    = mkHistoFromResourceFile("hook.hf3d")
  lazy val hookInfo: IsoclineInfo              = mkIsoclineInfoFromHisto(hookHisto, "hook")
  lazy val hookMinima: ArrayBuffer[IsoclineId] = mkLocalMinimumIdsFromIsoclineInfo(hookInfo)
  lazy val hookPondMap: PondMap                = mkPondMapFromLocalMinimumIds("hook", hookInfo, hookMinima)
  lazy val hookPondInfoMap: PondInfoMap        = mkPondInfoFromPondMap("hnook", hookInfo.isoMap, hookPondMap)

  lazy val peakHisto: Histo                    = mkHistoFromResourceFile("peak.hf3d")
  lazy val peakInfo: IsoclineInfo              = mkIsoclineInfoFromHisto(peakHisto, "peak")
  lazy val peakMinima: ArrayBuffer[IsoclineId] = mkLocalMinimumIdsFromIsoclineInfo(peakInfo)
  lazy val peakPondMap: PondMap                = mkPondMapFromLocalMinimumIds("peak", peakInfo, peakMinima)
  lazy val peakPondInfoMap: PondInfoMap        = mkPondInfoFromPondMap("peak", peakInfo.isoMap, peakPondMap)

  lazy val fourHisto: Histo                    = mkHistoFromResourceFile("four.hf3d")
  lazy val fourInfo: IsoclineInfo              = mkIsoclineInfoFromHisto(fourHisto, "four")
  lazy val fourMinima: ArrayBuffer[IsoclineId] = mkLocalMinimumIdsFromIsoclineInfo(fourInfo)
  lazy val fourPondMap: PondMap                = mkPondMapFromLocalMinimumIds("four", fourInfo, fourMinima)
  lazy val fourPondInfoMap: PondInfoMap        = mkPondInfoFromPondMap("four", fourInfo.isoMap, fourPondMap)

  lazy val bendHisto: Histo                    = mkHistoFromResourceFile("bend.hf3d")
  lazy val bendInfo: IsoclineInfo              = mkIsoclineInfoFromHisto(bendHisto, "bend")
  lazy val bendMinima: ArrayBuffer[IsoclineId] = mkLocalMinimumIdsFromIsoclineInfo(bendInfo)
  lazy val bendPondMap: PondMap                = mkPondMapFromLocalMinimumIds("bend", bendInfo, bendMinima)
  lazy val bendPondInfoMap: PondInfoMap        = mkPondInfoFromPondMap("bend", bendInfo.isoMap, bendPondMap)

  lazy val eyesHisto: Histo                    = mkHistoFromResourceFile("eyes.hf3d")
  lazy val eyesInfo: IsoclineInfo              = mkIsoclineInfoFromHisto(eyesHisto, "eyes")
  lazy val eyesMinima: ArrayBuffer[IsoclineId] = mkLocalMinimumIdsFromIsoclineInfo(eyesInfo)
  lazy val eyesPondMap: PondMap                = mkPondMapFromLocalMinimumIds("eyes", eyesInfo, eyesMinima)
  lazy val eyesPondInfoMap: PondInfoMap        = mkPondInfoFromPondMap("eyes", eyesInfo.isoMap, eyesPondMap)
}
