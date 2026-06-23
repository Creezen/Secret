package com.jayce.vexis.client

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.os.Environment
import android.provider.MediaStore
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
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
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
import com.jayce.vexis.util.Config.NIL
import com.jayce.vexis.client.BaseTool.envContext
import com.jayce.vexis.client.DataTool.dpToPx
import com.jayce.vexis.client.ability.click.GestureHandle
import com.jayce.vexis.client.ability.click.GestureCallback
import com.jayce.vexis.client.bean.FragmentAnimRes
import com.jayce.vexis.util.client.R
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.File
import kotlin.math.ceil

object AndroidTool {

    fun init() {}

    private val prefs by lazy {
        envContext.getSharedPreferences("TianJiData", Context.MODE_PRIVATE)
    }

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore("creezen")

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    suspend fun <T> getData(key: String, default: T): T {
        return envContext.datastore.data.map { data ->
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

    suspend fun <T> putData(key: String, value: T) {
        envContext.datastore.edit { data ->
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

    fun LinearLayout.addSimpleView(text: String, width: Int, height: Int, textSize: Int? = null) {
        val textView = TextView(envContext).also {
            it.text = text
            it.gravity = Gravity.CENTER
            it.layoutParams = ViewGroup.LayoutParams(width, height)
        }
        if (textSize != null) {
            textView.textSize = textSize.toFloat()
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

    fun TextView.intMsg(default: Int = 0): Int {
        if (this.msg().isEmpty()) return default
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
        Toast.makeText(envContext,"$this", Toast.LENGTH_LONG).show()
    }

    fun <T> T.toastX(location: Float = 56f, delay: Long = 2000) {
        val content = " $this "
        WindowTool.requestFloatWindow(envContext, R.layout.snake_border, delay, content) {
            apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                y = location.dpToPx().toInt()
            }
        }
    }

    fun sendBroadcast(action: String, func: (Intent.() -> Unit)? = null) {
        envContext.sendBroadcast(Intent(action).also {
            it.`package` = envContext.packageName
            func?.invoke(it)
        })
    }

    fun View.registerGestureEvent(viewId: String = NIL, callback: GestureCallback) {
        val handle = GestureHandle()
        handle.registerGestureEvent(viewId, this, callback)
    }

    fun Fragment.registerGestureEvent(viewId: String = NIL) {
        val handle = GestureHandle()
        val callback = this as? GestureCallback ?: return
        val view = this.view ?: return
        handle.registerGestureEvent(viewId, view, callback)
    }

    fun View.unregisterGestureEvent(viewId: String, handle: GestureHandle) {
        handle.unregisterGestureEvent(viewId)
    }

    fun Fragment.unregisterGestureEvent(viewId: String, handle: GestureHandle) {
        handle.unregisterGestureEvent(viewId)
    }

    fun getString(resId: Int, vararg args: Any? = arrayOf()): String {
        return envContext.getString(resId, *args)
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
        envContext.startActivity(intent)
    }

    fun Context.getThemeColor(resourceID: Int): Int {
        val typeValue = TypedValue()
        theme.resolveAttribute(resourceID, typeValue, true)
        return typeValue.data
    }

    fun installApp(context: Context) {
        val path = FileTool.getDir(FileTool.Dir.LOC_PRIVATE_FILE, context)
        val file = File("$path/game.apk")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    fun saveToGallery(context: Context, view: View) {
        view.post {
            val map = view.drawToBitmap(Bitmap.Config.ARGB_8888)
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}a.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            val url = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val uri = context.contentResolver.insert(url, values) ?: return@post
            kotlin.runCatching {
                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    map.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
            }.onFailure {
                TLog.e("save error")
            }
        }
    }

    fun View.onVisible(func: () -> Unit) {
        val rect = Rect()
        val isInScreen = getGlobalVisibleRect(rect)
        if (!isInScreen) return
        if (rect.width() < measuredWidth) return
        if (rect.height() < measuredHeight) return
        func.invoke()
    }

    fun Paint.adjustTextSize(drawWidth: Float, text: String) {
        val initSize = 5f
        textSize = initSize
        val measureWidth = measureText(text)
        val realWidth = initSize * (drawWidth / measureWidth)
        textSize = realWidth
    }
}