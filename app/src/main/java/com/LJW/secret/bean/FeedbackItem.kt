package com.ljw.secret.bean

data class FeedbackItem(
    val userID: String,
    val nickname: String,
    val time: String,
    val title: String,
    val content: String,
    val support: Long,
    val against: Long
)