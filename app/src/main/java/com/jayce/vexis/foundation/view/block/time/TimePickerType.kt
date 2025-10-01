package com.jayce.vexis.foundation.view.block.time

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