package com.jayce.vexis.domain.bo

data class ChessBO(
    val shouldSendRemote: Boolean,
    val type: Int,
    val x: Int,
    val y: Int
)
