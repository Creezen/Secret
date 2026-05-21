package com.jayce.vexis.core

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.jayce.vexis.foundation.ability.EventRepository
import org.koin.android.ext.android.inject

class CoreService : Service() {

    private val binder = ConnectionBinder()
    private val repository by inject<EventRepository>()

    override fun onCreate() {
        repository.initEventSystem()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopSelf()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        repository.releaseEventSystem()
        super.onDestroy()
    }

    inner class ConnectionBinder : Binder() {
        fun showNotification(notification: Notification) {
            startForeground(1, notification)
        }
    }
}