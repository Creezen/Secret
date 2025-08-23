package com.jayce.vexis.foundation.bean

import android.util.Log
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class HistoryEntry (
    val time: String,
    val event: String
) {

    companion object {
        const val TAG = "HistoryEntry"
    }

    fun isValid(): Boolean {
        return time.length == 17
    }
    
    fun millisTime(): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
        val localDateTime = LocalDateTime.parse(time, formatter)
        val zoneDateTime = localDateTime.atZone(ZoneId.systemDefault())
        return zoneDateTime.toInstant().toEpochMilli()
    }
}