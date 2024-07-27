package com.jayce.vexis

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.jayce.vexis.Common

open class BaseFragment:Fragment(), Common {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    open fun registerLauncher() {}
}