package com.creezen.commontool.bean

import com.creezen.commontool.Config.NIL

data class TelecomBean (
    val type: Int,
    val userId: String,
    val nickName: String,
    val session: String,
    val time: Long,
    val msgId: String = "-1",
    val content: String = NIL
)
