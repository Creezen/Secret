package com.creezen.commontool.bean

import com.creezen.commontool.Config.Constant.EMPTY_STRING

data class FileBean(
    val fileName: String = EMPTY_STRING,
    val fileID: String = EMPTY_STRING,
    val fileSuffix: String = EMPTY_STRING,
    val description: String = EMPTY_STRING,
    val illustrate: String = EMPTY_STRING,
    val fileSize: Long = 0,
    val uploadTime: String = EMPTY_STRING,
    var fileHash: String? = EMPTY_STRING
)