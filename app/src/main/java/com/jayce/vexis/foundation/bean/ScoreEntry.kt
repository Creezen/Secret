package com.jayce.vexis.foundation.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(RecordItemEntry::class, ["id"], ["recordId"])])
data class ScoreEntry(
    @PrimaryKey
    @ColumnInfo("recordId")
    var recordId: Long = 0,
    val userList: String,
    val scoreList: String,
    val totalStr: String,
)