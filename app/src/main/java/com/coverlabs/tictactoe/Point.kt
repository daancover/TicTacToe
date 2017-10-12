package com.coverlabs.tictactoe

/**
 * Created by Daniel on 12/10/2017.
 */

class Point(var x: Int, var y: Int) {
    init {
        this.x = x
        this.y = y
    }

    companion object {
        private val pointHashMap = HashMap<Int, Point>()

        fun getIndexByPoint(point: Point): Int {
            if (pointHashMap.size != 9) {
                pointHashMap.put(0, Point(0, 0))
                pointHashMap.put(1, Point(0, 1))
                pointHashMap.put(2, Point(0, 2))
                pointHashMap.put(3, Point(1, 0))
                pointHashMap.put(4, Point(1, 1))
                pointHashMap.put(5, Point(1, 2))
                pointHashMap.put(6, Point(2, 0))
                pointHashMap.put(7, Point(2, 1))
                pointHashMap.put(8, Point(2, 2))
            }

            for (i in 0..pointHashMap.size - 1) {
                val hashPoint = pointHashMap.get(i)

                if (hashPoint != null) {
                    if (hashPoint.x == point.x && hashPoint.y == point.y) {
                        return i
                    }
                }
            }

            return -1
        }

        fun get(index: Int): Point {
            if (pointHashMap.size != 9) {
                pointHashMap.put(0, Point(0, 0))
                pointHashMap.put(1, Point(0, 1))
                pointHashMap.put(2, Point(0, 2))
                pointHashMap.put(3, Point(1, 0))
                pointHashMap.put(4, Point(1, 1))
                pointHashMap.put(5, Point(1, 2))
                pointHashMap.put(6, Point(2, 0))
                pointHashMap.put(7, Point(2, 1))
                pointHashMap.put(8, Point(2, 2))
            }

            return pointHashMap.get(index)!!
        }
    }
}