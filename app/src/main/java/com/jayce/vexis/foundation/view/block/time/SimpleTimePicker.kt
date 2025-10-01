package com.jayce.vexis.foundation.view.block.time

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.creezen.tool.AndroidTool.init
import com.jayce.vexis.databinding.TimePickerYearMonthDayBinding
import com.jayce.vexis.foundation.view.block.time.TimePickerFactory.Companion.dayList
import com.jayce.vexis.foundation.view.block.time.TimePickerFactory.Companion.monthList
import com.jayce.vexis.foundation.view.block.time.TimePickerFactory.Companion.yearList

class SimpleTimePicker : ITimePicker<TimePickerYearMonthDayBinding> {

    override lateinit var binding: TimePickerYearMonthDayBinding

    override fun initLayout(context: Context, parent: ViewGroup) {
        binding = TimePickerYearMonthDayBinding.inflate(LayoutInflater.from(context), parent)
    }

    override fun initUI() {
        binding.apply{
            year.init(yearList, 2025)
            month.init(monthList)
            day.init(dayList)
        }
    }

    override fun getTime(): List<String> {
        return binding.let{
            ArrayList<String>().apply {
                add(yearList[it.year.value])
                add(monthList[it.month.value])
                add(dayList[it.day.value])
            }
        }
    }
}