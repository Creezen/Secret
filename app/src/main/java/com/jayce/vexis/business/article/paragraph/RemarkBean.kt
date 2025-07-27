package com.jayce.vexis.business.article.paragraph

data class RemarkBean(
    val synergyId: Long,
    val paragraphId: Long,
    val userId: String,
    val commentId: Long,
    val cotent: String,
    val favor: Long,
    val createTime: Long,
)