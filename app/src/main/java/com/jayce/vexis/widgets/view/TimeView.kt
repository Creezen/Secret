package com.jayce.vexis.widgets.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.jayce.vexis.R

class TimeView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    companion object {
        const val TAG = "TimeView"
    }

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
        canvas.drawBitmap(bitmap, width * 1.0f - bitmap.width, height / 2f, paint)
        canvas.drawBitmap(bitmap, width * 1.0f - bitmap.width - 50, height / 2f - 50, paint)
        canvas.drawBitmap(bitmap, width * 1.0f - bitmap.width - 100, height / 2f - 100, paint)
    }
}
