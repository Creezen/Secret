package com.jayce.vexis.foundation.view.block

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.creezen.tool.DataTool.calculateMultiPointDistance
import kotlin.math.abs
import kotlin.math.absoluteValue

class PaintView(
    context: Context,
    attributeSet: AttributeSet,
) : View(context, attributeSet) {
    private val paint = Paint()
    private var ballX = 500f
    private var ballY = 500f
    private var previewX = 0f
    private var previewY = 0f
    private var radis = 200f
    private var pointCount = 0
    private var previewDistinct = -1f
    private var previewAction = -1024

    init {
        paint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        paint.color = Color.RED
        canvas.drawCircle(ballX, ballY, radis, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                previewX = event.x
                previewY = event.y
                pointCount++
                previewAction = event.actionMasked
            }
            MotionEvent.ACTION_UP -> {
                pointCount--
                if (pointCount >= 1) {
                    return true
                }
                if (previewAction == MotionEvent.ACTION_POINTER_UP) {
                    return true
                }
                handleClickPoint(event.x, event.y)
                invalidate()
                previewAction = event.actionMasked
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                pointCount++
                if (pointCount >= 2) {
                    previewDistinct = calculateMultiPointDistance(event)
                }
                previewAction = event.actionMasked
            }
            MotionEvent.ACTION_POINTER_UP -> {
                pointCount--
                previewDistinct = -1f
                previewAction = event.actionMasked
            }
            MotionEvent.ACTION_MOVE -> {
                if (pointCount <= 1) {
                    return true
                }
                val distanceNow = calculateMultiPointDistance(event)
                if (abs(previewDistinct - distanceNow) < 50) {
                    return true
                }
                radis *= distanceNow / previewDistinct
                if (radis > 800) radis = 800f
                if (radis < 20) radis = 20f
                previewDistinct = distanceNow
                invalidate()
            }
        }
        return true
    }

    private fun handleClickPoint(
        x: Float,
        y: Float,
    ) {
        val horizon = x - previewX
        val vertical = y - previewY
        val absX = horizon.absoluteValue
        val absY = vertical.absoluteValue
        if (absX >= absY) {
            ballX += horizon * 0.5f
        } else {
            ballY += vertical * 0.5f
        }
    }
}