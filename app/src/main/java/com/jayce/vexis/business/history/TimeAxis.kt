package com.jayce.vexis.business.history

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jayce.vexis.R
import com.jayce.vexis.client.AndroidTool.adjustTextSize
import com.jayce.vexis.domain.bean.TimeUnitEntry
import com.jayce.vexis.domain.enums.TimeLevel
import com.jayce.vexis.util.Config.BASE_FACTOR_TWELVE
import com.jayce.vexis.util.Config.DAY
import kotlin.math.abs
import kotlin.math.min

class TimeAxis(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    companion object {
        private const val MIN_UNIT = 50
    }

    private var maxUnitCount: Int = 0
    private var startTime: TimeUnitEntry = TimeUnitEntry.zero()
    private var endTime: TimeUnitEntry = TimeUnitEntry.now()
    private var period: Pair<TimeUnitEntry, Long> = TimeUnitEntry.zero() to 0L
    private var timeLevel: TimeLevel = TimeLevel.LEVEL_TEN_MICRO_SECOND
    private var whiteSpace: Long = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintBold = Paint(paint).apply {
        strokeWidth = 3f
    }
    private val textPaint = Paint(paint).apply {
        color = context.getColor(R.color.metallicGold)
    }
    private val rectPaint = Paint(paint).apply {
        color = context.getColor(R.color.paleGray)
        style = Paint.Style.FILL
    }

    fun updateTimePeriod(start: TimeUnitEntry, end: TimeUnitEntry) {
        startTime = start
        endTime = end
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawAxis(canvas)
    }

    private fun drawAxis(canvas: Canvas) {
        initDrawLevel()
        val count = getCountByLevel(timeLevel)
        val unitSize = (height / count).toInt()
        val widthUint = measuredWidth * 0.15f
        val widthUnitLong = measuredWidth * 0.3f
        val contentHeight = unitSize * count
        whiteSpace = height - contentHeight
        canvas.drawRect(0f, 0f, width.toFloat(), whiteSpace * 0.5f, rectPaint)
        canvas.drawRect(0f, height - whiteSpace * 0.5f, width.toFloat(), height.toFloat(), rectPaint)
        val startPixel = (whiteSpace * 0.5 + getStartPercent() * contentHeight).toInt()
        val timeCopy = startTime.copy().round(timeLevel)
        for (i in startPixel until height step unitSize) {
            if ((i + whiteSpace * 0.5) > height) return
            val pair = timeLevel.levelTitle(timeCopy)
            val title = pair.first
            val isLevelTitle = pair.second
            val drawPaint: Paint
            val drawWidth: Float
            if (isLevelTitle) {
                drawPaint = paintBold
                drawWidth = widthUnitLong
            } else {
                drawPaint = paint
                drawWidth = widthUint
            }
            val leftWidth = min(measuredWidth * 0.4f, unitSize * 1.0f)
            textPaint.adjustTextSize(leftWidth, title)
            val margin = measuredWidth * 0.4f - textPaint.measureText(title)
            canvas.drawText(title, widthUnitLong + margin * 0.5f, i + textPaint.textSize * 0.4f, textPaint)

            canvas.drawLine(0f, i.toFloat(), drawWidth, i.toFloat(), drawPaint)
            canvas.drawLine(width - drawWidth, i.toFloat(), width.toFloat(), i.toFloat(), drawPaint)

            timeLevel.plus(timeCopy, timeLevel.plusFactor.toLong())
        }
    }

    private fun initDrawLevel() {
        maxUnitCount = height / MIN_UNIT
        period = endTime.diff(startTime)
        updateLevel()
    }

    private fun updateLevel() {
        timeLevel = TimeLevel.LEVEL_TEN_MICRO_SECOND
        while (timeLevel < TimeLevel.LEVEL_NONE) {
            val count = getCountByLevel(timeLevel)
            if (count <= maxUnitCount) return
            timeLevel = TimeLevel.of(timeLevel.nextLevel)
        }
    }

    private fun getCountByLevel(level: TimeLevel): Long {
        val periodDate = period.first
        val periodMillis = period.second
        val mod = when {
            level < TimeLevel.LEVEL_YEAR -> if (periodMillis % level.duration > 0) 1 else 0
            level == TimeLevel.LEVEL_YEAR -> if (periodMillis % DAY > 0) 1 else 0
            level > TimeLevel.LEVEL_YEAR -> {
                val sum = periodDate.year % level.duration +
                    periodDate.month +
                    periodDate.day +
                    periodDate.hour +
                    periodDate.second +
                    periodDate.milliSecond +
                    periodDate.microSecond
                if (sum != 0L) 1 else 0
            }
            else -> 0
        }
        val count = when {
            level < TimeLevel.LEVEL_YEAR -> periodMillis / level.duration + mod
            level == TimeLevel.LEVEL_YEAR -> {
                (periodDate.year * BASE_FACTOR_TWELVE + periodDate.month + mod).toLong()
            }
            level > TimeLevel.LEVEL_YEAR -> periodDate.year / level.duration + mod
            else -> 0
        }
        return count
    }

    private fun getStartPercent(): Double {
        val start = startTime.totalMilliSecond().toDouble()
        val end = endTime.totalMilliSecond().toDouble()
        val roundTime = startTime.copy().round(timeLevel).totalMilliSecond().toDouble()
        val percent = abs(roundTime - start) / abs(end - start)
        return percent
    }
}