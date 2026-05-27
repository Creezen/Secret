package com.jayce.vexis.foundation.ui.block.time

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.creezen.commontool.getMaxDayOfMonth
import com.creezen.tool.AndroidTool.init
import com.jayce.vexis.databinding.TimePickerYearMonthDayBinding
import com.jayce.vexis.domain.bean.TimeUnitEntry
import com.jayce.vexis.foundation.ui.block.time.TimePickerFactory.Companion.dayList
import com.jayce.vexis.foundation.ui.block.time.TimePickerFactory.Companion.monthList
import com.jayce.vexis.foundation.ui.block.time.TimePickerFactory.Companion.yearList
import java.time.LocalDateTime

class SimpleTimePicker : ITimePicker<TimePickerYearMonthDayBinding> {

    private var yearValue: Int = 1
        get() = binding.year.value + 1
        set(value) {
            binding.year.value = value - 1
            field = value - 1
        }

    private var monthValue: Int = 1
        get() = binding.month.value + 1
        set(value) {
            binding.month.value = value
            field = value - 1
        }

    private var dayValue: Int = 1
        get() = binding.day.value + 1
        set(value) {
            binding.day.value = value - 1
            field = value - 1
        }

    override lateinit var binding: TimePickerYearMonthDayBinding

    private  var onSimpleTimeChange: (TimeUnitEntry.() -> Unit)? = null

    override fun initLayout(context: Context, parent: ViewGroup) {
        binding = TimePickerYearMonthDayBinding.inflate(LayoutInflater.from(context), parent)
    }

    override fun initUI() {
        binding.apply {
            year.init(yearList, 2025)
            month.init(monthList)
            day.init(dayList)
            year.setOnValueChangedListener { _, _, newValue ->
                day.maxValue = getMaxDayOfMonth(newValue, monthValue) - 1
                onSimpleTimeChange?.invoke(getTime())
            }
            month.setOnValueChangedListener { _, _, newValue ->
                day.maxValue = getMaxDayOfMonth(yearValue, newValue + 1) - 1
                onSimpleTimeChange?.invoke(getTime())
            }
            day.setOnValueChangedListener { _, _, _ ->
                onSimpleTimeChange?.invoke(getTime())
            }
        }
    }

    override fun getTime() = TimeUnitEntry(yearValue, monthValue, dayValue, 0, 0, 0, 0)

    override fun setTime(time: String) {
        TimeUnitEntry.fromLocalDateTime(LocalDateTime.parse(time)).apply {
            yearValue = year
            monthValue = month
            dayValue = day
        }
    }

    override fun setOnTimePickerChange(onTimeChange: TimeUnitEntry.() -> Unit) {
        onSimpleTimeChange = onTimeChange
    }
}