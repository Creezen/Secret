package com.jayce.vexis.foundation.ui.block.time

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.jayce.vexis.domain.bo.TimeBO

interface ITimePicker<T : ViewBinding> {

    val binding: T

    fun initLayout(context: Context, parent: ViewGroup)

    fun initUI()

    fun getTime(): TimeBO

    fun setOnTimePickerChange(onTimeChange: TimeBO.() -> Unit)

    fun setTime(time: String)
}