package com.jayce.vexis.event

import android.content.Context
import android.content.Intent
import android.util.Log
import com.creezen.commontool.CreezenParam.EVENT_TYPE_DEFAULT
import com.creezen.commontool.CreezenParam.EVENT_TYPE_GAME
import com.creezen.commontool.CreezenParam.EVENT_TYPE_MESSAGE
import com.creezen.commontool.CreezenParam.EVENT_TYPE_NOTIFY
import com.creezen.tool.AndroidTool.broadcastByAction
import com.creezen.tool.Constant.BROAD_NOTIFY

object EventHandler {
    
    private const val TAG = "EventHandler"

    fun dispatchEvent(message: String, context: Context) {
        val index = message.indexOfFirst { it == '#' }
        val type = message.substring(0, index).toInt()
        val content = message.substring(index + 1, message.length)
        when(type) {
            EVENT_TYPE_DEFAULT -> {
                Log.e(TAG,"default: $content")
            }
            EVENT_TYPE_MESSAGE -> {
                Log.e("EventHandler.dispatchEvent","chat: $content")
                context.apply {
                    startService(Intent(this, ChatEventService::class.java).also {
                        it.putExtra("messageContent", content)
                    })
                }
            }
            EVENT_TYPE_NOTIFY -> {
                broadcastByAction(context, BROAD_NOTIFY) {
                    it.putExtra("broadcastNotify", content)
                }
                Log.e("EventHandler.dispatchEvent","EVENT_TYPE_NOTIFY")
            }
            EVENT_TYPE_GAME -> {
                Log.e("EventHandler.dispatchEvent","EVENT_TYPE_GAME")
            }
            else -> {
                Log.e("EventHandler.dispatchEvent","unknown message type")
            }
        }
    }

}