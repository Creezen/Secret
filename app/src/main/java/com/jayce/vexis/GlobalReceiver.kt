package com.jayce.vexis

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.Constant.BROAD_LOGOUT
import com.creezen.tool.Constant.BROAD_NOTIFY

class GlobalReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null)  return
        when(intent.action) {
            BROAD_LOGOUT -> "logout".toast()
            BROAD_NOTIFY -> {
                val msg = intent.getStringExtra("broadcastNotify")
                msg?.toast()
            }
        }
    }
}