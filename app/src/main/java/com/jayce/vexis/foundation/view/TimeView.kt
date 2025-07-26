package com.jayce.vexis.foundation.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.jayce.vexis.R
import com.jayce.vexis.business.history.TraceCell

class TimeView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    companion object {
        const val TAG = "TimeView"
    }

    private var earliestTime: Long = 0
    private var latestTime: Long = Long.MAX_VALUE
    private val contentList = arrayListOf<TraceCell>()
    private var onItemClick: (TraceCell) -> Unit = {}

    private val paint = Paint()
    private val bitmap by lazy {
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.delete, null)
        var bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        drawable?.let {
            bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, canvas.width, canvas.height)
            it.draw(canvas)
        }
        bitmap
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(contentList.isEmpty()) return
        contentList.forEach {
            drawTrace(canvas, it)
        }
    }

    private fun drawTrace(canvas: Canvas, item: TraceCell) {
        item.minX = width * 1.0f - bitmap.width
        item.minY = height * item.percent
        item.maxX = width.toFloat()
        item.maxY = item.minY + bitmap.height
        canvas.drawBitmap(bitmap, item.minX, item.minY, paint)
    }

    fun init(earliestTime: Long, latestTime: Long, onClick: (TraceCell) -> Unit) {
        this.earliestTime = earliestTime
        this.latestTime = latestTime
        this.onItemClick = onClick
    }

    fun addTraceCell(time: Long) {
        val percent = (time * 1.0 / (latestTime - earliestTime)).toFloat()
        contentList.add(TraceCell(timeStamp = time, percent = percent, message = "hello"))
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event == null) return true
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val itemList = contentList.reversed().filter {
                    it.isClicked(event.x, event.y)
                }
                if(itemList.isNotEmpty()) {
                    onItemClick.invoke(itemList.first())
                }
                return true
            }
            else -> { return false }
        }
    }
}
