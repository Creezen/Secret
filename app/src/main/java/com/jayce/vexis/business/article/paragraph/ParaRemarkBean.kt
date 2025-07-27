package com.jayce.vexis.business.article.paragraph

data class ParaRemarkBean(
    val paragraphId: Long,
    val content: String,
    val list: List<RemarkBean>,
)