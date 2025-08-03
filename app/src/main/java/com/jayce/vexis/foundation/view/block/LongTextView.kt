package com.jayce.vexis.foundation.view.block

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.jayce.vexis.R

class LongTextView(context: Context, attr: AttributeSet) : View(context, attr) {

    private val textPaint: TextPaint = TextPaint()
    private var mCanvas: Canvas? = null
    var longText: String = ""
        set(value) {
            field = value
            drawText(mCanvas)
        }

    init {
        textPaint.isAntiAlias = true
        textPaint.color = Color.GREEN
        textPaint.textSize = 80f
        textPaint.style = Paint.Style.FILL_AND_STROKE
        val ta = context.obtainStyledAttributes(attr, R.styleable.LongTextView)
        longText = ta.getString(R.styleable.LongTextView_longText) ?: ""
        ta.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mCanvas = canvas
        drawText(canvas)
    }

    private fun drawText(canvas: Canvas?) {
        canvas?.let {
//            it.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            val staticLayout = StaticLayout.Builder
                    .obtain(
                        longText,
                        0,
                        longText.length,
                        textPaint,
                        width,
                    ).build()
            staticLayout.draw(it)
            postInvalidate()
        }
    }
}