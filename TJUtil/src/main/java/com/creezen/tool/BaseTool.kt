package com.creezen.tool

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

@SuppressLint("StaticFieldLeak")
object BaseTool {

    private lateinit var envContext: Context

    fun register(context: Context) {
        envContext = context
    }

    fun getTJEnv(): Context {
        return envContext
    }
}