package com.jayce.vexis.domain.enums

import com.jayce.vexis.util.*
import com.jayce.vexis.util.Config.BASE_FACTOR_HUNDRED
import com.jayce.vexis.util.Config.BASE_FACTOR_ONE
import com.jayce.vexis.util.Config.BASE_FACTOR_TEN
import com.jayce.vexis.util.Config.DAY
import com.jayce.vexis.util.Config.HOUR
import com.jayce.vexis.util.Config.MICRO_SECOND
import com.jayce.vexis.util.Config.MILLI_SECOND
import com.jayce.vexis.util.Config.MINUTE
import com.jayce.vexis.util.Config.SECOND
import com.jayce.vexis.util.Config.TIME_LEVEL_DAY
import com.jayce.vexis.util.Config.TIME_LEVEL_HOUR
import com.jayce.vexis.util.Config.TIME_LEVEL_HUNDRED_MICRO_SECOND
import com.jayce.vexis.util.Config.TIME_LEVEL_HUNDRED_MILLI_SECOND
import com.jayce.vexis.util.Config.TIME_LEVEL_HUNDRED_YEAR
import com.jayce.vexis.util.Config.TIME_LEVEL_MILLI_SECOND
import com.jayce.vexis.util.Config.TIME_LEVEL_MINUTE
import com.jayce.vexis.util.Config.TIME_LEVEL_MONTH
import com.jayce.vexis.util.Config.TIME_LEVEL_SECOND
import com.jayce.vexis.util.Config.TIME_LEVEL_TEN_MILLI_SECOND
import com.jayce.vexis.util.Config.TIME_LEVEL_TEN_YEAR
import com.jayce.vexis.util.Config.TIME_LEVEL_THOUSAND_YEAR
import com.jayce.vexis.util.Config.TIME_LEVEL_YEAR
import com.jayce.vexis.util.Config.YEAR_BASE
import com.jayce.vexis.util.Config.YEAR_BASE_HUNDRED
import com.jayce.vexis.util.Config.YEAR_BASE_TEN
import com.jayce.vexis.domain.bean.TimeUnitEntry

enum class TimeLevel(val duration: Long, val plusFactor: Int, val nextLevel: Int) {

