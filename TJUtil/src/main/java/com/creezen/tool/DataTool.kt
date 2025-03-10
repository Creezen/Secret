package com.creezen.tool

import android.view.MotionEvent
import kotlin.math.pow
import kotlin.math.sqrt

object DataTool {

    fun calculateMultiPointDistance(event: MotionEvent): Float {
        return calculateDistance(
            event.getX(0),
            event.getY(0),
            event.getX(1),
            event.getY(1))
    }

    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val distX = (x1 - x2).toDouble().pow(2.0).toFloat()
        val distY = (y1 - y2).toDouble().pow(2.0).toFloat()
        return sqrt(distX + distY)
    }
    
}