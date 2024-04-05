package com.ljw.secret.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ljw.secret.Common

open class BaseFragment:Fragment(), Common {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    open fun registerLauncher() {}
}