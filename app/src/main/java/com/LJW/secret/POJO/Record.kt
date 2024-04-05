package com.ljw.secret.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Record(val round: String, val score: ArrayList<Int>) : Parcelable