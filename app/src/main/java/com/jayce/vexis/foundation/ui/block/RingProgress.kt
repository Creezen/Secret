package com.jayce.vexis.foundation.ui.block

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import com.jayce.vexis.R
import com.jayce.vexis.client.AndroidTool.adjustTextSize
import com.jayce.vexis.client.TLog
import java.util.Locale
import kotlin.math.min

class RingProgress(context: Context, attr: AttributeSet) : View(context, attr) {

    private var status: Int = 0
    private var showGradient: Boolean = true
    private var minSide: Int = 100
    private var stroke = 10f
    var type: Int = 0
    var strokePercent  = 0.25f
        set(value) {
            stroke = minSide * value
            paint.strokeWidth = stroke
            arcPaint.strokeWidth = stroke
            field = value
            postInvalidate()
        }
    var progress: Float = 0f
        set(value) {
            if (field == value) return
            field = if (value >= 1.0f) 1.0f else value
            if (field >= 1.0f) {
                showGradient = false
                hintText = "下载完成"
            }
            postInvalidate()
        }
    var hintText = "点击下载"
        set(value) {
            field = value
            postInvalidate()
        }
    private val gradientColor = intArrayOf(
        context.getColor(R.color.white),
        context.getColor(R.color.glassyGreen)
    )

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.lightGray)
        style = Paint.Style.STROKE
        strokeWidth = stroke
    }

    private val arcPaint = Paint(paint).apply {
        color = context.getColor(R.color.glassyGreen)
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hintPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        minSide = min(width, height)
        val radius = minSide * 0.5f * 0.85f
        drawRing(canvas, radius)
        drawText(canvas, radius)
    }

    private fun drawRing(canvas: Canvas, radius: Float) {
        canvas.drawCircle(width / 2f, height / 2f, radius, paint)
        val angle = 360 * progress
        if (showGradient) {
            val matrix = Matrix().apply { setRotate(90f, width / 2f, height / 2f) }
            val gradient = SweepGradient(width / 2f, height / 2f, gradientColor, null)
            gradient.setLocalMatrix(matrix)
            arcPaint.shader = gradient
        } else arcPaint.shader = null
        canvas.drawArc(getArcOval(radius), 90f, angle, false, arcPaint)
    }

    private fun drawText(canvas: Canvas, radius: Float) {
        val progressText = String.format(Locale.CHINA, "%.1f%%", progress * 100)
        progressPaint.adjustTextSize((radius * 2f - stroke * 1.2f) * 0.7f, progressText)
        var textWidth = progressPaint.measureText(progressText)
        canvas.drawText(progressText, width * 0.5f - textWidth * 0.5f, height * 0.5f - stroke * 0.5f, progressPaint)

        hintPaint.adjustTextSize((radius * 2f - stroke * 1.2f) * 0.8f, hintText)
        textWidth = hintPaint.measureText(hintText)
        canvas.drawText(hintText, width * 0.5f - textWidth * 0.5f, height * 0.5f + hintPaint.textSize, hintPaint)
    }

    private fun getArcOval(border: Float): RectF {
        val halfWidth = width / 2f
        val halfHeight = height / 2f
        val left = halfWidth - border
        val top = halfHeight - border
        val right = halfWidth + border
        val bottom = halfHeight + border
        return RectF(left, top, right, bottom)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        val listener = if (type == 1) l else ClickWrapper(l)
        super.setOnClickListener(listener)
    }

    inner class ClickWrapper(private val l: OnClickListener?) : OnClickListener {
        override fun onClick(v: View?) {
            when (status) {
                0, 2 -> {
                    status = 1
                    hintText = "正在下载"
                    l?.onClick(v)
                }
                1 -> {
                    status = 2
                    hintText = "下载暂停"
                }
            }
        }
    }
}