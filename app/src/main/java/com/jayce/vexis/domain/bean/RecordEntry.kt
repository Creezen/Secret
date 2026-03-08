package com.jayce.vexis.domain.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

@Parcelize
data class RecordEntry(
    val time: String,
    val scores: ArrayList<Int>,
) : Parcelable