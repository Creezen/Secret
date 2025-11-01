package com.creezen.commontool.bean

import com.creezen.commontool.Config.Constant.EMPTY_STRING

data class TransferStatusBean(
    val statusCode: Int,
    val data: String = EMPTY_STRING
)
