package com.creezen.tool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.creezen.tool.BaseTool.env
import com.creezen.tool.ability.click.ClickHandle
import com.creezen.tool.ability.click.SwipeCallback
import com.creezen.tool.contract.LifecycleJob
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.withTimeoutOrNull
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.CoroutineContext

object AndroidTool {

    private val prefs by lazy {
        env().getSharedPreferences("TianJiData", Context.MODE_PRIVATE)
    }

    @SuppressLint("ApplySharedPref")
    fun writePrefs(
        func: (Editor)->Unit
    ) {
        prefs.edit().apply {
            func(this)
            commit()
        }
    }

    fun <T> readPrefs(
        func: (SharedPreferences) -> T?
    ): T? {
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

    fun replaceFragment(
        fragmentManager: FragmentManager,
        resourceID: Int,
        fragment: Fragment,
        isAddToStack: Boolean = false
    ){
        val beginTransaction = fragmentManager.beginTransaction()
        beginTransaction.replace(resourceID,fragment)
        if (isAddToStack) {
            beginTransaction.addToBackStack(null)
        }
        beginTransaction.commit()
    }

    fun <T> T.toast() {
        Toast.makeText(env(),"$this", Toast.LENGTH_LONG).show()
    }

    fun workInDispatch(
        owner: LifecycleOwner,
        delayMillis: Long = 0,
        coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
        lifecycleJob: LifecycleJob
    ) {
        var result: Any?
        owner.lifecycleScope.launch(coroutineDispatcher) {
            if(delayMillis <= 0) {
                lifecycleJob.onDispatch()
                return@launch
            }
            result = withTimeoutOrNull(delayMillis) {
                lifecycleJob.onDispatch()
            }
            lifecycleJob.onTimeoutFinish(result != null)
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
}