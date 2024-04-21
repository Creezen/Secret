package com.ljw.secret.util

import android.widget.TextView
import android.widget.Toast
import com.ljw.secret.Constant.BASIC_LETTER
import com.ljw.secret.Env
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.TimeZone

object DataUtil {

    fun getRandomString(length:Int):String{
        val random = Random()
        val buffer = StringBuffer()
        for (i in 0 until length)
            buffer.append(BASIC_LETTER[random.nextInt(52)])
        return buffer.toString()
    }

    fun <T> T.toast() {
        Toast.makeText(Env.context,"$this", Toast.LENGTH_LONG).show()
    }

    fun Long.toTime(formater: String = "yyyy-MM-dd HH:mm:ss"):String{
        val simpleDateFormat= SimpleDateFormat(formater, Locale.CHINA)
        simpleDateFormat.timeZone= TimeZone.getTimeZone("GMT+8")
        if (formater.isNotEmpty()) return simpleDateFormat.format(Date(this))
        return ""
    }

    fun TextView.msg(needTrim: Boolean = true): String {
        val value = text.toString()
        return if (needTrim) {
            value.trim()
        } else {
            value
        }
    }

}