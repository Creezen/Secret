package com.jayce.vexis.foundation.ui.block

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.PopupWindow

@SuppressLint("ClickableViewAccessibility")
class TrackPopupMenu(
    private val layoutView: View,
    private val anchorView: View,
    width: Int = LayoutParams.WRAP_CONTENT,
    height: Int = LayoutParams.WRAP_CONTENT,
    focusable: Boolean = true
) : PopupWindow(layoutView, width, height, focusable) {

    private var clickX: Int = 0
    private var clickY: Int = 0

    init {
        anchorView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                clickX = event.x.toInt()
                clickY = event.y.toInt()
            }
            false
        }
        isFocusable = true
        setTouchInterceptor { v, event -> return@setTouchInterceptor false }
    }

    fun show() {
        showAsDropDown(anchorView, clickX, clickY - anchorView.height - layoutView.height - 64, Gravity.START)
    }

    fun setOutsideDismiss(isDismiss: Boolean = true) {
        setTouchInterceptor { v, event -> return@setTouchInterceptor !isDismiss }
    }
}