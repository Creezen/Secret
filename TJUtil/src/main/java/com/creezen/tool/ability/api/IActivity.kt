package com.creezen.tool.ability.api

import android.app.Activity
import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View

abstract class IActivity {

    var host: Activity? = null
    var contextWrapper: ContextWrapper? = null

    fun injectContext(contextWrapper: ContextWrapper) {
        this.contextWrapper = contextWrapper
    }

    open fun attach(activity: Activity) {
        this.host = activity
    }

    fun getInflate(): LayoutInflater? {
        return host?.layoutInflater?.cloneInContext(contextWrapper)
    }

    abstract fun getView(): View

    abstract fun onCreate(savedInstance: Bundle?)

    fun onStart() {}

    fun onResume() {}

    fun onPause() {}

    fun onStop() {}

    fun onDestroy() {}
}