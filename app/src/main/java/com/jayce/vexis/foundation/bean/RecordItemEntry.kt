package com.jayce.vexis.foundation.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecordItemEntry(
    val title: String,
    val time: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}