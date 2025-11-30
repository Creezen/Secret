package com.jayce.vexis.foundation.dynamic

import android.app.Activity
import android.os.Bundle
import com.creezen.tool.ability.api.IFragment

class DynamicActivity : Activity() {

    private val instance: IFragment by lazy {
        ModuleHelper.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}