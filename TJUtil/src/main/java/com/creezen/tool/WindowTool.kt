package com.creezen.tool

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import kotlinx.coroutines.delay

object WindowTool {

    private var isFloatShow: Boolean = false

    fun requestFloatWindow(
        context: Context,
        viewId: Int,
        dismissDelay: Long = 2000L,
        adjustParam: (WindowManager.LayoutParams.() -> WindowManager.LayoutParams)? = null
    ) {
        val view = LayoutInflater.from(context).inflate(viewId, null)
        requestFloatWindow(context, view, dismissDelay, adjustParam)
    }

    fun requestFloatWindow(
        context: Context,
        view: View,
        dismissDelay: Long = 2000L,
        adjustParam: (WindowManager.LayoutParams.() -> WindowManager.LayoutParams)? = null
    ) {
        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent().apply {
                setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                setData(Uri.parse("package:${context.packageName}"))
            }
            context.startActivity(intent)
        } else {
            showFloatWindow(context, view, dismissDelay, adjustParam)
        }
    }

    private fun showFloatWindow(
        context: Context,
        view: View,
        dismissDelay: Long,
        adjustParam: (WindowManager.LayoutParams.() -> WindowManager.LayoutParams)? = null
    ) {
        val manager = context.getSystemService(WindowManager::class.java)
        var param = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT
        )
        param.gravity = Gravity.START + Gravity.TOP
        adjustParam?.let{
            param = it.invoke(param)
        }
        if (isFloatShow) return
        manager.addView(view, param)
        isFloatShow = true
        ThreadTool.runOnMulti {
            if (dismissDelay < 0) return@runOnMulti
            delay(dismissDelay)
            manager.removeView(view)
            isFloatShow = false
        }
    }
}