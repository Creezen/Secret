package com.jayce.vexis.foundation.bean

data class RemarkEntry(
    val synergyId: Long,
    val paragraphId: Long,
    val userId: String,
    val commentId: Long,
    val cotent: String,
    val favor: Long,
    val createTime: Long,
)