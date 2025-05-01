package com.jayce.vexis.feedback

data class FeedbackItem(
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
