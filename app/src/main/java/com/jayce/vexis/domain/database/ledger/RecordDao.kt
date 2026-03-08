package com.jayce.vexis.domain.database.ledger

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jayce.vexis.domain.bean.RecordItemEntry
import com.jayce.vexis.domain.bean.ScoreEntry

@Dao
interface RecordDao {
    @Insert
    fun insertRecord(recordItemEntry: RecordItemEntry): Long

    @Insert
    fun insertScore(scoreEntry: ScoreEntry)

    @Query("select * from RecordItemEntry")
    fun getRecordList(): List<RecordItemEntry>

    @Query("select * from ScoreEntry where recordId = :id")
    fun getScoreList(id: Long): ScoreEntry
}