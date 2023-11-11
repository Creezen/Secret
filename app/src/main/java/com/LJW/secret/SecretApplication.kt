package com.LJW.secret

import android.app.Application
import android.content.Context

class SecretApplication : Application() {
    companion object{
        lateinit var context : Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Env.applicationContext = context
    }
}