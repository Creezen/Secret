package com.jayce.vexis.stylized

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.appcompat.widget.PopupMenu
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.view.menu.MenuPopupHelper

@SuppressLint("ClickableViewAccessibility")
class TrackPopupMenu(context: Context, private val view: View): PopupMenu(context, view) {

    private var clickX: Int = 0
    private var clickY: Int = 0

    init {
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                clickX = event.x.toInt()
                clickY = event.y.toInt()
            }
            false
        }
    }

    @SuppressLint("RestrictedApi")
    override fun show() {
        val parent = this::class.java.superclass
        val field = parent.getDeclaredField("mPopup")
        field.isAccessible = true
        val obj = field.get(this) as MenuPopupHelper
        obj.show(clickX, clickY - view.height)
        obj.setForceShowIcon(true)
    }
}