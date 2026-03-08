package com.jayce.vexis.foundation.ability

import android.content.Context
import android.util.Log
import com.creezen.commontool.Config.BROAD_NOTIFY
import com.creezen.commontool.Config.Constant.EMPTY_STRING
import com.creezen.commontool.Config.EventType.EVENT_TYPE_DEFAULT
import com.creezen.commontool.Config.EventType.EVENT_TYPE_FINISH
import com.creezen.commontool.Config.EventType.EVENT_TYPE_GAME
import com.creezen.commontool.Config.EventType.EVENT_TYPE_MESSAGE
import com.creezen.commontool.Config.EventType.EVENT_TYPE_NOTIFY
import com.creezen.commontool.bean.TelecomBean
import com.creezen.commontool.toBean
import com.creezen.commontool.toJson
import com.creezen.commontool.toTime
import com.creezen.tool.AndroidTool.broadcastByAction
import com.creezen.tool.AndroidTool.getData
import com.creezen.tool.AndroidTool.putDataAsync
import com.creezen.tool.NetTool
import com.creezen.tool.NetTool.registerSocket
import com.creezen.tool.ThreadTool
import com.jayce.vexis.core.SessionManager.BASE_SOCKET_PATH
import com.jayce.vexis.core.SessionManager.LOCAL_SOCKET_PORT
import com.jayce.vexis.core.SessionManager.liveUser
import com.jayce.vexis.core.base.BaseActivity.ActivityCollector.finishAll
import com.jayce.vexis.domain.bean.ChatEntry
import com.jayce.vexis.domain.bean.EventEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.net.Socket
import java.util.concurrent.LinkedBlockingQueue

object EventHandle {

    const val NAME_MESSAGE_SCOPE = "MESSAGE"
    private const val CACHE_MESSAGE = "cacheMessage"
    private val dumpMessage = arrayListOf<ChatEntry>()
    private val chatQueue = LinkedBlockingQueue<ChatEntry>()
    private val chatFlow = MutableSharedFlow<EventEntry>(0, 0, BufferOverflow.SUSPEND)

    fun initEventSystem(context: Context) {
        val mScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        ThreadTool.registerScope(NAME_MESSAGE_SCOPE, mScope)
        ThreadTool.runOnSpecific(NAME_MESSAGE_SCOPE) {
            preloadChatData()
            val socket = Socket(BASE_SOCKET_PATH, LOCAL_SOCKET_PORT)
            registerSocket(socket, true)
            start(mScope, context)
        }.onFailure {
            Log.d("LJW", "runOnSpecific error: ${it.message}")
        }
    }

    fun releaseEventSystem() {
        ThreadTool.unregisterScope(NAME_MESSAGE_SCOPE)
    }

    private fun start(scope: CoroutineScope, context: Context) {
        NetTool.connect(scope, liveUser.userId) {
            dispatchEvent(context, it)
            return@connect true
        }
        observeChatMessage()
    }

    private suspend fun dispatchEvent(context: Context, message: String) {
        val telecomMessage = message.toBean<EventEntry>() ?: return
        Log.d("LJW", "type: ${telecomMessage.type}  content: ${telecomMessage.content}")
        when (telecomMessage.type) {
            EVENT_TYPE_MESSAGE -> {
                chatFlow.emit(telecomMessage)
            }
            EVENT_TYPE_NOTIFY -> {
                broadcastByAction(context, BROAD_NOTIFY) {
                    it.putExtra("broadcastNotify", telecomMessage.content)
                }
            }
            EVENT_TYPE_DEFAULT -> {}
            EVENT_TYPE_GAME -> {}
            EVENT_TYPE_FINISH -> {
                finishAll()
            }
            else -> {}
        }
    }

    private suspend fun preloadChatData() {
        val cacheData = getData(CACHE_MESSAGE, ArrayList<ChatEntry>().toJson())
        val cacheDataList = cacheData.toBean<ArrayList<ChatEntry>>() ?: return
        chatQueue.clear()
        cacheDataList.forEach {
            chatQueue.put(it)
            dumpMessage.add(it)
        }
    }

    private fun observeChatMessage() {
        ThreadTool.runOnSpecific(NAME_MESSAGE_SCOPE) {
            chatFlow.collect {
                val item = ChatEntry(it.nickName, System.currentTimeMillis().toTime(), it.content)
                chatQueue.put(item)
                dumpMessage.add(item)
            }
        }
    }

    fun sendFinish() {
        chatQueue.put(ChatEntry(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING))
    }

    fun saveChatMessage() {
        putDataAsync(CACHE_MESSAGE, dumpMessage.toJson()) {}
    }

    fun getUnreadSize() = chatQueue.size

    fun getChatMessage(block: (LinkedBlockingQueue<ChatEntry>) -> Unit) {
        block.invoke(chatQueue)
    }
}