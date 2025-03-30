package com.jayce.vexis

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.metrics.Event
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.creezen.tool.BaseTool
import com.creezen.tool.NetTool
import com.jayce.vexis.ability.event.EventHandler
import com.jayce.vexis.chat.ChatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CreezenService : Service() {

   companion object {
       const val TAG = "CreezenService"
       val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
   }

    override fun onCreate() {
        Log.d(TAG,"onCreate")
        val notifyChannel = NotificationChannel("1", "login", NotificationManager.IMPORTANCE_HIGH)
        val builder = NotificationCompat.Builder(BaseTool.env(), "1")
            .setSmallIcon(R.drawable.tianji)
            .setContentTitle("登录成功通知")
            .setContentText("欢迎您，${onlineUser.nickname}")
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(notifyChannel)
        startForeground(1, builder)
        notifySocket()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG,"onBind")
        return null
    }

    private fun notifySocket() {
        NetTool.sendAckMessage(scope, onlineUser.userId) {
            EventHandler.dispatchEvent(it, this)
            return@sendAckMessage true
        }
//        scope.launch {
//            EventHandler.chatFlow.collect {
//            }
//        }
    }
}