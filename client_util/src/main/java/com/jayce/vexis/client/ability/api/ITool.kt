package com.jayce.vexis.client.ability.api

import android.content.Context

abstract class ITool {

    protected lateinit var context: Context

    fun injectContext(hostContext: Context) {
        context = hostContext
    }

    abstract fun init()
}