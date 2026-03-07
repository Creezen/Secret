package com.creezen.tool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Paint
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AnimRes
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.creezen.tool.BaseTool.env
import com.creezen.tool.DataTool.dpToPx
import com.creezen.tool.ability.click.ClickHandle
import com.creezen.tool.ability.click.SwipeCallback
import com.creezen.tool.bean.FragmentAnimRes
import com.example.testlib.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlin.math.ceil

object AndroidTool {

    fun init() {}

    private val prefs by lazy {
        env().getSharedPreferences("TianJiData", Context.MODE_PRIVATE)
    }

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore("creezen")

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    suspend fun <T> getData(key: String, default: T): T {
        return env().datastore.data.map { data ->
            when (default) {
                is Int -> data[intPreferencesKey(key)] ?: default
                is Double -> data[doublePreferencesKey(key)] ?: default
                is String -> data[stringPreferencesKey(key)] ?: default
                is Boolean -> data[booleanPreferencesKey(key)] ?: default
                is Float -> data[floatPreferencesKey(key)] ?: default
                is Long -> data[longPreferencesKey(key)] ?: default
                is ByteArray -> data[byteArrayPreferencesKey(key)] ?: default
                else -> default
            } as T
        }.firstOrNull() ?: default
    }

    fun <T> getDataAsync(key: String, default: T, block: suspend (T) -> Unit) {
        ThreadTool.runOnMulti(Dispatchers.IO) {
            block(getData(key, default))
        }
    }

    suspend fun <T> putData(key: String, value: T) {
        env().datastore.edit { data ->
            when (value) {
                is Int -> data[intPreferencesKey(key)] = value
                is Double -> data[doublePreferencesKey(key)] = value
                is String -> data[stringPreferencesKey(key)] = value
                is Boolean -> data[booleanPreferencesKey(key)] = value
                is Float -> data[floatPreferencesKey(key)] = value
                is Long -> data[longPreferencesKey(key)] = value
                is ByteArray -> data[byteArrayPreferencesKey(key)] = value
                else -> {}
            }
        }
    }

    fun <T> putDataAsync(key: String, value: T, block: () -> Unit) {
        ThreadTool.runOnMulti(Dispatchers.IO) {
            putData(key, value)
            block.invoke()
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
        val textView = TextView(env()).also {
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

    fun TextView.msg(needTrim: Boolean = true): String {
        val value = text.toString()
        return if (needTrim) {
            value.trim()
        } else {
            value
        }
    }

    fun TextView.intMsg(): Int {
        return this.msg().toInt()
    }

    fun TextView.measureSize(textString: String): Pair<Float, Float> {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        val textWidth = ceil(paint.measureText(textString))
        val textHeight = ceil(paint.fontMetrics.descent - paint.fontMetrics.ascent)
        val realWidth = textWidth + paddingStart + paddingEnd
        val realHeight = textHeight + paddingTop + paddingBottom
        post {
            text = textString
        }
        return realWidth to realHeight
    }

    fun replaceFragment(
        fragmentManager: FragmentManager,
        resourceID: Int,
        fragment: Fragment,
        fragmentTag: String,
        isAddToStack: Boolean = false,
        fragmentAnim: FragmentAnimRes? = null
    ){
        val beginTransaction = fragmentManager.beginTransaction()
        if (fragmentAnim != null) {
            beginTransaction.setCustomAnimations(
                fragmentAnim.enterAnim,
                fragmentAnim.exitAnim,
                fragmentAnim.popEnterAnim,
                fragmentAnim.popExitAnim
            )
        }
        var displayFragment = fragmentManager.findFragmentByTag(fragmentTag)
        if (displayFragment == null) {
            beginTransaction.add(resourceID, fragment, fragmentTag)
            displayFragment = fragment
        }
        fragmentManager.fragments.forEach {
            if(it.tag != fragmentTag) {
                beginTransaction.hide(it)
            }
        }
        beginTransaction.show(displayFragment)
        if (isAddToStack) {
            beginTransaction.addToBackStack(null)
        }
        beginTransaction.commit()
    }

    fun <T> T.toast() {
        Toast.makeText(env(),"$this", Toast.LENGTH_LONG).show()
    }

    fun <T> T.toastX(location: Float = 56f, delay: Long = 2000) {
        val content = " $this "
        WindowTool.requestFloatWindow(env(), R.layout.snake_border, delay, content) {
            apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                y = location.dpToPx().toInt()
            }
        }
    }

    fun broadcastByAction(context: Context, action: String, func: ((Intent) -> Unit)? = null) {
        context.sendBroadcast(Intent(action).also {
            it.`package` = env().packageName
            func?.invoke(it)
        })
    }

    fun View.registerSwipeEvent(viewId: String, handle: ClickHandle, callback: SwipeCallback) {
        handle.registerSwipeEvent(viewId, this, callback)
    }

    fun View.unregisterSwipeEvent(viewId: String, handle: ClickHandle) {
        handle.unregisterSwipeEvent(viewId)
    }

    fun getString(resId: Int, vararg args: Any? = arrayOf()): String {
        return env().getString(resId, *args)
    }

    fun NumberPicker.init(array: Array<String>, select: Int = 0) {
        displayedValues = array
        minValue = 0
        maxValue = array.size - 1
        value = select
    }

    fun startActivity(activityClazz: Class<*>) {
        val intent = Intent("com.jayce.vexis.dynamic.ShellActivity")
        intent.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("className", activityClazz.name)
        }
        env().startActivity(intent)
    }

    fun Context.getThemeColor(resourceID: Int): Int {
        val typeValue = TypedValue()
        theme.resolveAttribute(resourceID, typeValue, true)
        return typeValue.data
    }
}