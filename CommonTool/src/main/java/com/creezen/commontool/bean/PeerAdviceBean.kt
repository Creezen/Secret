package com.creezen.commontool.bean

data class PeerAdviceBean(
    val primary: String,
    val second: String,
    val tertiary: String,
    val content: String = ""
)
