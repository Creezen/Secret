package com.jayce.vexis.business.history

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.jayce.vexis.R
import com.jayce.vexis.client.ThreadTool.runOnIO
import com.jayce.vexis.domain.bo.MomentBO
import com.jayce.vexis.domain.bo.TimeBO
import com.jayce.vexis.util.vo.HistoryVO
import org.koin.core.component.KoinComponent

class TimeView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet), KoinComponent {

    private lateinit var olderTime : TimeBO
    private lateinit var laterTime: TimeBO
    private val momentList = arrayListOf<MomentBO>()
    private var onMomentClick: (MomentBO) -> Unit = {}

    private val paint = Paint()
    private val bitmap by lazy { momentBitmap() }

    fun init(startTime: TimeBO, endTime: TimeBO) = runOnIO { updateTime(startTime, endTime) }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (momentList.isEmpty()) return
        updatePercent()
        momentList.forEach { drawMoment(canvas, it) }
    }

    private fun drawMoment(canvas: Canvas, item: MomentBO) {
        if (item.percent <= 0) return
        val minX = width * 1.0f - bitmap.width
        val minY = height * item.percent
        val maxX = width.toFloat()
        val maxY = minY + bitmap.height
        item.updateRect(minX, maxX, minY, maxY)
        canvas.drawBitmap(bitmap, minX, minY, paint)
    }

    fun updateTime(startTime: TimeBO, endTime: TimeBO) {
        olderTime = startTime
        laterTime = endTime
    }

    fun setOnMomentClick(onClick: (MomentBO) -> Unit) { this.onMomentClick = onClick }

    fun addMoment(entryList: List<HistoryVO>) {
        momentList.clear()
        entryList.forEach { entry ->
            val moment = MomentBO(entry.millisTime(), entry.event)
            momentList.add(moment)
        }
        invalidate()
    }

    private fun updatePercent() {
        val olderTime = olderTime.totalMilliSecond()
        val laterTime = laterTime.totalMilliSecond()
        momentList.forEach {
            val momentTime = TimeBO.totalMilliSecond(it.timeStamp)
            if (momentTime < olderTime || momentTime > laterTime) return@forEach
            val duration = laterTime - olderTime
            val percent = (momentTime.toFloat() - olderTime) / duration
            it.percent = percent
        }
    }

    private fun momentBitmap(): Bitmap {
        val bitmapConfig = Bitmap.Config.ARGB_8888
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.goal, null)
            ?: return Bitmap.createBitmap(1, 1, bitmapConfig)
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, bitmapConfig)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        if (event.actionMasked != MotionEvent.ACTION_DOWN) return false
        val itemList = momentList.reversed().filter {
            it.isClicked(event.x, event.y)
        }
        if (itemList.isNotEmpty()) {
            onMomentClick.invoke(itemList.first())
        }
        return true
    }
}