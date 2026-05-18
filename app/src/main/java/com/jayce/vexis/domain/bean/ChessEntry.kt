package com.jayce.vexis.domain.bean

data class ChessEntry(
    val shouldSendRemote: Boolean,
    val type: Int,
    val x: Int,
    val y: Int
)
