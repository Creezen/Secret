package com.jayce.vexis.foundation.view.block

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.NumberPicker
import com.creezen.tool.ThreadTool
import com.jayce.vexis.databinding.TimePickerBinding
import kotlinx.coroutines.Dispatchers

class TimePicker(context: Context, attr: AttributeSet): LinearLayout(context, attr) {

    private val binding: TimePickerBinding
    private val yearList by lazy { getList(10000) }
    private val monthList by lazy { getList(12, 1) }
    private val dayList by lazy { getList(31, 1) }
    private val hourList by lazy { getList(24, 0) }
    private val minusList by lazy { getList(60, 0) }
    private val secondList by lazy { getList(60, 0) }
    private val millsList by lazy { getList(1000, 0) }

    init {
        orientation = HORIZONTAL
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        binding = TimePickerBinding.inflate(LayoutInflater.from(context), this)
        ThreadTool.runOnMulti(Dispatchers.Main) {
            initUI()
        }
    }

    private fun initUI() {
        with(binding) {
            year.init(yearList, 2025)
            month.init(monthList)
            day.init(dayList)
            hour.init(hourList)
            minus.init(minusList)
            second.init(secondList)
            mills.init(millsList)

        }
    }

    fun time(): List<String> {
        with(binding) {
            return ArrayList<String>().apply {
                add(yearList[year.value])
                add(monthList[month.value])
                add(dayList[day.value])
                add(hourList[hour.value])
                add(minusList[minus.value])
                add(secondList[second.value])
                add(millsList[mills.value])
            }
        }
    }

    fun formatTime(): String {
        var timeStr = ""
        time().forEachIndexed { i, v ->
            if(i == 0) {
                timeStr += v.padStart(4, '0')
            } else if(i == 6) {
                timeStr += v.padStart(3, '0')
            } else {
                timeStr += v.padStart(2, '0')
            }
        }
        return timeStr
    }

    private fun getList(num: Int, offset: Int = 0): Array<String> {
        return ArrayList<String>().apply {
            repeat(num) {
                add((it + offset).toString())
            }
        }.toTypedArray()
    }

    private fun NumberPicker.init(array: Array<String>, select: Int = 0) {
        displayedValues = array
        minValue = 0
        maxValue = array.size - 1
        value = select
    }

}