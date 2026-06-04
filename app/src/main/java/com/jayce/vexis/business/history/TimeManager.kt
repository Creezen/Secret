package com.jayce.vexis.business.history

import com.creezen.tool.AndroidTool.getData
import com.creezen.tool.AndroidTool.putData
import com.jayce.vexis.domain.bean.TimeUnitEntry
import java.time.LocalDateTime
import java.util.concurrent.locks.ReentrantLock

class TimeManager {

    companion object {
        const val START_YEAR = "startYear"
        const val START_MONTH = "startMonth"
        const val START_DAY = "startDay"
        const val START_HOUR = "startHour"
        const val START_MINUTE = "startMinute"
        const val START_SECOND = "startSecond"
        const val START_MILLISECOND = "startMilliSecond"
        const val START_MICROSECOND = "startMicroSecond"

        const val END_YEAR = "endYear"
        const val END_MONTH = "endMonth"
        const val END_DAY = "endDay"
        const val END_HOUR = "endHour"
        const val END_MINUTE = "endMinute"
        const val END_SECOND = "endSecond"
        const val END_MILLISECOND = "endMilliSecond"
        const val END_MICROSECOND = "endMicroSecond"
    }

    private val lock = ReentrantLock()
    private var hasValidStartTime: Boolean = false
    private var hasValidEndTime: Boolean = false
    private var hasReadPersistTime: Boolean = false
    
    private var startTime: TimeUnitEntry = TimeUnitEntry.zero()
    private var endTime: TimeUnitEntry = TimeUnitEntry.zero()
    
    suspend fun getTime(): Pair<TimeUnitEntry, TimeUnitEntry> {
        lock.lock()
        if (hasReadPersistTime || (hasValidStartTime && hasValidEndTime)) {
            lock.unlock()
            return startTime to endTime
        }
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
        val tempStartTime = if (startYear < 0) TimeUnitEntry.zero()
        else TimeUnitEntry(startYear, startMonth, startDay, startHour, startMinute, startSecond, startMilliSecond, startMicroSecond)
        val tempEndTime = if (endYear < 0) TimeUnitEntry.fromLocalDateTime(LocalDateTime.now())
        else TimeUnitEntry(endYear, endMonth, endDay, endHour, endMinute, endSecond, endMilliSecond, endMicroSecond)
        startTime = tempStartTime
        endTime = tempEndTime
        hasReadPersistTime = tempStartTime.year >= 0 && tempEndTime.year >= 0
        lock.unlock()
        return startTime to endTime
    }

    suspend fun setTime(startTimeEntry: TimeUnitEntry, endTimeEntry: TimeUnitEntry) {
        lock.lock()
        setStartTime(startTimeEntry)
        setEndTime(endTimeEntry)
        lock.unlock()
    }

    private suspend fun setStartTime(timeUnitEntry: TimeUnitEntry) {
        startTime = timeUnitEntry
        hasValidStartTime = true
        timeUnitEntry.apply {
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

    private suspend fun setEndTime(timeUnitEntry: TimeUnitEntry) {
        endTime = timeUnitEntry
        hasValidEndTime = true
        timeUnitEntry.apply {
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