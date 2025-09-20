package com.creezen.commontool

import com.creezen.commontool.Config.Constant.BASIC_LETTER
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.TimeZone

fun getRandomString(length: Int) : String {
    val random = Random()
    val buffer = StringBuffer()
    for (i in 0 until length) {
        buffer.append(BASIC_LETTER[random.nextInt(52)])
    }
    return buffer.toString()
}

fun Long.toTime(formater: String = "yyyy-MM-dd HH:mm:ss"):String{
    val simpleDateFormat= SimpleDateFormat(formater, Locale.CHINA)
    simpleDateFormat.timeZone= TimeZone.getTimeZone("GMT+8")
    if (formater.isNotEmpty()) return simpleDateFormat.format(Date(this))
    return ""
}

inline fun <reified T> String.toBean(): T? {
    runCatching {
        return Gson().fromJson(this, T::class.java)
    }.onFailure {
        it.printStackTrace()
    }
    return null
}

fun Any.toJson(): String? {
    runCatching {
        return Gson().toJson(this)
    }.onFailure {
       it.printStackTrace()
    }
    return null
}

fun isLeapYear(year: Int) = when {
    year % 100 == 0 -> year % 400 == 0
    year % 4 == 0 -> true
    else -> false
}