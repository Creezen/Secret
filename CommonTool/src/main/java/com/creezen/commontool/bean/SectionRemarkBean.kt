package com.creezen.commontool.bean

data class SectionRemarkBean(
    val sectionId: Long,
    val content: String,
    val list: List<RemarkBean>,
)
