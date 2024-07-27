package com.jayce.vexis

import android.app.Application
import android.content.IntentFilter
import com.creezen.tool.BaseTool
import com.creezen.tool.Constant.BROADCAST_LOGOUT

class Env: Application() {

    private val globalReceiver = GlobalReceiver()

    private val filter = IntentFilter().apply {
        addAction(BROADCAST_LOGOUT)
    }

    override fun onCreate() {
        super.onCreate()
        BaseTool.register(applicationContext)
        registerReceiver(globalReceiver, filter, RECEIVER_NOT_EXPORTED)
    }
}