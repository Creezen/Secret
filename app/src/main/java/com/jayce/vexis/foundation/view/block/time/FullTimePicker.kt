package com.jayce.vexis.foundation.view.block.time

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.creezen.tool.AndroidTool.init
import com.jayce.vexis.databinding.TimePickerBinding
import com.jayce.vexis.foundation.view.block.time.TimePickerFactory.Companion.dayList
import com.jayce.vexis.foundation.view.block.time.TimePickerFactory.Companion.hourList
import com.jayce.vexis.foundation.view.block.time.TimePickerFactory.Companion.millsList
import com.jayce.vexis.foundation.view.block.time.TimePickerFactory.Companion.minusList
import com.jayce.vexis.foundation.view.block.time.TimePickerFactory.Companion.monthList
import com.jayce.vexis.foundation.view.block.time.TimePickerFactory.Companion.secondList
import com.jayce.vexis.foundation.view.block.time.TimePickerFactory.Companion.yearList

class FullTimePicker : ITimePicker<TimePickerBinding> {

    override lateinit var binding: TimePickerBinding

    override fun initLayout(context: Context, parent: ViewGroup) {
        binding = TimePickerBinding.inflate(LayoutInflater.from(context), parent)
    }

    override fun initUI() {
        binding.apply{
            year.init(yearList, 2025)
            month.init(monthList)
            day.init(dayList)
            hour.init(hourList)
            minus.init(minusList)
            second.init(secondList)
            mills.init(millsList)
            year.setOnValueChangedListener { _, _, newVal ->

            }
        }
    }

    override fun getTime(): List<String> {
        return binding.let{
            ArrayList<String>().apply {
                add(yearList[it.year.value])
                add(monthList[it.month.value])
                add(dayList[it.day.value])
                add(hourList[it.hour.value])
                add(minusList[it.minus.value])
                add(secondList[it.second.value])
                add(millsList[it.mills.value])
            }
        }
    }
}