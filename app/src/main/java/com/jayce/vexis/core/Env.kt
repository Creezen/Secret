package com.jayce.vexis.core

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import com.creezen.tool.AndroidTool
import com.creezen.tool.BaseTool
import com.creezen.tool.BaseTool.setFont
import com.creezen.tool.bean.InitParam
import com.jayce.vexis.BuildConfig
import com.jayce.vexis.core.Config.API_BASE_URL
import com.jayce.vexis.core.Config.BROAD_LOGOUT
import com.jayce.vexis.core.Config.BROAD_NOTIFY

class Env : Application() {

    companion object {
        const val TAG = "Env"
    }

    private val coreReceiver = CoreReceiver()

    private val filter = IntentFilter().apply {
            addAction(BROAD_LOGOUT)
            addAction(BROAD_NOTIFY)
            addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        }

    override fun onCreate() {
        super.onCreate()
        val param = InitParam(
            BuildConfig.socketPort,
            BuildConfig.socketUrl,
            BuildConfig.baseUrl,
            API_BASE_URL
        )
        BaseTool.init(applicationContext, param)

        val font = AndroidTool.readPrefs {
                it.getString("font", "华文行楷")
            }
        setFont(font as String)
        registerReceiver(coreReceiver, filter, RECEIVER_NOT_EXPORTED)
    }
}