package com.jayce.vexis.domain.database.book

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jayce.vexis.domain.bean.book.BookLineEntry
import com.jayce.vexis.domain.bean.book.BookEntry

@Dao
interface RecordDao {
    @Insert
    fun insertRecord(bookLineEntry: BookLineEntry): Long

    @Insert
    fun insertScore(bookEntry: BookEntry)

    @Query("select * from BookLineEntry")
    fun getBookList(): List<BookLineEntry>

    @Query("select * from BookEntry where recordId = :id")
    fun getScoreList(id: Long): BookEntry
}