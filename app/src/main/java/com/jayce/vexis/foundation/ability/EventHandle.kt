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
import com.creezen.tool.AndroidTool.readPrefs
import com.creezen.tool.AndroidTool.writePrefs
import com.creezen.tool.NetTool
import com.creezen.tool.ThreadTool
import com.jayce.vexis.core.CoreService.Companion.NAME_MESSAGE_SCOPE
import com.jayce.vexis.core.CoreService.Companion.scope
import com.jayce.vexis.core.SessionManager.user
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.core.base.BaseActivity.ActivityCollector.finishAll
import com.jayce.vexis.foundation.bean.ChatEntry
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.LinkedBlockingQueue

object EventHandle {

    private const val CACHE_MESSAGE = "cacheMessage"
    private val dumpMessage = arrayListOf<ChatEntry>()
    private val chatQueue = LinkedBlockingQueue<ChatEntry>()
    private val chatFlow = MutableSharedFlow<TelecomBean>(0, 0, BufferOverflow.SUSPEND)

    fun getUnreadSize() = chatQueue.size

    fun getChatMessage(block: (LinkedBlockingQueue<ChatEntry>) -> Unit) {
        block.invoke(chatQueue)
    }

    fun notifySocket(context: Context) {
        NetTool.sendAckMessage(scope, user().userId) {
            dispatchEvent(context, it)
            return@sendAckMessage true
        }
        listenToChatMessage()
    }

    fun sendFinish() {
        chatQueue.put(ChatEntry(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING))
    }

    private suspend fun dispatchEvent(context: Context, message: String) {
        val telecomMsg = message.toBean<TelecomBean>() ?: return
        Log.d("LJW", "type: ${telecomMsg.type}  content: ${telecomMsg.content}")
        when (telecomMsg.type) {
            EVENT_TYPE_MESSAGE -> {
                chatFlow.emit(telecomMsg)
            }
            EVENT_TYPE_NOTIFY -> {
                broadcastByAction(context, BROAD_NOTIFY) {
                    it.putExtra("broadcastNotify", telecomMsg.content)
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

    fun initChatData() {
        val data = readPrefs {
            getString(CACHE_MESSAGE, ArrayList<ChatEntry>().toJson())
        }
        chatQueue.clear()
        data?.toBean<ArrayList<ChatEntry>>().let {
            it?.forEach {
                chatQueue.put(it)
                dumpMessage.add(it)
            }
        }
    }

    private fun listenToChatMessage() {
        ThreadTool.runOnSpecific(NAME_MESSAGE_SCOPE) {
            chatFlow.collect {
                val item = ChatEntry(it.nickName, System.currentTimeMillis().toTime(), it.content)
                chatQueue.put(item)
                dumpMessage.add(item)
            }
        }
    }

    fun saveChatMessage() {
        writePrefs { sp ->
            sp.putString(CACHE_MESSAGE, dumpMessage.toJson())
        }
    }
}