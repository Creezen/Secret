package com.jayce.vexis.foundation.ui.block

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.jayce.vexis.R

class DragScroll(private val context: Context, val attr: AttributeSet) : View(context, attr){

    var isDragged: Boolean = false
        set(value) {
            onDragStateChanged?.invoke(value)
            field = value
        }
    private var lastY: Float = 0f
    private var dragTop = 0f
    private val dragWidth = 60f
    private var dragHeight = 30f
    private val lineWidth = 5f
    private val lineMargin = 2f
    var percent: Float = 0f
        set(value) {
            dragTop = (height - dragHeight) * value
            field = value
            invalidate()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.lightGray)
    }
    private val linePaint = Paint(paint).apply {
        color = context.getColor(R.color.gray)
        strokeWidth = lineWidth
    }

    private var onDragStateChanged: ((Boolean) -> Unit)? = null
    private var onDrag: ((Float) -> Unit)? = null

    fun setOnDragStateChange(func: (Boolean) -> Unit) { onDragStateChanged = func }
    fun setOnDrag(func: (Float) -> Unit) { onDrag = func }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawScroll(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return super.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (isDragged) return true
                if (!isDragging(event.x, event.y)) return true
                isDragged = true
                lastY = event.y
            }
            MotionEvent.ACTION_UP -> { isDragged = false }
            MotionEvent.ACTION_MOVE -> {
                if (!isDragged) return true
                val maxOffset = height - dragHeight
                if (event.y <= 0 || event.y >= maxOffset) { return true }
                val offset = event.y - lastY
                dragTop += offset
                onDrag?.invoke(offset / maxOffset)
                invalidate()
                lastY = event.y
            }
            MotionEvent.ACTION_CANCEL -> isDragged = false
        }
        return true
    }

    private fun isDragging(x: Float, y: Float): Boolean {
        return x >= 0 && x <= width && y >= dragTop && y <= dragTop + dragHeight
    }

    private fun drawScroll(canvas: Canvas) {
        val radius = dragHeight * 0.5f
        val dragMargin = (width - dragWidth) * 0.5f
        val rectF = RectF(dragMargin, dragTop, dragMargin + dragWidth, dragTop + dragHeight)
        canvas.drawRoundRect(rectF, radius, radius, paint)
        val top = dragTop + lineMargin
        val bottom = dragTop + dragHeight - lineMargin
        val left = dragMargin + radius + lineWidth * 0.5f
        val middle = width * 0.5f
        val right = width - dragMargin - lineWidth * 0.5f - radius
        canvas.drawLine(left, top, left, bottom, linePaint)
        canvas.drawLine(middle, top, middle, bottom, linePaint)
        canvas.drawLine(right, top, right, bottom, linePaint)
    }
}