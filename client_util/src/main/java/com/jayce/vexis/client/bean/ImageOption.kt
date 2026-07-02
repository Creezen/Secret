package com.jayce.vexis.client.bean

import android.graphics.drawable.Drawable

data class ImageOption(
    val isCircle: Boolean = false,
    val key: String? = null,
    val basePath: String = "",
    val useThumbnail: Boolean = false,
    val placeHolderId: Int? = null,
    val placeHolder: Drawable? = null
)
