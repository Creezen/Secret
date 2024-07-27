package com.jayce.vexis.stylized.animator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class LoginCanvas(context: Context, attrs: AttributeSet): View(context, attrs) {

    private var paint: Paint = Paint()
    private val rect = Rect(50, 0, 150, 100)

    init {
        paint.isAntiAlias = true
        paint.color = Color.RED
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate((width/2).toFloat(), (height/2).toFloat())
        repeat(36){
            canvas.drawRect(rect, paint)
            canvas.rotate(10f)
        }
    }
}