package com.jayce.vexis.widgets

import android.util.Log
import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.jayce.vexis.R

class DefaultSpinner(context: Context, attr: AttributeSet) : AppCompatSpinner(context, attr) {

    companion object {
        const val TAG = "DefaultSpinner"
    }

    private var selectCallback: ((Int) -> Unit)? = null

    fun onItemSelect() {
        selectCallback?.invoke(this.selectedItemPosition)
    }

    fun <T> configuration(
        source: List<T>,
        prompLayoutId: Int = R.layout.spinner_prompt,
        downLayoutId: Int = R.layout.spinner_dropdown,
        onSelect: ((Int) -> Unit)? = null,
    ) {
        val list = ArrayList<T>().apply {
            source.forEach { add(it) }
        }
        val adapter = ArrayAdapter(context, prompLayoutId, list)
        adapter.setDropDownViewResource(downLayoutId)
        this.adapter = adapter
        this.selectCallback = onSelect
        this.setSelection(0)
    }

    fun <T> refreshData(list: List<T>) {
        val adapter = this.adapter as ArrayAdapter<T>
        adapter.clear()
        adapter.addAll(list)
        adapter.notifyDataSetChanged()
        this.setSelection(0)
    }
}
