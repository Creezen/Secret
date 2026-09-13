package com.jayce.vexis.domain.viewmodel

import com.jayce.vexis.client.AndroidTool.getData
import com.jayce.vexis.client.AndroidTool.putData
import com.jayce.vexis.core.base.BaseViewModel
import com.jayce.vexis.domain.bo.TimeBO
import com.jayce.vexis.util.Config.END_DAY
import com.jayce.vexis.util.Config.END_HOUR
import com.jayce.vexis.util.Config.END_MICROSECOND
import com.jayce.vexis.util.Config.END_MILLISECOND
import com.jayce.vexis.util.Config.END_MINUTE
import com.jayce.vexis.util.Config.END_MONTH
import com.jayce.vexis.util.Config.END_SECOND
import com.jayce.vexis.util.Config.END_YEAR
import com.jayce.vexis.util.Config.START_DAY
import com.jayce.vexis.util.Config.START_HOUR
import com.jayce.vexis.util.Config.START_MICROSECOND
import com.jayce.vexis.util.Config.START_MILLISECOND
import com.jayce.vexis.util.Config.START_MINUTE
import com.jayce.vexis.util.Config.START_MONTH
import com.jayce.vexis.util.Config.START_SECOND
import com.jayce.vexis.util.Config.START_YEAR
import java.time.LocalDateTime

class HistoryViewModel : BaseViewModel() {

    private var hasValidStartTime: Boolean = false
    private var hasValidEndTime: Boolean = false

    var scale: Int = 0
    var startTime: TimeBO = TimeBO.zero()
    var endTime: TimeBO = TimeBO.zero()

    suspend fun init() {
        val startYear = getData(START_YEAR, -1)
        val startMonth = getData(START_MONTH, -1)
        val startDay = getData(START_DAY, -1)
        val startHour = getData(START_HOUR, -1)
        val startMinute = getData(START_MINUTE, -1)
        val startSecond = getData(START_SECOND, -1)
        val startMilliSecond = getData(START_MILLISECOND, -1)
        val startMicroSecond = getData(START_MICROSECOND, -1)
        val endYear = getData(END_YEAR, -1)
        val endMonth = getData(END_MONTH, -1)
        val endDay = getData(END_DAY, -1)
        val endHour = getData(END_HOUR, -1)
        val endMinute = getData(END_MINUTE, -1)
        val endSecond = getData(END_SECOND, -1)
        val endMilliSecond = getData(END_MILLISECOND, -1)
        val endMicroSecond = getData(END_MICROSECOND, -1)
        val tempStartTime = if (startYear < 0) TimeBO.zero()
        else TimeBO(startYear, startMonth, startDay, startHour, startMinute, startSecond, startMilliSecond, startMicroSecond)
        val tempEndTime = if (endYear < 0) TimeBO.fromLocalDateTime(LocalDateTime.now())
        else TimeBO(endYear, endMonth, endDay, endHour, endMinute, endSecond, endMilliSecond, endMicroSecond)
        startTime = tempStartTime
        endTime = tempEndTime
    }

    suspend fun setTime(startTimeEntry: TimeBO, endTimeEntry: TimeBO) {
        setStartTime(startTimeEntry)
        setEndTime(endTimeEntry)
    }

    suspend fun setStartTime(timeBO: TimeBO) {
        startTime = timeBO
        hasValidStartTime = true
        timeBO.apply {
            putData(START_YEAR, year)
            putData(START_MONTH, month)
            putData(START_DAY, day)
            putData(START_HOUR, hour)
            putData(START_MINUTE, minute)
            putData(START_SECOND, second)
            putData(START_MILLISECOND, milliSecond)
            putData(START_MICROSECOND, microSecond)
        }
    }

    suspend fun setEndTime(timeBO: TimeBO) {
        endTime = timeBO
        hasValidEndTime = true
        timeBO.apply {
            putData(END_YEAR, year)
            putData(END_MONTH, month)
            putData(END_DAY, day)
            putData(END_HOUR, hour)
            putData(END_MINUTE, minute)
            putData(END_SECOND, second)
            putData(END_MILLISECOND, milliSecond)
            putData(END_MICROSECOND, microSecond)
        }
    }
}