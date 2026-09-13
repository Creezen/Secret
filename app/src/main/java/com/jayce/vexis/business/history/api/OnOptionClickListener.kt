package com.jayce.vexis.business.history.api

import com.jayce.vexis.domain.bo.TimeBO

interface OnOptionClickListener {

    fun onScaleChange()

    fun onTimeChange()

    fun onSearch(type: Int, text: String, time: TimeBO)
}