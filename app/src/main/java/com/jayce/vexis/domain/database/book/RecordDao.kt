package com.jayce.vexis.domain.database.book

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jayce.vexis.domain.bo.book.BookLineBO

@Dao
interface RecordDao {
    @Insert
    fun insertRecord(bookLineBO: BookLineBO): Long

    @Insert
    fun insertScore(bookEntity: BookEntity)

    @Query("select * from BookLineBO")
    fun getBookList(): List<BookLineBO>

    @Query("select * from BookEntity where recordId = :id")
    fun getScoreList(id: Long): BookEntity
}