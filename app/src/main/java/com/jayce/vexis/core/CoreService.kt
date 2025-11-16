package com.jayce.vexis.core

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import com.creezen.tool.NetTool.initSocket
import com.creezen.tool.NetTool.setOnlineSocket
import com.creezen.tool.ThreadTool
import com.jayce.vexis.core.SessionManager.BASE_SOCKET_PATH
import com.jayce.vexis.core.SessionManager.LOCAL_SOCKET_PORT
import com.jayce.vexis.foundation.ability.EventHandle.initChatData
import com.jayce.vexis.foundation.ability.EventHandle.notifySocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import java.net.Socket

class CoreService : Service() {

    companion object {
        const val TAG = "CoreService"
        const val NAME_MESSAGE_SCOPE = "MSG_SCOPE"
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    private val binder = ConnectionBinder()

    override fun onCreate() {
        val mScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        ThreadTool.registerScope(NAME_MESSAGE_SCOPE, mScope)
        initChatData()
        initSocket()
        ThreadTool.runOnSpecific(NAME_MESSAGE_SCOPE) {
            val socket = Socket(BASE_SOCKET_PATH, LOCAL_SOCKET_PORT)
            setOnlineSocket(socket)
            notifySocket(this)
        }.onFailure {
            Log.d("LJW", "runOnSpecific error: ${it.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("LJW", "onBind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("LJW", "onUnbind")
        stopSelf()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d("LJW", "onDestroy")
        ThreadTool.unregisterScope(NAME_MESSAGE_SCOPE)
        super.onDestroy()
    }

    inner class ConnectionBinder : Binder() {
        fun showNotification(notification: Notification) {
            startForeground(1, notification)
        }
    }
}