package com.jayce.vexis.foundation.base

import android.os.Bundle
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment(), Common {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    open fun registerLauncher() {}
}