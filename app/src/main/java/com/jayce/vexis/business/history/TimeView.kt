package com.jayce.vexis.business.history

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.creezen.commontool.bean.HistoryBean
import com.creezen.tool.TLog
import com.creezen.tool.ThreadTool
import com.jayce.vexis.R
import com.jayce.vexis.domain.bean.MomentEntry
import com.jayce.vexis.domain.bean.TimeUnitEntry
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TimeView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet), KoinComponent {

    private val manager by inject<TimeManager>()

    private lateinit var olderTime : TimeUnitEntry
    private lateinit var laterTime: TimeUnitEntry
    private val momentList = arrayListOf<MomentEntry>()
    private var onMomentClick: (MomentEntry) -> Unit = {}

    private val paint = Paint()
    private val bitmap by lazy { momentBitmap() }

    init {
        ThreadTool.runOnIO { updateTime() }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (momentList.isEmpty()) return
        updatePercent()
        momentList.forEach { drawMoment(canvas, it) }
    }

    private fun drawMoment(canvas: Canvas, item: MomentEntry) {
        val minX = width * 1.0f - bitmap.width
        val minY = height * item.percent
        val maxX = width.toFloat()
        val maxY = minY + bitmap.height
        item.updateRect(minX, maxX, minY, maxY)
        canvas.drawBitmap(bitmap, minX, minY, paint)
    }

    suspend fun updateTime() {
        val pair = manager.getTime()
        olderTime = pair.first
        laterTime = pair.second
    }

    fun setOnMomentClick(onClick: (MomentEntry) -> Unit) {
        this.onMomentClick = onClick
    }

    fun addMoment(entryList: List<HistoryBean>) {
        momentList.clear()
        entryList.forEach { entry ->
            val moment = MomentEntry(entry.millisTime(), entry.event)
            momentList.add(moment)
        }
        invalidate()
    }

    private fun updatePercent() {
        val olderTime = olderTime.totalMilliSecond()
        val laterTime = laterTime.totalMilliSecond()
        momentList.forEach {
            val momentTime = TimeUnitEntry.totalMilliSecond(it.timeStamp)
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