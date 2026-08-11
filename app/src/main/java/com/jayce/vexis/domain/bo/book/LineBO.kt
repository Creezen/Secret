package com.jayce.vexis.domain.bo.book

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

@Parcelize
data class LineBO(
    val time: String,
    val scores: ArrayList<Int>,
) : Parcelable