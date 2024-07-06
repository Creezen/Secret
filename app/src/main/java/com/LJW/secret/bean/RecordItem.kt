package com.ljw.secret.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RecordItem(val time: String, val scores: ArrayList<Int>) : Parcelable