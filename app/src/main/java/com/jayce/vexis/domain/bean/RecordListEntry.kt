package com.jayce.vexis.domain.bean

import com.creezen.commontool.Config.NIL

data class RecordListEntry(
    val title: String = NIL,
    val time: String = NIL,
    val result: String = NIL,
    val id: Long,
)