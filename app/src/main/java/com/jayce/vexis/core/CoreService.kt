package com.jayce.vexis.core

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.jayce.vexis.foundation.ability.EventHandle.initEventSystem
import com.jayce.vexis.foundation.ability.EventHandle.releaseEventSystem

class CoreService : Service() {

    companion object {
        const val TAG = "CoreService"
    }

    private val binder = ConnectionBinder()

    override fun onCreate() {
        initEventSystem(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopSelf()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        releaseEventSystem()
        super.onDestroy()
    }

    inner class ConnectionBinder : Binder() {
        fun showNotification(notification: Notification) {
            startForeground(1, notification)
        }
    }
}