package com.jayce.vexis.utility.ledger.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jayce.vexis.utility.ledger.bean.RecordItemBean

@Entity(foreignKeys = [ForeignKey(RecordItemBean::class, ["id"], ["recordId"])])
data class ScoreBean (
    @PrimaryKey
    @ColumnInfo("recordId")
    var recordId: Long = 0,
    val userList: String,
    val scoreList: String,
    val totalStr: String
)