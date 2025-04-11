package com.jayce.vexis.writing.paragraph

data class CommentBean(
    val synergyId: Long,
    val paragraphId: Long,
    val userId: String,
    val commentId: Long,
    val cotent: String,
    val favor: Long,
    val createTime: Long,
)
