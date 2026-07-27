package com.jayce.vexis.business.kit.maze

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.jayce.vexis.business.kit.maze.generator.IMazeGenerator
import com.jayce.vexis.client.TLog
import com.jayce.vexis.domain.bean.GridUnit
import com.jayce.vexis.domain.enums.MazeType
import kotlin.math.abs

class MazeManager(context: Context) : SensorEventListener {

    companion object {
        private const val CLIP_PAD = 0.6f
        private const val DRAW_PAINT_PAD = 0.5f
        private const val DRAW_PAINT_PLAYER_PAD = 1.2f
    }

    private var positionX = 0
    private var positionY = 0
    private var lastX = 0
    private var lastY = 0
    private var hasStepFinish = false
    private var accValue = FloatArray(3)
    private var fieldValue = FloatArray(3)

    private val manager = context.getSystemService(SensorManager::class.java)
    private val sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val fieldSensor = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private var mazeMoveListener: MazeMoveListener? = null

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

    fun startSensorController(listener: MazeMoveListener) {
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        manager.registerListener(this, fieldSensor, SensorManager.SENSOR_DELAY_NORMAL)
        mazeMoveListener = listener
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

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> accValue = event.values
            Sensor.TYPE_MAGNETIC_FIELD -> fieldValue = event.values
        }
        calculateOritation()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { /**/ }

    private fun calculateOritation() {
        val destValue = FloatArray(3)
        val R = FloatArray(9)
        // 根据加速度传感器和磁场传感器的值，获取未处理的方向信息R
        SensorManager.getRotationMatrix(R, null, accValue, fieldValue)
        /**
         * 处理R值，存给destValue，然后可以取destValue的值，转换为方向角
         * destValue[0]：获取方位值，也即东南西北的值  北0  东90 南180 西270（-90）
         * destValue[1]：手机前后翻转的值（相对于屏幕就是上下），水平状态下是0，往前翻（手机上面往下压，下面往上翘）值从0到90，往后翻从0到-90
         * destValue[2]：手机左右翻转的值，往左翻0到-90，往右翻0到90
         */
        SensorManager.getOrientation(R, destValue)
        val oritation = Math.toDegrees(destValue[0].toDouble())
        val upDown = Math.toDegrees(destValue[1].toDouble())
        val leftRight = Math.toDegrees(destValue[2].toDouble())
        val alpha = 5
        if (abs(upDown) < alpha && abs(leftRight) < alpha) {
           if (hasStepFinish) hasStepFinish = false
            return
        }
        if (hasStepFinish) return
        if (abs(upDown) > abs(leftRight)) {
            if (upDown > 0) mazeMoveListener?.onUp()
            else mazeMoveListener?.onDown()
        } else {
            if (leftRight < 0) mazeMoveListener?.onLeft()
            else mazeMoveListener?.onRight()
        }
        hasStepFinish = true
    }
}