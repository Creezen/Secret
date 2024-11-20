package com.jayce.vexis.gadgets.ledger.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RecordBean(val time: String, val scores: ArrayList<Int>) : Parcelable