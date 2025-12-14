package com.jayce.vexis.foundation.bean

data class DownloadTask(
    val fileId: String,
    val fileName: String,
    val size: Long,
    val time: Long,
    val taskLastCount: Int,
)
