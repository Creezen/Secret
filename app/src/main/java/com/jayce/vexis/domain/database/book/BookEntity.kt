package com.jayce.vexis.domain.database.book

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jayce.vexis.domain.bo.book.BookLineBO

@Entity(foreignKeys = [ForeignKey(BookLineBO::class, ["id"], ["recordId"])])
data class BookEntity(
    @PrimaryKey
    @ColumnInfo("recordId")
    var recordId: Long = 0,
    val userList: String,
    val scoreList: String,
    val totalStr: String,
)