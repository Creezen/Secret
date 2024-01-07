package com.ljw.secret

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class Env : Application() {

    companion object{
        lateinit var context : Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}