# HistogramWaterVolume
Determine volume of water that a 3D column histogram can hold.

HistogramWaterVolume

There are several websites that describe a programming challenge: Given a 2D column histogram,  image water being poured over the top of that histogram.
If the histogram is high in the middle and descends toward each edge, then all the water will flow off the histogram, leaving none behind.
However, if there are local minima in the interior of the histogram, then some water will remain.
The height of water at a given location is determined by the maximum column height to the left and to the right.
The challenge is to  find the volume of the remaining water in a computationally efficient manner.

See, for example, [this website](http://stackoverflow.com/questions/24414700/amazon-water-collected-between-towers), which contains a concise Haskell solution, among others.

This repository addresses the analogous case for a 3D column histogram.  The approach, as follows, is:
* Group together 4-connected columns of the same height, into "cells".
* Create a graph whose nodes are these cells, and whose edges reflect the direction that water flows between connected cells.
* Have the term "ponds" refer to the regions where water collects.  Each local minimum lies within a pond, but a given pond can contain one or more minima.
* Determine the ponds and their volumes.

The last two steps have not been completed.

