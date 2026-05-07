package com.jayce.vexis.foundation.ability

import android.util.Log
import androidx.recyclerview.widget.DiffUtil

class RefreshDiffCallback<T>(private val oldList: List<T>, private val newList: List<T>) : DiffUtil.Callback() {

    init {
        Log.d("LJW", "oldList: ${oldList.size}  newList: ${newList.size}")
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}