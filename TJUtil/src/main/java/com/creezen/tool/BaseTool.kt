package com.creezen.tool

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface

@SuppressLint("StaticFieldLeak")
object BaseTool {

    private lateinit var envContext: Context

    fun register(context: Context) {
        envContext = context
    }

    fun env(): Context {
        return envContext
    }

    fun setFont(file: String) {
        val field = Typeface::class.java.getField("MONOSPACE")
        field.isAccessible = true
        val obj = Typeface.createFromAsset(envContext.assets, "fonts/$file.ttf")
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
}