package com.ljw.secret.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RecordItem(val round: String, val score: ArrayList<Int>) : Parcelable