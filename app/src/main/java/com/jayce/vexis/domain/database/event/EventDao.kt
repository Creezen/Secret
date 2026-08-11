package com.jayce.vexis.domain.database.event

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert
    fun insert(eventEntity: EventEntity): Long

    @Query("select * from eventEntry")
    fun getAllEvent(): Flow<List<EventEntity>>

    @Query("select * from eventEntry where id = :eventId")
    fun getEventById(eventId: Long): EventEntity

    @Query("select * from eventEntry where type IN (:type)")
    fun getEventListByType(type: List<Int>): Flow<List<EventEntity>>

    @Query("select count(*) from eventEntry where type IN (:type) and isRead = false")
    fun getEventCountByType(type: List<Int>): Int

    @Query("update eventEntry set isRead = true where id = :eventId")
    fun markEventAsRead(eventId: Long)
}