package com.ljw.util

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

@SuppressLint("StaticFieldLeak")
object TUtil {

    private lateinit var envContext: Context

    fun register(context: Context) {
        envContext = context
    }

    fun <T> T.toast() {
        Toast.makeText(envContext,"$this", Toast.LENGTH_LONG).show()
    }

    fun getTJEnv(): Context {
        return envContext
    }

}