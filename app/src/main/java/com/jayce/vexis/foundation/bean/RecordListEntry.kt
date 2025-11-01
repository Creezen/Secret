package com.jayce.vexis.foundation.bean

import com.creezen.commontool.Config.Constant.EMPTY_STRING

data class RecordListEntry(
    val title: String = EMPTY_STRING,
    val time: String = EMPTY_STRING,
    val result: String = EMPTY_STRING,
    val id: Long,
)