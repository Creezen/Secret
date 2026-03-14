package com.jayce.vexis.domain.database.event

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jayce.vexis.domain.bean.EventEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert
    fun insert(eventEntry: EventEntry): Long

    @Query("select * from eventEntry")
    fun getAllEvent(): Flow<List<EventEntry>>

    @Query("select * from eventEntry where id = :eventId")
    fun getEventById(eventId: Long): EventEntry

    @Query("select * from eventEntry where type IN (:type)")
    fun getEventListByType(type: List<Int>): Flow<List<EventEntry>>
}