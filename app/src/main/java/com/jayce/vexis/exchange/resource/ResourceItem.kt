package com.jayce.vexis.exchange.resource

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResourceItem(
    val fileName: String,
    val fileID: String,
    val fileSuffix: String,
    val description: String,
    val illustrate: String,
    val fileSize: Long,
    val uploadTime: String,
) : Parcelable