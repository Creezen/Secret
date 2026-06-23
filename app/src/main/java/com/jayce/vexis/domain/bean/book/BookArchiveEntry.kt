package com.jayce.vexis.domain.bean.book

import com.jayce.vexis.util.Config.NIL

data class BookArchiveEntry(
    val title: String = NIL,
    val time: String = NIL,
    val result: String = NIL,
    val id: Long,
)