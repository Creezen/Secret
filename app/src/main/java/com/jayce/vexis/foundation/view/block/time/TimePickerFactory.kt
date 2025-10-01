package com.jayce.vexis.foundation.view.block.time

import com.creezen.tool.DataTool.getNumberList

class TimePickerFactory {

    companion object {
        val yearList by lazy { getNumberList(10000) }
        val monthList by lazy { getNumberList(12, 1) }
        val dayList by lazy { getNumberList(31, 1) }
        val hourList by lazy { getNumberList(24, 0) }
        val minusList by lazy { getNumberList(60, 0) }
        val secondList by lazy { getNumberList(60, 0) }
        val millsList by lazy { getNumberList(1000, 0) }
    }

    fun getTimePicker(type: TimePickerType) = when(type) {
        TimePickerType.SIMPLE -> SimpleTimePicker()
        TimePickerType.FULL -> FullTimePicker()
    }

}