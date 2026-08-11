package com.jayce.vexis.business.history.api

import com.jayce.vexis.domain.bo.TimeBO

interface OnOptionClickListener {

    fun onScaleChange(factor: Int)

    fun onTimeChange(start: TimeBO, end: TimeBO)

    fun onSearch(type: Int, text: String, time: TimeBO)
}