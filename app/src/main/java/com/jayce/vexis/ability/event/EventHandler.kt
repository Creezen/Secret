package com.jayce.vexis.ability.event

import android.content.Context
import android.content.Intent
import android.util.Log
import com.creezen.commontool.CreezenParam.EVENT_TYPE_DEFAULT
import com.creezen.commontool.CreezenParam.EVENT_TYPE_GAME
import com.creezen.commontool.CreezenParam.EVENT_TYPE_MESSAGE
import com.creezen.commontool.CreezenParam.EVENT_TYPE_NOTIFY
import com.creezen.tool.AndroidTool.broadcastByAction
import com.creezen.tool.Constant.BROAD_NOTIFY
import com.creezen.tool.ThreadTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

object EventHandler {
    
    private const val TAG = "EventHandler"

    val chatFlow = MutableSharedFlow<String>(0, 0, BufferOverflow.SUSPEND)

    suspend fun dispatchEvent(message: String, context: Context) {
        val index = message.indexOfFirst { it == '#' }
        val type = message.substring(0, index).toInt()
        val content = message.substring(index + 1, message.length)
        when(type) {
            EVENT_TYPE_DEFAULT -> {
                Log.e(TAG,"default: $content")
            }
            EVENT_TYPE_MESSAGE -> {
                Log.e(TAG,"emit chat: $content")
                chatFlow.emit(content)
            }
            EVENT_TYPE_NOTIFY -> {
                broadcastByAction(context, BROAD_NOTIFY) {
                    it.putExtra("broadcastNotify", content)
                }
                Log.e(TAG,"EVENT_TYPE_NOTIFY")
            }
            EVENT_TYPE_GAME -> {
                Log.e(TAG,"EVENT_TYPE_GAME")
            }
            else -> {
                Log.e(TAG,"unknown message type")
            }
        }
    }

}