package com.jayce.vexis.core.base

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jayce.vexis.foundation.ability.RefreshDiffCallback

abstract class BaseAdapter<T, VH: RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    abstract fun getAttachedList(): List<T>

    abstract fun updateAttachedList(newList: List<T>)

    fun notifyDataChange(newList: List<T>) {
        val callback = RefreshDiffCallback(getAttachedList(), newList)
        val result = DiffUtil.calculateDiff(callback)
        updateAttachedList(newList)
        result.dispatchUpdatesTo(this)
    }
}