package com.jayce.vexis.domain.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(BookLineEntry::class, ["id"], ["recordId"])])
data class BookEntry(
    @PrimaryKey
    @ColumnInfo("recordId")
    var recordId: Long = 0,
    val userList: String,
    val scoreList: String,
    val totalStr: String,
)