package com.jayce.vexis.business.history.api

import com.jayce.vexis.domain.bean.TimeUnitEntry

interface OnOptionClickListener {

    fun onScaleChange(factor: Int)

    fun onTimeChange(start: TimeUnitEntry, end: TimeUnitEntry)

    fun onSearch(type: Int, text: String, time: TimeUnitEntry)
}