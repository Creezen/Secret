package com.jayce.vexis

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.creezen.commontool.CreezenTool.toTime
import com.creezen.tool.AndroidTool.readPrefs
import com.creezen.tool.AndroidTool.writePrefs
import com.creezen.tool.BaseTool
import com.creezen.tool.DataTool.toData
import com.creezen.tool.DataTool.toJson
import com.creezen.tool.NetTool
import com.creezen.tool.ThreadTool
import com.google.gson.reflect.TypeToken
import com.jayce.vexis.ability.event.EventHandler
import com.jayce.vexis.chat.ChatActivity
import com.jayce.vexis.chat.ChatItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.LinkedBlockingQueue

class CreezenService : Service() {

   companion object {
       const val TAG = "CreezenService"
       const val NAME_MESSAGE_SCOPE = "MSG_SCOPE"
       const val CACHE_MESSAGE = "CACHE_MESSAGE"
       val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
       private val chatQueue = LinkedBlockingQueue<ChatItem>()

       fun getChatMessage(block: (LinkedBlockingQueue<ChatItem>) -> Unit) {
           block.invoke(chatQueue)
       }

       fun getQueueMessage(): List<ChatItem> {
           return chatQueue.toList()
       }
   }

    override fun onCreate() {
        Log.d(TAG,"onCreate")
        ThreadTool.registerScope(NAME_MESSAGE_SCOPE, scope)
        initData()
        sendNotification()
        notifySocket()
    }

    private fun initData()  {
        val data = readPrefs {
            it.getString(CACHE_MESSAGE, ArrayList<ChatItem>().toJson())
        }
        Log.d(TAG,"data: $data")
        chatQueue.clear()
        data?.toData<ArrayList<ChatItem>>().let {
            it?.forEach {
                chatQueue.put(it)
            }
        }
    }

    private fun sendNotification() {
        val notifyChannel = NotificationChannel("1", "login", NotificationManager.IMPORTANCE_HIGH)
        val builder = NotificationCompat.Builder(BaseTool.env(), "1")
            .setSmallIcon(R.drawable.tianji)
            .setContentTitle("登录成功通知")
            .setContentText("欢迎您，${onlineUser.nickname}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setOngoing(true)
            .build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(notifyChannel)
        startForeground(1, builder)
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
        ThreadTool.runOnSpecific(NAME_MESSAGE_SCOPE) {
            EventHandler.chatFlow.collect {
                chatQueue.put(ChatItem(onlineUser.nickname, System.currentTimeMillis().toTime(), it))
            }
        }
    }

    override fun onDestroy() {
        ThreadTool.unregisterScope(NAME_MESSAGE_SCOPE)
        super.onDestroy()
    }
}