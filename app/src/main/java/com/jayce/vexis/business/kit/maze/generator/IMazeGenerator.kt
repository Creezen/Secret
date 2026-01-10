package com.jayce.vexis.business.kit.maze.generator

import android.util.Log
import com.jayce.vexis.foundation.bean.GridUnit

abstract class IMazeGenerator {

    var row: Int = -1
    var line: Int = -1
    lateinit var mazeMatrix: Array<Array<GridUnit>>

    fun init(rowSize: Int, lineSize: Int) {
        this.row = rowSize
        this.line = lineSize
        initMatrix()
        generateMaze(0, 0)
    }

    private fun initMatrix(): Array<Array<GridUnit>> {
        mazeMatrix = Array(row) { Array(line) { GridUnit() } }
        for (i in 0 until row) {
            for (j in 0 until line) {
                mazeMatrix[i][j].x = i
                mazeMatrix[i][j].y = j
            }
        }
        mazeMatrix[0][0].hasLeft = false
        mazeMatrix[row - 1][line - 1].hasRight = false
        return mazeMatrix
    }

    fun isValidGrid(x: Int, y: Int, ): Boolean {
        return isCoordinateValid(x, y) && !mazeMatrix[x][y].visit
    }

    fun isCoordinateValid(x: Int, y: Int): Boolean {
        return (x >= 0 && y >= 0 && x < row && y < line)
    }

    abstract fun generateMaze(x: Int, y: Int)
}