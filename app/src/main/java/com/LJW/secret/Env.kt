package com.ljw.secret

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import java.util.concurrent.atomic.AtomicBoolean

class Env : Application() {

    companion object{
        val isInit = AtomicBoolean(false)
        @SuppressLint("StaticFieldLeak")
        lateinit var context : Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        isInit.set(true)
    }
}