package com.jayce.vexis.ability.event

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class ChatEventService : Service() {

    companion object {

        private var isActivityBind: Boolean = false
        private val queue = LinkedBlockingQueue<String>()
        private var getMessageImmediate: ((String) -> Unit)? = null
        val messageLock = ReentrantLock()

        fun bindActivity() {
            isActivityBind = true
        }

        fun unbindActivity() {
            isActivityBind = false
        }
    }

    private val binder by lazy {
        ServiceBinder()
    }

    override fun onBind(intent: Intent?): IBinder {
        isActivityBind = true
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        messageLock.lock()
        unbindActivity()
        binder.unregisterReceiveMessage()
        messageLock.unlock()
        return true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        messageLock.lock()
        val data = intent?.getStringExtra("messageContent") ?: return START_NOT_STICKY
        if(!isActivityBind) {
            queue.put(data)
        } else {
            getMessageImmediate?.invoke(data)
        }
        messageLock.unlock()
        return super.onStartCommand(intent, flags, startId)
    }

    class ServiceBinder: Binder(){

        fun onBindForData(func: (LinkedBlockingQueue<String>) -> Unit) {
            func.invoke(queue)
        }

        fun registerReceiveMessage(callback: (String) -> Unit) {
            getMessageImmediate = callback
        }

        fun unregisterReceiveMessage() {
            getMessageImmediate = null
        }
    }
}