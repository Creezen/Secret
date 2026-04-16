package com.jayce.vexis.domain.enums

enum class TimePickerType {

    SIMPLE,

    FULL;

    companion object {
        fun getType(num: Int) = when(num) {
            0 -> SIMPLE
            1 -> FULL
            else -> FULL
        }
    }
}