package com.creezen.tool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface

@SuppressLint("StaticFieldLeak")
object BaseTool {

    private lateinit var envContext: Context

    fun init(context: Context, initParam: InitParam) {
        envContext = context
        AndroidTool.init()
        DataTool.init()
        FileTool.init()
        NetTool.init(initParam)
        SoundTool.init()
        ThreadTool.init()
    }

    fun env(): Context {
        return envContext
    }

    fun setFont(file: String) {
        val obj = Typeface.createFromAsset(envContext.assets, "fonts/$file.ttf")
        val field = Typeface::class.java.getField("MONOSPACE")
        field.isAccessible = true
        field.set(this, obj)
    }

    fun restartApp() {
        with(envContext) {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

    data class InitParam(
        val socketPort: Int = 0,
        val baseSocketPath: String = "",
        val baseUrl: String = "",
        val apiBaseUrl: String = ""
    )
}