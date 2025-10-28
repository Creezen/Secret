package com.jayce.vexis.core

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.creezen.tool.ThreadTool
import com.jayce.vexis.foundation.ability.EventHandle.initChatData
import com.jayce.vexis.foundation.ability.EventHandle.notifySocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CoreService : Service() {

    companion object {
        const val TAG = "CoreService"
        const val NAME_MESSAGE_SCOPE = "MSG_SCOPE"
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    private val binder = ConnectionBinder()

    override fun onCreate() {
        ThreadTool.registerScope(NAME_MESSAGE_SCOPE, scope)
        initChatData()
        notifySocket(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        return binder
    }

    override fun onDestroy() {
        ThreadTool.unregisterScope(NAME_MESSAGE_SCOPE)
        super.onDestroy()
    }

    inner class ConnectionBinder : Binder() {
        fun showNotification(notification: Notification) {
            startForeground(1, notification)
        }
    }
}