package com.jayce.vexis.business.kit.maze

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.jayce.vexis.business.kit.maze.enums.MazeType
import com.jayce.vexis.foundation.bean.GridUnit
import kotlin.math.absoluteValue

class MazeView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val paint = Paint()
    private val playerPaint = Paint()
    private var bitmap: Bitmap? = null
    private val bitmapCanvas: Canvas = Canvas()
    private var initStatus = 0

    private var gridWidth = -1f
    private var downX = -1f
    private var downY = -1f
    private var statusCallback: MazeStatusCallback? = null

    private val manager = MazeManager()

    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 1f
        playerPaint.strokeWidth = 3f
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (initStatus == 0) {
            bitmapCanvas.setBitmap(bitmap)
            return
        }
        if (initStatus == 1) {
            drawMazeMap()
            val mRect = manager.getPlayerReign(gridWidth, paint.strokeWidth, playerPaint.strokeWidth)
            bitmapCanvas.drawRect(mRect.left, mRect.top, mRect.right, mRect.bottom, playerPaint)
        }
        bitmap?.apply {
            canvas.drawBitmap(this, 0f, 0f, paint)
        }
    }

    private fun drawMazeMap() {
        val row = height / gridWidth.toInt()
        val line = width / gridWidth.toInt()
        val remainX = width - line * gridWidth
        val remainY = height - row * gridWidth
        bitmapCanvas.translate(remainX * 0.5f, remainY * 0.5f)
        manager.init(row, line, MazeType.GROW_TREE)
        manager.matrix.forEach { mazeLine ->
            mazeLine.forEach {
                drawLine(bitmapCanvas, it)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return true
        initStatus = 2
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val horizon = event.x - downX
                val vertical = event.y - downY
                val absX = horizon.absoluteValue
                val absY = vertical.absoluteValue
                handleClickEvent(absX, absY, horizon, vertical)
                invalidate()
            }
        }
        return true
    }

    private fun handleClickEvent(absX: Float, absY: Float, horizon: Float, vertical: Float) {
        if (absX > absY) {
            if (manager.hitRightWall(horizon) || manager.hitLeftWall(horizon)) {
                statusCallback?.onError()
                return
            }
            manager.updateVertical((horizon / absX).toInt())
        } else {
            if (manager.hitBottomWall(vertical) || manager.hitTopWall(vertical)) {
                statusCallback?.onError()
                return
            }
            manager.updateHorizon((vertical / absY).toInt())
        }
        if (manager.isAtDestination()) {
            statusCallback?.onFinish()
        } else {
            statusCallback?.onMove()
        }
        handlePlayer()
    }

    private fun handlePlayer() {
        bitmapCanvas.save()
        val rect = manager.getPlayerRect(gridWidth, paint.strokeWidth)
        bitmapCanvas.clipRect(rect.left, rect.top, rect.right, rect.bottom)
        bitmapCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
        bitmapCanvas.restore()
        paint.color = Color.RED
        val mRect = manager.getPlayerReign(gridWidth, paint.strokeWidth, playerPaint.strokeWidth)
        bitmapCanvas.drawRect(mRect.left, mRect.top, mRect.right, mRect.bottom, playerPaint)
    }

    private fun drawLine(canvas: Canvas, item: GridUnit) {
        item.apply {
            val x0 = x * gridWidth
            val x1 = (x + 1) * gridWidth
            val y0 = y * gridWidth
            val y1 = (y + 1) * gridWidth
            if (hasTop) canvas.drawLine(y0, x0, y1, x0, paint)
            if (hasRight) canvas.drawLine(y1, x0, y1, x1, paint)
            if (hasBottom) canvas.drawLine(y0, x1, y1, x1, paint)
            if (hasLeft) canvas.drawLine(y0, x0, y0, x1, paint)
        }
    }

    fun setRowAndLine(width: Float) {
        this.gridWidth = width
        initStatus = 1
    }

    fun registerCallback(callback: MazeStatusCallback) {
        this.statusCallback = callback
    }
}