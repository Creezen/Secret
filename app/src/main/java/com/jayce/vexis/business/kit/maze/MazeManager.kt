package com.jayce.vexis.business.kit.maze

import android.util.Log
import com.jayce.vexis.business.kit.maze.generator.IMazeGenerator
import com.jayce.vexis.business.kit.maze.enums.MazeType
import com.jayce.vexis.foundation.bean.GridUnit

class MazeManager {

    companion object {
        private const val CLIP_PAD = 0.6f
        private const val DRAW_PAINT_PAD = 0.5f
        private const val DRAW_PAINT_PLAYER_PAD = 1.2f
    }

    private var positionX = 0
    private var positionY = 0
    private var lastX = 0
    private var lastY = 0

    val row: Int
        get() = generator?.row ?: -1
    val line: Int
        get() = generator?.line ?: -1
    val matrix: Array<Array<GridUnit>>
        get() = generator?.mazeMatrix ?: arrayOf()

    private var generator: IMazeGenerator? = null

    fun init(rowSize: Int, lineSize: Int, type: MazeType) {
        this.generator = MazeFactory.getGenerator(type)
        generator?.init(rowSize, lineSize)
    }

    fun updateHorizon(x: Int) {
        savePositionStatus()
        positionX += x
    }

    fun updateVertical(y: Int) {
        savePositionStatus()
        positionY += y
    }

    private fun savePositionStatus() {
        lastX = positionX
        lastY = positionY
    }

    fun getPlayerRect(cellWidth: Float, strokeWidth: Float): MazeRect {
        return MazeRect(
            lastY * cellWidth + strokeWidth * CLIP_PAD,
            lastX * cellWidth + strokeWidth * CLIP_PAD,
            (lastY + 1) * cellWidth - strokeWidth * CLIP_PAD,
            (lastX + 1) * cellWidth - strokeWidth * CLIP_PAD
        )
    }

    fun getPlayerReign(cellWidth: Float, paintStrokeWidth: Float, playerStrokeWidth: Float): MazeRect {
        val paintWidth = paintStrokeWidth * DRAW_PAINT_PAD
        val playerWidth = playerStrokeWidth * DRAW_PAINT_PLAYER_PAD
        return MazeRect(
            positionY * cellWidth + paintWidth + playerWidth,
            positionX * cellWidth + paintWidth + playerWidth,
            (positionY + 1) * cellWidth - paintWidth - playerWidth,
            (positionX + 1) * cellWidth - paintWidth - playerWidth
        )
    }

    fun isAtDestination(): Boolean {
        return positionX == row - 1 && positionY == line - 1
    }

    fun hitLeftWall(horizon: Float): Boolean {
        return matrix[positionX][positionY].hasLeft && horizon < 0
    }

    fun hitRightWall(horizon: Float): Boolean {
        return matrix[positionX][positionY].hasRight && horizon > 0
    }

    fun hitTopWall(vertical: Float): Boolean {
        return matrix[positionX][positionY].hasTop && vertical < 0
    }

    fun hitBottomWall(vertical: Float): Boolean {
        return matrix[positionX][positionY].hasBottom && vertical > 0
    }

}