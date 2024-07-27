package com.creezen.tool

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.text.Editable
import android.text.TextWatcher
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

object AndroidTool {

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
        val textView = TextView(BaseTool.getTJEnv()).also {
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
        val spinnerAdapter = ArrayAdapter(BaseTool.getTJEnv(), promptResId, dataArrayList)
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
        resourceID:Int,
        fragment: Fragment,
        isAddToStack:Boolean = false
    ){
        val beginTransaction = fragmentManager.beginTransaction()
        beginTransaction.replace(resourceID,fragment)
        if (isAddToStack) beginTransaction.addToBackStack(null)
        beginTransaction.commit()
    }

    inline fun TextView.addTextChangedListener(bridge: EditTextBridge.()->Unit)=addTextChangedListener(
        EditTextBridge().apply (bridge))

    class EditTextBridge: TextWatcher {
        private var beforeTextChanged: ((CharSequence?, Int, Int, Int) ->  Unit)? = null
        private var onTextChanged: ((CharSequence?, Int, Int, Int) -> Unit)? = null
        private var afterTextChanged: ((Editable?) ->  Unit)? = null
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { beforeTextChanged?.invoke(s, start, count, after) }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { onTextChanged?.invoke(s, start, before, count) }
        override fun afterTextChanged(s: Editable?) { afterTextChanged?.invoke(s) }
        fun beforeTextChanged(listener: (CharSequence?, Int, Int, Int) ->  Unit) { beforeTextChanged = listener }
        fun onTextChanged(listener: (CharSequence?, Int, Int, Int) ->  Unit) { onTextChanged = listener }
        fun afterTextChanged(listener: (Editable?) ->  Unit) { afterTextChanged = listener }
    }

    fun <T> T.toast() {
        Toast.makeText(BaseTool.getTJEnv(),"$this", Toast.LENGTH_LONG).show()
    }
}