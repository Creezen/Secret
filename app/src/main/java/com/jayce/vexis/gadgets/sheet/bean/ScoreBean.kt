package com.jayce.vexis.gadgets.sheet.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(RecordItemBean::class, ["id"], ["recordId"])])
data class ScoreBean(
    @PrimaryKey
    @ColumnInfo("recordId")
    var recordId: Long = 0,
    val userList: String,
    val scoreList: String,
    val totalStr: String,
)
