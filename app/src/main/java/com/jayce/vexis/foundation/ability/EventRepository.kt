package com.jayce.vexis.foundation.ability

import android.app.Notification
import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.creezen.commontool.Config.EVENT_TYPE_CHAT
import com.creezen.commontool.Config.EVENT_TYPE_DEFAULT
import com.creezen.commontool.Config.EVENT_TYPE_FEEDBACK
import com.creezen.commontool.toBean
import com.creezen.tool.BaseTool.envContext
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.registerSocket
import com.creezen.tool.ThreadTool
import com.creezen.tool.ThreadTool.registerScope
import com.creezen.tool.ThreadTool.runOnSpecific
import com.jayce.vexis.R
import com.jayce.vexis.StatusManager.BASE_SOCKET_PATH
import com.jayce.vexis.StatusManager.LOCAL_SOCKET_PORT
import com.jayce.vexis.StatusManager.liveUser
import com.jayce.vexis.domain.bean.EventEntry
import com.jayce.vexis.domain.database.event.EventDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.net.Socket

class EventRepository {

    companion object {
        const val MESSAGE_SCOPE = "MESSAGE"
    }

    private val eventDao = EventDatabase.getDatabase(envContext).eventDao()

    private val _chatEventFlow: MutableSharedFlow<EventEntry> = MutableSharedFlow(0, 256, BufferOverflow.SUSPEND)
    val chatEventFlow: SharedFlow<EventEntry> = _chatEventFlow.asSharedFlow()

    private val notificationTitleMap = mapOf(
        EVENT_TYPE_FEEDBACK to "反馈通知"
    )

    fun initEventSystem() {
        val mScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        registerScope(MESSAGE_SCOPE, mScope)
        runOnSpecific(MESSAGE_SCOPE) {
            val socket = Socket(BASE_SOCKET_PATH, LOCAL_SOCKET_PORT)
            registerSocket(socket, true)
            NetTool.connect(mScope, liveUser.userId) {
                val message = it.toBean<EventEntry>() ?: return@connect true
                insertEvent(message)
                return@connect true
            }
        }.onFailure {
            Log.d("LJW", "runOnSpecific error: ${it.message}")
        }
    }

    fun releaseEventSystem() {
        ThreadTool.unregisterScope(MESSAGE_SCOPE)
    }

    private suspend fun insertEvent(eventEntry: EventEntry) {
        val id = eventDao.insert(eventEntry)
        dispatchEvent(id)
    }

    fun getAllEvent(): Flow<List<EventEntry>> {
        return eventDao.getAllEvent()
    }

    fun getChatEvent(): Flow<List<EventEntry>> {
        return eventDao.getEventListByType(listOf(EVENT_TYPE_CHAT))
    }

    fun getFeedbackEvent(): Flow<List<EventEntry>> {
        return eventDao.getEventListByType(listOf(EVENT_TYPE_FEEDBACK))
    }

    fun getUnreadFeedbackCount(): Int {
        return eventDao.getEventCountByType(listOf(EVENT_TYPE_FEEDBACK))
    }

    fun getUnreadChatCount(): Int {
        return eventDao.getEventCountByType(listOf(EVENT_TYPE_CHAT))
    }

    private suspend fun dispatchEvent(eventId: Long) {
        val event = eventDao.getEventById(eventId)
        Log.d("LJW", "event: $event")
        when (event.type) {
            EVENT_TYPE_CHAT -> _chatEventFlow.emit(event)
            EVENT_TYPE_FEEDBACK -> { sendNotification(event.type, event.content) }
            EVENT_TYPE_DEFAULT -> {}
            else -> {}
        }
    }

    private fun sendNotification(type: Int, message: String) {
        val title = notificationTitleMap[type] ?: return
        val notification = NotificationCompat.Builder(envContext, "message")
            .setSmallIcon(R.mipmap.tianji)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        val manager = envContext.getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }
}