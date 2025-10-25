package com.jayce.vexis.foundation.ability

import android.content.Context
import android.util.Log
import com.creezen.commontool.Config.BROAD_NOTIFY
import com.creezen.commontool.Config.EventType.EVENT_TYPE_DEFAULT
import com.creezen.commontool.Config.EventType.EVENT_TYPE_GAME
import com.creezen.commontool.Config.EventType.EVENT_TYPE_MESSAGE
import com.creezen.commontool.Config.EventType.EVENT_TYPE_NOTIFY
import com.creezen.commontool.bean.TelecomBean
import com.creezen.commontool.toBean
import com.creezen.tool.AndroidTool.broadcastByAction
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

object EventHandler {
    private const val TAG = "EventHandler"

    val chatFlow = MutableSharedFlow<String>(0, 0, BufferOverflow.SUSPEND)

    suspend fun dispatchEvent(
        message: String,
        context: Context,
    ) {
        val telecomMsg = message.toBean<TelecomBean>() ?: return
        when (telecomMsg.type) {
            EVENT_TYPE_DEFAULT -> {
                Log.d(TAG, "default: ${telecomMsg.content}")
            }
            EVENT_TYPE_MESSAGE -> {
                Log.d(TAG, "emit chat: ${telecomMsg.content}")
                chatFlow.emit(telecomMsg.content)
            }
            EVENT_TYPE_NOTIFY -> {
                broadcastByAction(context, BROAD_NOTIFY) {
                    it.putExtra("broadcastNotify", telecomMsg.content)
                }
                Log.d(TAG, "EVENT_TYPE_NOTIFY")
            }
            EVENT_TYPE_GAME -> {
                Log.e(TAG, "EVENT_TYPE_GAME")
            }
            else -> {
                Log.e(TAG, "unknown message type")
            }
        }
    }
}