package com.jayce.vexis.foundation.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecordEntry(
    val time: String,
    val scores: ArrayList<Int>,
) : Parcelable