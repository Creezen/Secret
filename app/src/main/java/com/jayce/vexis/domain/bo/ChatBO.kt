package com.jayce.vexis.domain.bo

data class ChatBO(
    val nickname: String,
    val time: String,
    val msg: String,
    val id: Long,
    var isRead: Boolean
)