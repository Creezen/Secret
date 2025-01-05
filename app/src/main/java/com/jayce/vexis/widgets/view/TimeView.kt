package com.jayce.vexis.widgets.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.creezen.tool.DataTool.calculateMultiPointDistance

class TimeView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    companion object {
        const val TAG = "TimeView"
    }
    private var pointCount = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                pointCount++
            }
            MotionEvent.ACTION_UP -> {
                pointCount--
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                pointCount++
            }
            MotionEvent.ACTION_POINTER_UP -> {
                pointCount--
            }
            MotionEvent.ACTION_MOVE -> {
                if(pointCount >= 2) {
                    Log.d(TAG,"${calculateMultiPointDistance(event)}")
                }
            }
        }
        return super.onTouchEvent(event)
    }

}