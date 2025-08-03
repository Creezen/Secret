package com.jayce.vexis.foundation.bean

data class ParaRemarkEntry(
    val paragraphId: Long,
    val content: String,
    val list: List<RemarkEntry>,
)