package com.jayce.vexis.client.ability.api

import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts

abstract class IActivity<I, O> {

    var host: Activity? = null
    lateinit var contextWrapper: ContextWrapper
    var launcher: ActivityResultLauncher<I>? = null

    fun getContext() = contextWrapper

    fun injectContext(contextWrapper: ContextWrapper) {
        this.contextWrapper = contextWrapper
    }

    open fun attach(activity: Activity) {
        this.host = activity
    }

    fun getInflate(): LayoutInflater? {
        return host?.layoutInflater?.cloneInContext(contextWrapper)
    }

    open fun getContract(): ActivityResultContract<I, O>? = null

    open fun onContractCallback(data: O) {}

    fun openFile(): ActivityResultContract<Array<String>, Uri?> {
        return ActivityResultContracts.OpenDocument()
    }

    fun getPermission(): ActivityResultContract<Array<String>, Map<String, Boolean>> {
        return ActivityResultContracts.RequestMultiplePermissions()
    }

    fun startActivity(): ActivityResultContract<Intent, ActivityResult> {
        return ActivityResultContracts.StartActivityForResult()
    }

    fun registerLauncher(caller: ActivityResultCaller) {
        val contract = getContract() ?: return
        launcher = caller.registerForActivityResult(contract) {
            onContractCallback(it)
        }
    }

    abstract fun getView(): View

    abstract fun onCreate(savedInstance: Bundle?)

    open fun onStart() {}

    open fun onResume() {}

    open fun onPause() {}

    open fun onStop() {}

    open fun onDestroy() {}
}