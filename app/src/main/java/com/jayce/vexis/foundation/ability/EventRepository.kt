package com.jayce.vexis.foundation.ability

import android.app.Notification
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.jayce.vexis.util.Config.EVENT_TYPE_CHAT
import com.jayce.vexis.util.Config.EVENT_TYPE_DEFAULT
import com.jayce.vexis.util.Config.EVENT_TYPE_FEEDBACK
import com.jayce.vexis.util.Config.EVENT_TYPE_ROLE
import com.jayce.vexis.util.toBean
import com.jayce.vexis.client.BaseTool.envContext
import com.jayce.vexis.client.NetTool
import com.jayce.vexis.client.NetTool.registerSocket
import com.jayce.vexis.client.TLog
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.client.ThreadTool.registerScope
import com.jayce.vexis.client.ThreadTool.runOnSpecific
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
        const val SCOPE_EVENT = "MESSAGE"
    }

    private val eventDao = EventDatabase.getDatabase(envContext).eventDao()

    private val _chatEventFlow: MutableSharedFlow<EventEntry> = MutableSharedFlow(0, 256, BufferOverflow.SUSPEND)
    val chatEventFlow: SharedFlow<EventEntry> = _chatEventFlow.asSharedFlow()

    private val _mailEventFlow: MutableSharedFlow<EventEntry> = MutableSharedFlow(0, 256, BufferOverflow.SUSPEND)
    val mailEventFlow: SharedFlow<EventEntry> = _mailEventFlow.asSharedFlow()

    private val notificationTitleMap = mapOf(
        EVENT_TYPE_FEEDBACK to "反馈通知",
        EVENT_TYPE_ROLE to "用户管理通知"

    )

    private val chatTypeList = listOf(EVENT_TYPE_CHAT)
    private val mailTypeList = listOf(EVENT_TYPE_FEEDBACK, EVENT_TYPE_ROLE)

    fun initEventSystem() {
        val mScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        registerScope(SCOPE_EVENT, mScope)
        runOnSpecific(SCOPE_EVENT) {
            val socket = Socket(BASE_SOCKET_PATH, LOCAL_SOCKET_PORT)
            registerSocket(socket, true)
            NetTool.connect(mScope, liveUser.userId) {
                val message = it.toBean<EventEntry>() ?: return@connect true
                insertEvent(message)
                return@connect true
            }
        }.onFailure {
            TLog.d("runOnSpecific error: ${it.message}")
        }
    }

    fun releaseEventSystem() {
        ThreadTool.unregisterScope(SCOPE_EVENT)
    }

    private suspend fun insertEvent(eventEntry: EventEntry) {
        val id = eventDao.insert(eventEntry)
        dispatchEvent(id)
    }

    fun getAllEvent(): Flow<List<EventEntry>> {
        return eventDao.getAllEvent()
    }

    fun getChatEvent(): Flow<List<EventEntry>> {
        return eventDao.getEventListByType(chatTypeList)
    }

    fun getUnreadChatCount(): Int {
        return eventDao.getEventCountByType(chatTypeList)
    }

    fun getMailEvent(): Flow<List<EventEntry>> {
        return eventDao.getEventListByType(mailTypeList)
    }

    fun getUnreadMailCount(): Int {
        return eventDao.getEventCountByType(mailTypeList)
    }

    private suspend fun dispatchEvent(eventId: Long) {
        val event = eventDao.getEventById(eventId)
        TLog.d("event: $event")
        when (event.type) {
            EVENT_TYPE_CHAT -> _chatEventFlow.emit(event)
            EVENT_TYPE_FEEDBACK,
            EVENT_TYPE_ROLE -> {
                _mailEventFlow.emit(event)
                sendNotification(event.type, event.content)
            }
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