    LEVEL_TEN_MICRO_SECOND(MICRO_SECOND, BASE_FACTOR_ONE, TIME_LEVEL_HUNDRED_MICRO_SECOND){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.microSecond % 10 ==  0
            val title = if (isLevelTitle) {
                "${time.microSecond}μs"
            } else {
                "${time.microSecond % 10}μs"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusMicro(num)
    },

    LEVEL_HUNDRED_MICRO_SECOND(BASE_FACTOR_TEN * MICRO_SECOND, BASE_FACTOR_TEN, TIME_LEVEL_MILLI_SECOND){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.microSecond % 100  == 0
            val title = if (isLevelTitle) {
                "${time.microSecond / 100 * 100}μs"
            } else {
                "${time.microSecond % 100}"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            time.roundMicroSecond(10)
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusMicro(num)
    },

    LEVEL_MILLI_SECOND(BASE_FACTOR_HUNDRED * MICRO_SECOND, BASE_FACTOR_HUNDRED, TIME_LEVEL_TEN_MILLI_SECOND){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.microSecond == 0
            val title = if (isLevelTitle) "${time.milliSecond}ms"
            else "${time.microSecond}μs"
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            time.roundMicroSecond(100)
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusMicro(num)
    },

    LEVEL_TEN_MILLI_SECOND(MILLI_SECOND, BASE_FACTOR_ONE, TIME_LEVEL_HUNDRED_MILLI_SECOND){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.milliSecond % 10 == 0
            val title = if (isLevelTitle) {
                "${time.milliSecond / 10 * 10}ms"
            } else {
                "${time.milliSecond % 10}ms"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            if (time.microSecond > 0) {
                time.plusMillis(1)
                time.microSecond = 0
            }
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusMillis(num)
    },

    LEVEL_HUNDRED_MILLI_SECOND(BASE_FACTOR_TEN * MILLI_SECOND, BASE_FACTOR_TEN, TIME_LEVEL_SECOND){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.milliSecond % 100 == 0
            val title = if (isLevelTitle) {
                "${time.milliSecond / 100 * 100}ms"
            } else {
                "${time.milliSecond % 100}ms"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            time.roundMilliSecond(10)
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusMillis(num)
    },

    LEVEL_SECOND(BASE_FACTOR_HUNDRED * MILLI_SECOND, BASE_FACTOR_HUNDRED, TIME_LEVEL_MINUTE){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle =time.milliSecond == 0
            val title = if (isLevelTitle) {
                "${time.second}s"
            } else {
                "${time.milliSecond}ms"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            time.roundMilliSecond(100)
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusMillis(num)
    },

    LEVEL_MINUTE(SECOND, BASE_FACTOR_ONE, TIME_LEVEL_HOUR){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.second == 0
            val title = if (isLevelTitle) {
                "${time.minute}分"
            } else {
                "${time.second}s"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            if (time.milliSecond > 0) {
                time.plusSecond(1)
                time.milliSecond = 0
            }
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusSecond(num)
    },

    LEVEL_HOUR(MINUTE, BASE_FACTOR_ONE, TIME_LEVEL_DAY){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.minute == 0
            val title = if (isLevelTitle) {
                "${time.hour}时"
            } else {
                "${time.minute}分"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            if (time.second > 0) {
                time.plusMinute(1)
                time.second = 0
            }
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusMinute(num)
    },

    LEVEL_DAY(HOUR, BASE_FACTOR_ONE, TIME_LEVEL_MONTH){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.hour == 0
            val title = if (isLevelTitle) {
                "${time.day}日"
            } else {
                "${time.hour}时"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            if (time.minute > 0) {
                time.plusHour(1)
                time.minute = 0
            }
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusHour(num)
    },

    LEVEL_MONTH(DAY, BASE_FACTOR_ONE, TIME_LEVEL_YEAR){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.day == 1
            val title = if (isLevelTitle) {
                "${time.month}月"
            } else {
                "${time.day}日"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            if (time.hour > 0) {
                time.plusDay(1)
                time.hour = 0
            }
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusDay(num)
    },

    LEVEL_YEAR(-1, BASE_FACTOR_ONE, TIME_LEVEL_TEN_YEAR){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.month == 1
            val title = if (isLevelTitle) {
                "${time.year}年"
            } else {
                "${time.month}月"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            if (time.day > 1) {
                time.plusMonth(1)
                time.day = 1
            }
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusMonth(num)
    },

    LEVEL_TEN_YEAR(YEAR_BASE, BASE_FACTOR_ONE, TIME_LEVEL_HUNDRED_YEAR){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.year % 10 == 0
            val title = if (isLevelTitle) {
                "${time.year / 10 * 10}年"
            } else {
                "${time.year % 10}年"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            if (time.month > 1) {
                time.plusYear(1)
                time.month = 1
            }
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusYear(num)
    },

    LEVEL_HUNDRED_YEAR(YEAR_BASE_TEN, BASE_FACTOR_TEN, TIME_LEVEL_THOUSAND_YEAR){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.year % 100 == 0
            val title = if (isLevelTitle) {
                "${time.year / 100 * 100}年"
            } else {
                "${time.year % 100}年"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            time.roundYear(10)
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusYear(num)
    },

    LEVEL_THOUSAND_YEAR(YEAR_BASE_HUNDRED, BASE_FACTOR_HUNDRED, -1){
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> {
            val isLevelTitle = time.year % 1000 == 0
            val title = if (isLevelTitle) {
                "${time.year / 1000 * 1000}年"
            } else {
                "${time.year % 1000}年"
            }
            return title to isLevelTitle
        }

        override fun roundTime(time: TimeUnitEntry): TimeUnitEntry {
            of(ordinal - 1).roundTime(time)
            time.roundYear(100)
            return time
        }

        override fun plus(time: TimeUnitEntry, num: Long) = time.plusYear(num)
    },

    LEVEL_NONE(-1, -1, -1) {
        override fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean> = "" to false

        override fun roundTime(time: TimeUnitEntry) = time

        override fun plus(time: TimeUnitEntry, num: Long) { /**/ }
    };

    /**
     * @param time: 传入的数据，会改变原实例的值
     * @return 返回title文本，和当前的title是否主level
     */
    abstract fun levelTitle(time: TimeUnitEntry): Pair<String, Boolean>

    /**
     * @param time: 传入的数据，会改变原实例的值
     */
    abstract fun roundTime(time: TimeUnitEntry): TimeUnitEntry

    /**
     * @param time: 时间值，会改变原实例
     * @param num: 需要添加的值
     */
    abstract fun plus(time: TimeUnitEntry, num: Long)

    companion object {
        fun of(level: Int) = entries[level]
    }
}