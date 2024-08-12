package com.jayce.vexis.utility.ledger

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RecordBean(val time: String, val scores: ArrayList<Int>) : Parcelable