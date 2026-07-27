package com.jayce.vexis.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.amap.api.maps.MapsInitializer
import com.amap.api.services.core.ServiceSettings
import com.jayce.vexis.util.Config.ACTION_BROADCAST_LOGOUT
import com.jayce.vexis.util.Config.ACTION_BROADCAST_NOTIFY
import com.jayce.vexis.client.AndroidTool.getData
import com.jayce.vexis.client.BaseTool
import com.jayce.vexis.client.BaseTool.setFont
import com.jayce.vexis.client.ThreadTool.runOnIO
import com.jayce.vexis.BuildConfig
import com.jayce.vexis.foundation.ability.NetStatusCallback
import org.koin.core.context.startKoin

class Env : Application() {

    private val coreReceiver = CoreReceiver()

    private val filter = IntentFilter().apply {
        addAction(ACTION_BROADCAST_LOGOUT)
        addAction(ACTION_BROADCAST_NOTIFY)
    }

    private val channelInfo = listOf(
        "login" to "登录通知",
        "message" to "消息通知"
    )

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        startKoin { modules(modules) }
    }

    override fun onCreate() {
        super.onCreate()
        val param = BaseTool.InitParam(
            BuildConfig.socketPort,
            BuildConfig.socketUrl,
            BuildConfig.baseUrl,
            debugNetwork = false,
            debugThread = false,
            debugImage = false
        )
        BaseTool.init(applicationContext, param)
        ServiceSettings.updatePrivacyAgree(this, true)
        ServiceSettings.updatePrivacyShow(this, true, true)
//        MapsInitializer.setTerrainEnable(true)
        runOnIO { setFont(getData("font", "细体宋体")) }
        registerReceiver(coreReceiver, filter, RECEIVER_NOT_EXPORTED)
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        manager.registerDefaultNetworkCallback(NetStatusCallback())
        initNotification()
    }

    private fun initNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notificationChannels.forEach {
            manager.deleteNotificationChannel(it.id)
        }
        val importance = NotificationManager.IMPORTANCE_HIGH
        channelInfo.forEach {
            val notifyChannel = NotificationChannel(it.first, it.second, importance)
            notifyChannel.enableVibration(true)
            manager.createNotificationChannel(notifyChannel)
        }
    }
}