package com.jayce.vexis.foundation.dynamic

import android.app.Activity
import android.os.Bundle
import com.creezen.tool.ability.api.IActivity

class DynamicActivity : Activity() {

    private var instance: IActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent()
        instance?.attach(this)
        instance?.onCreate(savedInstanceState)
        setContentView(instance?.getView())
    }

    private fun handleIntent() {
        val className = intent.getStringExtra("className") ?: ""
        instance = ModuleHelper.getActivity(className)
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
}