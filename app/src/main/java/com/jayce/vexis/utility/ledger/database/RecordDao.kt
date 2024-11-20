package com.jayce.vexis.utility.ledger.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jayce.vexis.utility.ledger.bean.RecordItemBean
import com.jayce.vexis.utility.ledger.bean.ScoreBean

@Dao
interface RecordDao {

    @Insert
    fun insertRecord(recordItemBean: RecordItemBean): Long

    @Insert
    fun insertScore(scoreBean: ScoreBean)

    @Query("select * from RecordItemBean")
    fun getRecordList() : List<RecordItemBean>

    @Query("select * from ScoreBean where recordId = :id")
    fun getScoreList(id: Long) : ScoreBean
}