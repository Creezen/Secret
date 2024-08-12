package com.creezen.tool

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.withTimeoutOrNull
import kotlinx.coroutines.withTimeoutOrNull

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

    fun <T> Spinner.init(
        list: List<T>,
        promptResId: Int,
        dropdownResId: Int,
        itemSelect: ((T)->Unit)? = null
    ){
        val dataArrayList = ArrayList(list)
        val spinnerAdapter = ArrayAdapter(env(), promptResId, dataArrayList)
        spinnerAdapter.setDropDownViewResource(dropdownResId)
        this.adapter = spinnerAdapter
        this.setOnItemSelectedListener {
            onItemSelected{ _, _, pos, _ ->
                itemSelect?.invoke(dataArrayList[pos])
            }
        }
        this.setSelection(0)
    }

    inline fun Spinner.setOnItemSelectedListener(bridge: SpinnerBridge.()->Unit) = setOnItemSelectedListener(
        SpinnerBridge().apply(bridge))

    class SpinnerBridge: AdapterView.OnItemSelectedListener {
        private var onItemSelected:((AdapterView<*>?, View?, Int, Long) -> Unit)? = null
        private var onNothingSelected:((AdapterView<*>?) -> Unit)? = null
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) { onItemSelected?.invoke(p0, p1, p2, p3) }
        override fun onNothingSelected(p0: AdapterView<*>?) { onNothingSelected?.invoke(p0) }
        fun onItemSelected(listener:(AdapterView<*>?, View?, Int, Long) -> Unit) { onItemSelected = listener }
        fun onNothingSelected(listener:(AdapterView<*>?) -> Unit){ onNothingSelected = listener }
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
        timeoutMessage: String? = null,
        func: suspend () -> Unit
    ) {
        owner.lifecycleScope.launch {
            if(delayMillis <= 0) {
                func.invoke()
                return@launch
            }
            val result = withTimeoutOrNull(delayMillis) {
                func.invoke()
            }
            if(result == null) {
                timeoutMessage?.toast() ?: "任务执行超时".toast()
            }
        }
    }
}