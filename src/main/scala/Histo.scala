package HistogramWaterVolume

import Util._

import scala.io.Source

// ========================================
// Histo(gram)
// ========================================
class Histo(val xSize: Int, val ySize: Int) {
  var cells: Array[HCell] = new Array[HCell](xSize * ySize)
  for (y <- 0 until ySize) {
    for (x <- 0 until xSize) {
      cells(y * xSize + x) = new HCell((x, y), -1)
    }
  }

  def getHeight(x: Int, y: Int): Height = {
    lazy val funcName = "getHeight"
    require(x >= 0 && x < xSize, "Histo(): x is out of range")
    require(y >= 0 && y < ySize, "Histo(): y is out of range")
    assertWrapper(cells != null, funcName, "Histo: getHeight: Collection of cells should be initialized")
    cells(y * xSize + x).height
  }

  def initFromFunction(f: (Int, Int) => Int): Unit = {
    for (y <- 0 until ySize) {
      for (x <- 0 until xSize) {
        setHeight(x, y, f(x, y))
      }
    }
  }

  def initFromRandom(min: Int, max: Int): Unit = {
    val r = scala.util.Random
    for (y <- 0 until ySize) {
      for (x <- 0 until xSize) {
        setHeight(xSize, ySize, min + r.nextInt(max - min + 1))
      }
    }
  }

  def setHeight(x: Int, y: Int, h: Height): Unit = {
    lazy val funcName = "setHeight"
    require(x >= 0 && x < xSize, "Histo(): x is out of range")
    require(y >= 0 && y < ySize, "Histo(): y is out of range")
    assertWrapper(cells != null, funcName, "Histo: setHeight: Collection of cells should be initialized")
    val cell = cells(y * xSize + x)
    cell.height = h
  }
}

object Histo {
  /** Read histogram configuration from a file, and make a Histo class using the information in that file.
    *
    * @param filename The first line of "filename" (disregarding comment lines, which begin with a '#' character)
    *                 contains the x and y sizes of the histogram grid, separated by a comma.
    *                 The remaining lines (disregarding comment lines) contain the height of the histogram cells,
    *                 delimited by commas.
    * @return A Histo class, which contains the histogram's height as a function of x and y.
    */
  def mkHistoFromResourceFile(filename: String): Histo = {
    lazy val funcName = "mkHistoFromResourceFile"
    val resourceDir: String = "src/test/resources/"
    val filePath: String = resourceDir + filename

    def parseSizeLine(sizeLine: String): (Int, Int) = {
      lazy val funcName = "parseSizeLine"
      val sizes: Array[Int] = sizeLine.split(",").map(_.toInt)
      assertWrapper(sizes.length == 2, funcName
        , "Input file's size line should have two components.  Found %d".format(sizes.length)
      )
      (sizes(0), sizes(1))
    }
    def parseHeightLine(histo: Histo, lineNum: Int, heightLine: String): Unit = {
      lazy val funcName = "parseHeightLine"
      val xSize = histo.xSize
      val ySize = histo.ySize
      assertWrapper(lineNum < ySize, funcName
        , "%s: Number of height-specifying lines (%d) exceeds expected count (%d)"
          .format(funcName, lineNum, ySize)
      )
      val heights: Array[Int] = heightLine.split(",").map(_.toInt)
      assertWrapper(heights.length == xSize, funcName
        , "Number of height-specifying rows (%d) does not match expected count (%d)"
          .format(heights.length, xSize)
      )
      val indexedHeights: List[(Int, Int)] = heights.view.zipWithIndex.toList
      val f: (Int, Int) => Unit =
        (height: Int, xIndex: Int) => histo.setHeight(xIndex, lineNum, height)
      indexedHeights.foreach[Unit](
        (heightIndex: (Int, Int)) => f(heightIndex._1, heightIndex._2)
      )
    }

    // dprintln("DEBUG: ", "\nmkHistoFromResourceFile: Reading file: %s".format(filePath))
    val lines: Iterator[String] = Source.fromFile(filePath, "UTF-8").getLines()
    val isCommentRegEx = """^\\w*#""".r
    def isComment(str: String): Boolean = isCommentRegEx.findFirstIn(str) match {
      case Some(_) => true
      case None => false
    }
    // First line
    val sizeLine: String = lines.filterNot(isComment(_)).next()
    val (xSize, ySize) = parseSizeLine(sizeLine)

    // Remaining lines
    val histo: Histo = new Histo(xSize, ySize)
    var lineNum = 0
    lines.filterNot(isComment(_)).foreach(heightLine => {
      parseHeightLine(histo, lineNum, heightLine)
      lineNum += 1
    })
    assertWrapper(lineNum == ySize, funcName
      , "funcName: Count of height-specifying rows (%d) does not match expected count (%d)".format(lineNum, ySize)
    )
    histo
  }
}
