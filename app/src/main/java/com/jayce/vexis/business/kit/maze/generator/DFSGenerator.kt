package com.jayce.vexis.business.kit.maze.generator

import com.jayce.vexis.foundation.bean.GridUnit
import com.jayce.vexis.foundation.bean.PointEntry
import java.util.ArrayList
import java.util.Stack

class DFSGenerator : IMazeGenerator() {

    private val visitStack = Stack<GridUnit>()

    override fun generateMaze(x: Int, y: Int) {
        if (!mazeMatrix[x][y].visit) {
            visitStack.push(mazeMatrix[x][y])
            mazeMatrix[x][y].visit = true
        }
        val validGrid = arrayListOf<PointEntry>()
        setVisitableGrid(validGrid, x, y)
        if (validGrid.isEmpty()) {
            if (visitStack.isEmpty()) {
                return
            }
            val grid = visitStack.pop()
            generateMaze(grid.x, grid.y)
            return
        }
        validGrid.shuffle()
        val nextVisit = validGrid[0]
        makeWall(x, y, nextVisit)
        generateMaze(x + nextVisit.x, y + nextVisit.y)
    }

    private fun setVisitableGrid(validGrid: ArrayList<PointEntry>, x: Int, y: Int) {
        if (isValidGrid(x - 1, y)) {
            validGrid.add(PointEntry(-1, 0))
        }
        if (isValidGrid(x, y + 1)) {
            validGrid.add(PointEntry(0, 1))
        }
        if (isValidGrid(x + 1, y)) {
            validGrid.add(PointEntry(1, 0))
        }
        if (isValidGrid(x, y - 1)) {
            validGrid.add(PointEntry(0, -1))
        }
    }

    private fun makeWall(x: Int, y: Int, pointEntry: PointEntry) {
        if (pointEntry.x == -1 && pointEntry.y == 0) {
            mazeMatrix[x][y].hasTop = false
            mazeMatrix[x - 1][y].hasBottom = false
            return
        }
        if (pointEntry.x == 0 && pointEntry.y == 1) {
            mazeMatrix[x][y].hasRight = false
            mazeMatrix[x][y + 1].hasLeft = false
            return
        }
        if (pointEntry.x == 1 && pointEntry.y == 0) {
            mazeMatrix[x][y].hasBottom = false
            mazeMatrix[x + 1][y].hasTop = false
            return
        }
        if (pointEntry.x == 0 && pointEntry.y == -1) {
            mazeMatrix[x][y].hasLeft = false
            mazeMatrix[x][y - 1].hasRight = false
        }
    }

}