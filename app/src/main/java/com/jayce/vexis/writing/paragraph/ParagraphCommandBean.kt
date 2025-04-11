package com.jayce.vexis.writing.paragraph

data class ParagraphCommandBean(
    val paragraphId: Long,
    val content: String,
    val list: List<CommentBean>,
)
