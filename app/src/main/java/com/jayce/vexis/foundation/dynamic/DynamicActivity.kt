package com.jayce.vexis.foundation.dynamic

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import com.jayce.vexis.client.ModuleHelper
import com.jayce.vexis.client.ability.api.IActivity

class DynamicActivity : ComponentActivity(), ActivityResultCaller {

    private var instance: IActivity<*, *>? = null

    override fun attachBaseContext(newBase: Context?) {
        handleIntent()
        super.attachBaseContext(instance?.getContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance?.attach(this)
        instance?.registerLauncher(this)
        instance?.onCreate(savedInstanceState)
        setContentView(instance?.getView())
    }

    override fun onStart() {
        super.onStart()
        instance?.onStart()
    }

    override fun onResume() {
        super.onResume()
        instance?.onResume()
    }

    override fun onPause() {
        super.onPause()
        instance?.onPause()
    }

    override fun onStop() {
        super.onStop()
        instance?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        instance?.onDestroy()
    }

    private fun handleIntent() {
        val className = intent.getStringExtra("className") ?: ""
        instance = ModuleHelper.getActivity(className)
    }
}