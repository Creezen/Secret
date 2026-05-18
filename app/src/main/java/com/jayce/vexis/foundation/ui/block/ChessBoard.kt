package com.jayce.vexis.foundation.ui.block

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.creezen.tool.AndroidTool.toast
import com.jayce.vexis.foundation.ability.socket.LanManager.Companion.TYPE_SERVER

class ChessBoard(context: Context, attr: AttributeSet) : View(context, attr) {

    private val paint by lazy { Paint() }
    private val chessPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val positionArray by lazy {
        Array(16) { Array(16) { 0 } }
    }
    private var cellWidth: Int = 0
    private var xMargin: Float = 0f
    private var yMargin: Float = 0f
    private var isInit: Boolean = false
    private var previewPosition: Pair<Int, Int> = -1 to -1

    private var onStonePlace: ((Int, Int) -> Unit)? = null

    fun setOnStonePlace(placeStone: (Int, Int) -> Unit) {
        onStonePlace = placeStone
    }

    fun placeStone(x: Int, y: Int, type: Int) {
        positionArray[x][y] = if (type == TYPE_SERVER) 1 else 2
        previewPosition = x to y
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBoard(canvas)
        when (isInit) {
            true -> drawChess(canvas)
            false -> isInit = true
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isInit) return false
        if (event == null) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                makeMove(event)
            }
        }
        return true
    }

    private fun drawBoard(canvas: Canvas) {
        val minWidth = if (width > height) height else width
        cellWidth = minWidth / 15
        val realWidth = cellWidth * 15
        xMargin = (width - realWidth) / 2.0f
        yMargin = (height - realWidth) / 2.0f
        for (i in 0..15) {
            canvas.drawLine(xMargin, yMargin + cellWidth * i, width - xMargin, yMargin + cellWidth * i, paint)
            canvas.drawLine(xMargin + cellWidth * i, yMargin, xMargin + cellWidth * i, height - yMargin, paint)
        }
    }

    private fun makeMove(event: MotionEvent) {
        val boardX = event.x - xMargin
        val boardY = event.y - yMargin
        var xCell = (boardX / cellWidth).toInt()
        var yCell = (boardY / cellWidth).toInt()
        if ((boardX % cellWidth) > (cellWidth / 2.0f)) xCell += 1
        if ((boardY % cellWidth) > (cellWidth / 2.0f)) yCell += 1
        if (xCell < 0 || xCell > 15 || yCell < 0 || yCell > 15) return
        if (positionArray[xCell][yCell] > 0) return
        onStonePlace?.invoke(xCell, yCell)
        invalidate()
    }

    private fun drawChess(canvas: Canvas) {
        positionArray.forEachIndexed index1@{ xIndex, rowArray ->
            rowArray.forEachIndexed index2@{ yIndex, column ->
                if (column <= 0) return@index2
                drawChessPiece(canvas, xIndex, yIndex, column < 2)
            }
        }
        if (previewPosition.first >= 0) {
            checkIsFinish()
        }
    }

    private fun drawChessPiece(canvas: Canvas, xCell: Int, yCell: Int, isBlackPlayer: Boolean) {
        val chessWidth = cellWidth * 0.45f
        chessPaint.setShadowLayer(chessWidth, 3f, 3f, Color.argb(100, 0, 0, 0))
        val x = xMargin + xCell * cellWidth
        val y = yMargin + yCell * cellWidth
        val lightX = x - chessWidth * 0.2f
        val lightY = y - chessWidth * 0.2f
        val gradientColor = if (isBlackPlayer) {
            intArrayOf(Color.rgb(200, 200, 200), Color.BLACK, Color.DKGRAY)
        } else {
            intArrayOf(Color.WHITE, Color.LTGRAY, Color.GRAY)
        }
        val gradient = RadialGradient(
            lightX,
            lightY,
            chessWidth,
            gradientColor,
            floatArrayOf(0f, 0.6f, 1f),
            Shader.TileMode.CLAMP
        )
        chessPaint.setShader(gradient)
        canvas.drawCircle(x, y, chessWidth, chessPaint)
        chessPaint.setShader(null)
    }

    private fun checkIsFinish() {
        var lastHorizonType = 0
        var lastVertialType = 0
        var horizonCount = 0
        var vertialCount = 0
        (0..14).forEach {
            val x = positionArray[previewPosition.first][it]
            if (x == lastHorizonType && lastHorizonType > 0) horizonCount++
            else horizonCount = 1
            if (horizonCount >= 5) {
                "game over".toast()
                return
            }
            lastHorizonType = x
        }

        (0..14).forEach {
            val y = positionArray[it][previewPosition.second]
            if (y == lastVertialType && lastVertialType > 0) vertialCount++
            else vertialCount = 1
            if (vertialCount >= 5) {
                "game over".toast()
                return
            }
            lastVertialType = y
        }
    }
}