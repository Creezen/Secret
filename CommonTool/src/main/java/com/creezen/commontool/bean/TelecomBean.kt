package com.creezen.commontool.bean

import com.creezen.commontool.Config.Constant.EMPTY_STRING

data class TelecomBean (
    val type: Int,
    val userId: String,
    val nickName: String,
    val session: String,
    val msgId: String = "-1",
    val content: String = EMPTY_STRING
)
