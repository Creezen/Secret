package com.jayce.vexis.domain.bo.book

import com.jayce.vexis.util.Config.NIL

data class BookArchiveBO(
    val title: String = NIL,
    val time: String = NIL,
    val result: String = NIL,
    val id: Long,
)