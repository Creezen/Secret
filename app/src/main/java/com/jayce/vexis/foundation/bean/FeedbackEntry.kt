package com.jayce.vexis.foundation.bean

data class FeedbackEntry(
    val feedbackID: String,
    val userName: String,
    val userID: String,
    val type: String,
    val title: String,
    val content: String,
    val createTime: Long,
    val support: Long,
    val against: Long,
)