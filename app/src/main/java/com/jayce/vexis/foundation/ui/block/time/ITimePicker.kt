package com.jayce.vexis.foundation.ui.block.time

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.jayce.vexis.domain.bean.TimeUnitEntry

interface ITimePicker<T : ViewBinding> {

    val binding: T

    fun initLayout(context: Context, parent: ViewGroup)

    fun initUI()

    fun getTime(): TimeUnitEntry

    fun setOnTimePickerChange(onTimeChange: TimeUnitEntry.() -> Unit)

    fun setTime(time: String)
}