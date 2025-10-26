package com.jayce.vexis.core

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkRequest
import com.amap.api.services.core.ServiceSettings
import com.creezen.commontool.Config.API_BASE_URL
import com.creezen.commontool.Config.BROAD_LOGOUT
import com.creezen.commontool.Config.BROAD_NOTIFY
import com.creezen.tool.AndroidTool
import com.creezen.tool.BaseTool
import com.creezen.tool.BaseTool.setFont
import com.creezen.tool.ThreadTool
import com.creezen.tool.bean.InitParam
import com.jayce.vexis.BuildConfig
import com.jayce.vexis.foundation.ability.NetStatusCallback
import com.jayce.vexis.foundation.viewmodel.ChatViewModel
import com.jayce.vexis.foundation.viewmodel.RegisterViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

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

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        initViewModel()
    }

    override fun onCreate() {
        super.onCreate()
        val param = InitParam(
            BuildConfig.socketPort,
            BuildConfig.socketUrl,
            BuildConfig.baseUrl,
            API_BASE_URL
        )
        ServiceSettings.updatePrivacyAgree(this, true)
        ServiceSettings.updatePrivacyShow(this, true, true)
        BaseTool.init(applicationContext, param)

        val font = AndroidTool.readPrefs {
                it.getString("font", "华文行楷")
            }
        setFont(font as String)
        registerReceiver(coreReceiver, filter, RECEIVER_NOT_EXPORTED)

        ThreadTool.runOnMulti(Dispatchers.IO) {
            val request = NetworkRequest.Builder().build()
            val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            manager.registerNetworkCallback(request, NetStatusCallback())
        }
    }

    private fun initViewModel() {
        val module = module {
            viewModel { RegisterViewModel() }
            viewModel { ChatViewModel() }
        }
        startKoin {
            modules(module)
        }
    }
}