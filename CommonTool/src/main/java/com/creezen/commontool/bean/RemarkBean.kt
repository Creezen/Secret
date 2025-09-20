package com.creezen.commontool.bean

import java.math.BigInteger

data class RemarkBean(
    val articleId: BigInteger,
    val sectionId: Long,
    val userId: String,
    val remarkId: Long,
    val cotent: String,
    val favor: Long,
    val createTime: BigInteger
)
