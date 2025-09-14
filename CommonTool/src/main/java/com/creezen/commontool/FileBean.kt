package com.creezen.commontool

data class FileBean(
    val fileName: String = "",
    val fileID: String = "",
    val fileSuffix: String = "",
    val description: String = "",
    val illustrate: String = "",
    val fileSize: Long = 0,
    val uploadTime: String = "",
    var fileHash: String? = ""
)