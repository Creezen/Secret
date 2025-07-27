package com.jayce.vexis.foundation.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView
import com.creezen.tool.AndroidTool.registerSwipeEvent
import com.creezen.tool.ability.click.ClickHandle
import com.creezen.tool.ability.click.SwipeCallback

class ScalableScrollView(context: Context, attr: AttributeSet) : ScrollView(context, attr), SwipeCallback {
    private val handle by lazy {
        ClickHandle(ClickHandle.Mode.INTERCEPT)
    }

    init {
        this.registerSwipeEvent("scale", handle, this@ScalableScrollView)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        var flag = false
        if (computeVerticalScrollRange() <= height) return false
        ev?.let {
            flag = handle.handleViewClick("scale", ev, this@ScalableScrollView)
        }
        return super.onInterceptTouchEvent(ev) && flag
    }

    override fun onPinchIn(
        viewId: String,
        scaleFactor: Float,
    ): Boolean {
        return false
    }

    override fun onPinchOut(
        viewId: String,
        scaleFactor: Float,
    ): Boolean {
        return false
    }
}