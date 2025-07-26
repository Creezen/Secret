package com.jayce.vexis.business.kit.ledger.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecordItemBean(
    val title: String,
    val time: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
