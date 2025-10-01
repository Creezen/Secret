package com.jayce.vexis.foundation.view.block.time

import android.content.Context
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

interface ITimePicker <T: ViewBinding> {

    val binding: T

    fun initLayout(context: Context, parent: ViewGroup)

    fun initUI()

    fun getTime(): List<String>
}