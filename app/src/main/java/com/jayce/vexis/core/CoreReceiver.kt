package com.jayce.vexis.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.creezen.commontool.Config.ACTION_BROADCAST_LOGOUT
import com.creezen.commontool.Config.ACTION_BROADCAST_NOTIFY

class CoreReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return
        when (intent.action) {
            ACTION_BROADCAST_LOGOUT -> { /**/ }
            ACTION_BROADCAST_NOTIFY -> {  /**/ }
        }
    }
}