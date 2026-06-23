package com.jayce.vexis.business.kit.maze

import com.jayce.vexis.business.kit.maze.generator.IMazeGenerator
import com.jayce.vexis.domain.bean.GridUnit
import com.jayce.vexis.domain.enums.MazeType

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
    var isGameFinish: Boolean = false

    fun init(rowSize: Int, lineSize: Int, type: MazeType) {
        this.generator = MazeFactory.getGenerator(type)
        generator?.init(rowSize, lineSize)
    }

    fun updateHorizon(x: Int): Boolean {
        val newHorizon = positionX + x
        if (newHorizon < 0 || newHorizon >= row) return false
        savePositionStatus()
        positionX = newHorizon
        return true
    }

    fun updateVertical(y: Int): Boolean {
        val newVertical = positionY + y
        if (newVertical < 0 || newVertical >= line) return false
        savePositionStatus()
        positionY = newVertical
        return true
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