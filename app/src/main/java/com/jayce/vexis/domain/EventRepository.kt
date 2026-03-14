package com.jayce.vexis.domain

import android.util.Log
import com.creezen.commontool.Config.BROAD_NOTIFY
import com.creezen.commontool.Config.EventType.EVENT_TYPE_DEFAULT
import com.creezen.commontool.Config.EventType.EVENT_TYPE_FINISH
import com.creezen.commontool.Config.EventType.EVENT_TYPE_GAME
import com.creezen.commontool.Config.EventType.EVENT_TYPE_MESSAGE
import com.creezen.commontool.Config.EventType.EVENT_TYPE_NOTIFY
import com.creezen.tool.AndroidTool.broadcastByAction
import com.creezen.tool.BaseTool
import com.jayce.vexis.core.base.BaseActivity.ActivityCollector.finishAll
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
        val id = eventDao.insert(eventEntry)
        dispatchEvent(id)
    }

    fun getAllEvent(): Flow<List<EventEntry>> {
        return eventDao.getAllEvent()
    }

    fun getChatEventList(): Flow<List<EventEntry>> {
        return eventDao.getEventListByType(listOf(EVENT_TYPE_MESSAGE))
    }

    private suspend fun dispatchEvent(eventId: Long) {
        val event = eventDao.getEventById(eventId)
        Log.d("LJW", "event: $event")
        when (event.type) {
            EVENT_TYPE_MESSAGE -> { _flow.emit(event) }
            EVENT_TYPE_NOTIFY -> {
                broadcastByAction(BaseTool.env(), BROAD_NOTIFY) {
                    it.putExtra("broadcastNotify", event.content)
                }
            }
            EVENT_TYPE_DEFAULT -> {}
            EVENT_TYPE_GAME -> {}
            EVENT_TYPE_FINISH -> { finishAll() }
            else -> {}
        }
    }
}