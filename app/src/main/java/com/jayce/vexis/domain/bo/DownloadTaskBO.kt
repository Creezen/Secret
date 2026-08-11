package com.jayce.vexis.domain.bo

data class DownloadTaskBO(
    val fileId: String,
    val fileName: String,
    val resourceName: String,
    val size: Long,
    val time: Long,
    val taskLastCount: Int,
    val onFinish: (() -> Unit)? = null
)
