package com.creezen.commontool

import com.alibaba.fastjson2.JSON
import com.creezen.commontool.CreezenParam.BASIC_LETTER
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.TimeZone

object CreezenTool {

    fun getRandomString(length: Int) : String {
        val random = Random()
        val buffer = StringBuffer()
        for (i in 0 until length)
            buffer.append(BASIC_LETTER[random.nextInt(52)])
        return buffer.toString()
    }

    fun Long.toTime(formater: String = "yyyy-MM-dd HH:mm:ss"):String{
        val simpleDateFormat= SimpleDateFormat(formater, Locale.CHINA)
        simpleDateFormat.timeZone= TimeZone.getTimeZone("GMT+8")
        if (formater.isNotEmpty()) return simpleDateFormat.format(Date(this))
        return ""
    }

    fun <T> T.pojo2Map()= JSON.parseObject(JSON.toJSONString(this), HashMap::class.java) as HashMap<String,String>

    inline fun <reified T> Map<String,String>.map2pojo():T{
        val gson = Gson()
        return gson.fromJson(gson.toJson(this), T::class.java)
    }

    fun isLeapYear(year: Int) = when {
        year % 100 == 0 -> year % 400 == 0
        year % 4 == 0 -> true
        else -> false
    }
}