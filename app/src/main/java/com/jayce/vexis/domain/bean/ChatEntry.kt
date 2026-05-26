package com.jayce.vexis.domain.bean

data class ChatEntry(
    val nickname: String,
    val time: String,
    val msg: String,
    val id: Long,
    var isRead: Boolean
)