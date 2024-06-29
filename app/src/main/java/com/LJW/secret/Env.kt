package com.ljw.secret

import android.app.Application
import com.ljw.util.TUtil
import java.util.concurrent.atomic.AtomicBoolean

class Env : Application() {

    companion object{
        val isInit = AtomicBoolean(false)
    }

    override fun onCreate() {
        super.onCreate()
        TUtil.register(applicationContext)
        isInit.set(true)
    }
}