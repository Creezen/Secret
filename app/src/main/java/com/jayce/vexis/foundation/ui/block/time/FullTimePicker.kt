package com.jayce.vexis.foundation.ui.block.time

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.creezen.commontool.getMaxDayOfMonth
import com.creezen.tool.AndroidTool.init
import com.jayce.vexis.databinding.TimePickerBinding
import com.jayce.vexis.domain.bean.TimeUnitEntry
import com.jayce.vexis.foundation.ui.block.time.TimePickerFactory.Companion.dayList
import com.jayce.vexis.foundation.ui.block.time.TimePickerFactory.Companion.hourList
import com.jayce.vexis.foundation.ui.block.time.TimePickerFactory.Companion.millsList
import com.jayce.vexis.foundation.ui.block.time.TimePickerFactory.Companion.minusList
import com.jayce.vexis.foundation.ui.block.time.TimePickerFactory.Companion.monthList
import com.jayce.vexis.foundation.ui.block.time.TimePickerFactory.Companion.secondList
import com.jayce.vexis.foundation.ui.block.time.TimePickerFactory.Companion.yearList
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FullTimePicker : ITimePicker<TimePickerBinding> {

    override lateinit var binding: TimePickerBinding

    private var yearValue: Int = 1
        get() = binding.year.value + 1
        set(value) {
            binding.year.value = value -1
            field = value -1
        }

    private var monthValue: Int = 1
        get() = binding.month.value + 1
        set(value) {
            binding.month.value = value - 1
            field = value - 1
        }

    private var dayValue: Int = 1
        get() = binding.day.value + 1
        set(value) {
            binding.day.value = value - 1
            field = value - 1
        }

    private var hourValue: Int = 0
        get() = binding.hour.value
        set(value) {
            binding.hour.value = value
            field = value
        }

    private var minuteValue: Int = 0
        get() = binding.minus.value
        set(value) {
            binding.minus.value = value
            field = value
        }

    private var secondValue: Int = 0
        get() = binding.second.value
        set(value) {
            binding.second.value = value
            field = value
        }

    private var millisValue: Int = 0
        get() = binding.mills.value
        set(value) {
            binding.mills.value = value
            field = value
        }

    private  var onFullTimeChange: (TimeUnitEntry.() -> Unit)? = null

    override fun initLayout(context: Context, parent: ViewGroup) {
        binding = TimePickerBinding.inflate(LayoutInflater.from(context), parent)
    }

    override fun initUI() {
        binding.apply {
            year.init(yearList)
            month.init(monthList)
            day.init(dayList)
            hour.init(hourList)
            minus.init(minusList)
            second.init(secondList)
            mills.init(millsList)

            year.setOnValueChangedListener { _, _, newValue ->
                day.maxValue = getMaxDayOfMonth(newValue, monthValue) - 1
                onFullTimeChange?.invoke(getTime())
            }
            month.setOnValueChangedListener { _, _, newValue ->
                day.maxValue = getMaxDayOfMonth(yearValue, newValue + 1) - 1
                onFullTimeChange?.invoke(getTime())
            }
            day.setOnValueChangedListener { _, _, _ ->
                onFullTimeChange?.invoke(getTime())
            }
            hour.setOnValueChangedListener { _, _, _ ->
                onFullTimeChange?.invoke(getTime())
            }
            minus.setOnValueChangedListener { _, _, _ ->
                onFullTimeChange?.invoke(getTime())
            }
            second.setOnValueChangedListener { _, _, _ ->
                onFullTimeChange?.invoke(getTime())
            }
            mills.setOnValueChangedListener { _, _, _ ->
                onFullTimeChange?.invoke(getTime())
            }
        }
    }

    override fun getTime() = TimeUnitEntry(yearValue, monthValue , dayValue, hourValue, minuteValue, secondValue, millisValue, 0)

    override fun setTime(time: String) {
        if (time.isEmpty()) return
        val format = DateTimeFormatter.ofPattern("y-M-d  H:m:s.SSS")
        TimeUnitEntry.fromLocalDateTime(LocalDateTime.parse(time, format)).apply {
            yearValue = year
            monthValue = month
            dayValue = day
            hourValue = hour
            minuteValue = minute
            secondValue = second
            millisValue = milliSecond
        }
    }

    override fun setOnTimePickerChange(onTimeChange: TimeUnitEntry.() -> Unit) {
        onFullTimeChange = onTimeChange
    }
}