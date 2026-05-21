package com.creezen.commontool.bean

import com.creezen.commontool.Config.NIL

data class TransferStatusBean(
    val statusCode: Int,
    val data: String = NIL
)
