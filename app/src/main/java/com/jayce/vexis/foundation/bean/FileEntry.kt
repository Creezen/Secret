package com.jayce.vexis.foundation.bean

import android.os.Parcelable
import com.creezen.commontool.Config.Constant.EMPTY_STRING
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileEntry(
    val fileName: String,
    val fileID: String,
    val fileSuffix: String,
    val description: String,
    val illustrate: String,
    val fileSize: Long,
    val uploadTime: String,
    val fileHash: String = EMPTY_STRING
) : Parcelable