package com.jayce.vexis.domain.bean

import com.jayce.vexis.domain.enums.TimeLevel
import com.jayce.vexis.util.getMaxDayOfMonth
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId

data class TimeUnitEntry(
    var year: Int,
    var month: Int,
    var day: Int,
    var hour: Int,
    var minute: Int,
    var second: Int,
    var milliSecond: Int = 0,
    var microSecond: Int = 0
) {
    companion object {
        private const val TIME_OFFSET = 62167248343000L

        fun totalMilliSecond(timeStamp: Long) = timeStamp + TIME_OFFSET

        fun zero() = TimeUnitEntry(0, 1, 1, 0, 0, 0, 0, 0)

        fun now(): TimeUnitEntry = fromLocalDateTime(LocalDateTime.now())

        fun fromLocalDateTime(localDateTime: LocalDateTime): TimeUnitEntry {
            val year = localDateTime.year
            val month = localDateTime.monthValue
            val day = localDateTime.dayOfMonth
            val hour = localDateTime.hour
            val minute = localDateTime.minute
            val second = localDateTime.second
            val nano = localDateTime.nano
            val micros = nano / 1000
            val microSecond = micros % 1000
            val milliSecond = micros / 1000
            return TimeUnitEntry(year, month, day, hour, minute, second, milliSecond, microSecond)
        }
    }

    fun diff(other: TimeUnitEntry): Pair<TimeUnitEntry, Long> {
        val pair = isLater(other)
        val later = pair.first
        val older = pair.second
        var isNeg = false

        var gapMicroSecond = later.microSecond - older.microSecond
        if (gapMicroSecond < 0) {
            isNeg = true
            gapMicroSecond += 1000
        }

        var gapMilliSecond = later.milliSecond - older.milliSecond
        if (isNeg) {
            gapMilliSecond -= 1
            isNeg = false
        }
        if (gapMilliSecond < 0) {
            isNeg = true
            gapMilliSecond += 1000
        }

        var gapSecond = later.second - older.second
        if (isNeg) {
            gapSecond -= 1
            isNeg = false
        }
        if (gapSecond < 0) {
            isNeg = true
            gapSecond += 60
        }

        var gapMinute = later.minute - older.minute
        if (isNeg) {
            gapMinute -= 1
            isNeg = false
        }
        if (gapMinute < 0) {
            isNeg = true
            gapMinute += 60
        }

        var gapHour = later.hour - older.hour
        if (isNeg) {
            gapHour -= 1
            isNeg = false
        }
        if (gapHour < 0) {
            isNeg = true
            gapHour += 24
        }

        val oldLocalDateTime = older.toLocalDateTime()
        val laterLocalDateTime = later.toLocalDateTime()
        val period = Period.between(oldLocalDateTime.toLocalDate(), laterLocalDateTime.toLocalDate())
        if (isNeg) period.minusDays(1)
        val gapDay = period.days
        val gapMonth = period.months
        val gapYear = period.years

        val duration = Duration.between(oldLocalDateTime, laterLocalDateTime)
        val gapTimestamp = duration.seconds * 1_000_000 + duration.nano / 1_000

        return TimeUnitEntry(
            gapYear,
            gapMonth,
            gapDay,
            gapHour,
            gapMinute,
            gapSecond,
            gapMilliSecond,
            gapMicroSecond
        ) to gapTimestamp
    }

    private fun isLater(that: TimeUnitEntry): Pair<TimeUnitEntry, TimeUnitEntry> {
        if (year != that.year) {
            return if (year > that.year) this to that else that to this
        }
        if (month != that.month) {
            return if (month > that.month) this to that else that to this
        }
        if (day != that.day) {
            return if (day > that.day) this to that else that to this
        }
        if (hour != that.hour) {
            return if (hour > that.hour) this to that else that to this
        }
        if (minute != that.minute) {
            return if (minute > that.minute) this to that else that to this
        }
        if (second != that.second) {
            return if (second > that.second) this to that else that to this
        }
        if (milliSecond != that.milliSecond) {
            return if (milliSecond > that.milliSecond) this to that else that to this
        }
        if (microSecond != that.microSecond) {
            return if (microSecond > that.microSecond) this to that else that to this
        }
        return this to this
    }

    fun formatString(char: Char, length: Int): String {
        val padYear = "$year".padStart(4, char)
        val padMonth = "$month".padStart(2, char)
        val padDay = "$day".padStart(2, char)
        val threePartString = "$padYear$padMonth$padDay"
        if (length == 3) return threePartString
        val padHour = "$hour".padStart(2, char)
        val padMinute = "$minute".padStart(2, char)
        val padSecond = "$second".padStart(2, char)
        val padMilliSecond = "$milliSecond".padStart(3, char)
        val sevenPartString = "$threePartString$padHour$padMinute$padSecond$padMilliSecond"
        if (length == 7) return sevenPartString
        val padMicroSecond = "$microSecond".padStart(3, char)
        return "$sevenPartString$padMicroSecond"
    }

    fun formatString(length: Int) = when (length) {
        3 -> "$year - $month - $day"
        7 -> {
            val milliSeconds = "$milliSecond".padStart(3, '0')
            "$year-$month-$day  $hour:$minute:${second}.${milliSeconds}"
        }
        8 -> "$year-$month-$day  $hour:$minute:${second}.${milliSecond}.$microSecond"
        else -> ""
    }

    fun toLocalDateTime(): LocalDateTime {
        val nano = milliSecond * 1_000_000 + microSecond * 1_000
        return LocalDateTime.of(year, month, day, hour, minute, second, nano)
    }

    fun totalMilliSecond() =
        toLocalDateTime()
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli() + TIME_OFFSET

    fun plusMicro(addMicro: Long) {
        val micro = microSecond + addMicro
        if (micro >= 1000) {
            val divider = micro / 1000
            microSecond = (micro % 1000).toInt()
            plusMillis(divider)
        } else microSecond = micro.toInt()
    }

    fun plusMillis(addMillis: Long) {
        val milli = milliSecond + addMillis
        if (milli >= 1000) {
            val divider = milli / 1000
            milliSecond = (milli % 1000).toInt()
            plusSecond(divider)
        } else milliSecond = milli.toInt()
    }

    fun plusSecond(addSecond: Long) {
        val sec = second + addSecond
        if (sec >= 60) {
            val divider = sec / 60
            second = (sec % 60).toInt()
            plusMinute(divider)
        } else second = sec.toInt()
    }

    fun plusMinute(addMinute: Long) {
        val minu = minute + addMinute
        if (minu >= 60) {
            val divider = minu / 60
            minute = (minu % 60).toInt()
            plusHour(divider)
        } else minute = minu.toInt()
    }

    fun plusHour(addHour: Long) {
        val h = hour + addHour
        if (h >= 24) {
            val divider = h / 24
            hour = (h % 24).toInt()
            plusDay(divider)
        } else hour = h.toInt()
    }

    fun plusDay(addDay: Long) {
        var remain = addDay + day
        var maxDays = getMaxDayOfMonth(year, month)
        while (remain > maxDays) {
            remain -= maxDays
            plusMonth(1)
            maxDays = getMaxDayOfMonth(year, month)
        }
        day = remain.toInt()
    }

    fun plusMonth(addMonth: Long) {
        val mon = month + addMonth
        if (mon > 12) {
            val divider = mon / 12
            month = (mon % 12).toInt()
            plusYear(divider)
        } else month = mon.toInt()
    }

    fun plusYear(addYear: Long) {
        year += addYear.toInt()
    }

    fun roundMicroSecond(factor: Int) {
        val new = microSecond / factor * factor
        if (new < microSecond) {
            microSecond = new
            plusMicro(factor.toLong())
        } else  microSecond = new
    }

    fun roundMilliSecond(factor: Int) {
        val new = milliSecond / factor * factor
        if (new < milliSecond) {
            milliSecond = new
            plusMillis(factor.toLong())
        } else milliSecond = new
    }

    fun roundSecond(factor: Int) {
        val new = second / factor * factor
        if (new < second) {
            second = new
            plusMicro(factor.toLong())
        } else second = new
    }

    fun roundMinute(factor: Int) {
        val new = minute / factor * factor
        if (new < minute) {
            minute = new
            plusMicro(factor.toLong())
        } else minute = new
    }

    fun roundHour(factor: Int) {
        val new = hour / factor * factor
        if (new < hour) {
            hour = new
            plusMicro(factor.toLong())
        } else hour = new
    }

    fun roundDay(factor: Int) {
        val new = day / factor * factor
        if (new < day) {
            day = new
            plusMicro(factor.toLong())
        } else day = new
    }

    fun roundMonth(factor: Int) {
        val new = month / factor * factor
        if (new < month) {
            month = new
            plusMicro(factor.toLong())
        } else month = new
    }

    fun roundYear(factor: Int) {
        val new = year / factor * factor
        if (new < year) {
            year = new
            plusMicro(factor.toLong())
        } else year = new
    }

    fun round(level: TimeLevel) = level.roundTime(this)
}