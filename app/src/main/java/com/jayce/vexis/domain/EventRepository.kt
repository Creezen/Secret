package com.jayce.vexis.domain

import com.creezen.tool.BaseTool
import com.jayce.vexis.domain.bean.EventEntry
import com.jayce.vexis.domain.database.event.EventDatabase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class EventRepository {

    private val eventDao by lazy {
        EventDatabase.getDatabase(BaseTool.env()).eventDao()
    }

    private val _flow: MutableSharedFlow<EventEntry> = MutableSharedFlow(0, 256, BufferOverflow.SUSPEND)
    val flow: SharedFlow<EventEntry> = _flow.asSharedFlow()

    suspend fun insertEvent(eventEntry: EventEntry) {
        eventDao.insert(eventEntry)
        _flow.emit(eventEntry)
    }

    fun getAllEvent(): Flow<List<EventEntry>> {
        return eventDao.getAllEvent()
    }

}