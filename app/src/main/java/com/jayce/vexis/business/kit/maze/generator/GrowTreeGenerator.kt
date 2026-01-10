package com.jayce.vexis.business.kit.maze.generator

import android.util.Log
import com.jayce.vexis.business.kit.maze.enums.MazeDirection
import com.jayce.vexis.foundation.bean.GridUnit
import kotlin.random.Random

class GrowTreeGenerator : IMazeGenerator() {

    private val activeList: ArrayList<GridUnit> = arrayListOf()

    override fun generateMaze(x: Int, y: Int) {
        val randomX =  Random.nextInt(0, row)
        val randomY = Random.nextInt(0, line)
        activeList.add(mazeMatrix[randomX][randomY])
        while (true) {
            val factor = Random.nextDouble(0.0, 1.0)

            val index = if (factor > 0.4) {
                activeList.size - 1
            } else {
                if (activeList.size == 1) {
                    0
                } else {
                    Random.nextInt(0, activeList.size - 1)
                }
            }
            val selectUnit = activeList[index]
            val neighborUnits = getNeighborUnit(selectUnit)
            if (neighborUnits.isNotEmpty()) {
                val selectNeighborIndex = Random.nextInt(0, neighborUnits.size)
                val selectNeighbor = neighborUnits[selectNeighborIndex]
                selectNeighbor.first.visit = true
                removeWall(selectUnit, selectNeighbor)
                activeList.add(selectNeighbor.first)
            } else {
                activeList.removeAt(index)
            }
            if (activeList.isEmpty()) return
        }
    }

    private fun getNeighborUnit(unit: GridUnit): List<Pair<GridUnit, MazeDirection>> {
        val neighborList = arrayListOf<Pair<GridUnit, MazeDirection>>()
        val unitX = unit.x
        val unitY = unit.y
        if (isValidGrid(unitX - 1, unitY)) {
            neighborList.add(mazeMatrix[unitX - 1][unitY] to MazeDirection.TOP)
        }
        if (isValidGrid(unitX, unitY + 1)) {
            neighborList.add(mazeMatrix[unitX][unitY + 1] to MazeDirection.RIGHT)
        }
        if (isValidGrid(unitX + 1, unitY)) {
            neighborList.add(mazeMatrix[unitX + 1][unitY] to MazeDirection.BOTTOM)
        }
        if (isValidGrid(unitX, unitY - 1)) {
            neighborList.add(mazeMatrix[unitX][unitY - 1] to MazeDirection.LEFT)
        }
        return neighborList
    }

    private fun removeWall(selectUnit: GridUnit, neighborPair: Pair<GridUnit, MazeDirection>) {
        val neighborUnit = neighborPair.first
        when (neighborPair.second) {
            MazeDirection.LEFT -> {
                neighborUnit.hasRight = false
                selectUnit.hasLeft = false
            }
            MazeDirection.RIGHT -> {
                neighborUnit.hasLeft = false
                selectUnit.hasRight = false
            }
            MazeDirection.TOP -> {
                neighborUnit.hasBottom = false
                selectUnit.hasTop = false
            }
            MazeDirection.BOTTOM -> {
                neighborUnit.hasTop = false
                selectUnit.hasBottom = false
            }
        }
    }
}