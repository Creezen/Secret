package com.ljw.secret.util

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import com.ljw.util.TUtil

object AndroidUtil {

    private val prefsMap = HashMap<Context, SharedPreferences>()

    private fun getPrefs(
        context: Context,
    ): SharedPreferences {
        return prefsMap[context] ?: context.getSharedPreferences("TianJiData", Context.MODE_PRIVATE).also {
            prefsMap[context] = it
        }
    }

    fun writePrefs(
        context: Context,
        func: (Editor)->Unit
    ) {
        val prefs = getPrefs(context)
        prefs.edit().apply {
            func(this)
        }.apply()
    }

    fun <T> readPrefs(
        context: Context,
        func: (SharedPreferences) -> T?
    ): T? {
        val prefs = getPrefs(context)
        prefs.apply {
            return func(this)
        }
    }

    fun LinearLayout.addSimpleView(
        text: String,
        borderLength: Int = WRAP_CONTENT,
        otherBorder: Int = MATCH_PARENT,
        textSize: Int? = null,
        beInCenter: Boolean = true
    ) {
        val orientation = this.orientation
        val textView = TextView(TUtil.getTJEnv()).also {
            it.text = text
            if (orientation == LinearLayout.HORIZONTAL) {
                it.layoutParams = ViewGroup.LayoutParams(
                    borderLength, otherBorder
                )
            } else {
                it.layoutParams = ViewGroup.LayoutParams(
                    otherBorder, borderLength
                )
            }
        }
        if (textSize != null) {
            textView.textSize = textSize.toFloat()
        }
        if (beInCenter) {
            textView.gravity = Gravity.CENTER
        }
        addView(textView)
    }
}