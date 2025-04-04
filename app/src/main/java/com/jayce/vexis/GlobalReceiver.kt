package com.jayce.vexis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.creezen.tool.AndroidTool
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.AndroidTool.writePrefs
import com.creezen.tool.Constant.BROAD_LOGOUT
import com.creezen.tool.Constant.BROAD_NOTIFY
import com.creezen.tool.DataTool.toJson
import com.jayce.vexis.chat.ChatActivity
import com.jayce.vexis.chat.ChatItem

class GlobalReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "GlobalReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null)  return
        when(intent.action) {
            BROAD_LOGOUT -> "logout".toast()
            BROAD_NOTIFY -> {
                val msg = intent.getStringExtra("broadcastNotify")
                msg?.toast()
            }
            Intent.ACTION_CLOSE_SYSTEM_DIALOGS -> {
                Log.d(TAG,"write message")
                val list = arrayListOf<ChatItem>()
                list.addAll(ChatActivity.getChatList())
                list.addAll(CreezenService.getQueueMessage())
                writePrefs {
                    list.toJson()?.let { str ->
                        Log.d(TAG,"str1: $str")
                        it.putString(CreezenService.CACHE_MESSAGE, str)
                    }
                }
            }
        }
    }
}