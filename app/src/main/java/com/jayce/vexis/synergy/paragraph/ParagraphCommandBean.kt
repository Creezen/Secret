package com.jayce.vexis.synergy.paragraph

data class ParagraphCommandBean(
    val paragraphId: Long,
    val content: String,
    val list: List<CommentBean>
)
