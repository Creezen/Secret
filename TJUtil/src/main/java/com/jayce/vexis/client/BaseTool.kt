package com.jayce.vexis.client

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import com.jayce.vexis.util.Config.NIL

@SuppressLint("StaticFieldLeak")
object BaseTool {

    lateinit var envContext: Context

    fun init(context: Context, initParam: InitParam) {
        envContext = context
        AndroidTool.init()
        DataTool.init()
        FileTool.init()
        NetTool.init(initParam)
        SoundTool.init()
        ThreadTool.init(initParam)
    }

    fun setFont(file: String) {
        val obj = Typeface.createFromAsset(envContext.assets, "fonts/$file.ttf")
        val field = Typeface::class.java.getDeclaredField("MONOSPACE")
        field.isAccessible = true
        field.set(null, obj)
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
        val baseSocketPath: String = NIL,
        val baseUrl: String = NIL,
        val debugNetwork: Boolean,
        val debugThread: Boolean,
        val debugImage: Boolean
    )
}