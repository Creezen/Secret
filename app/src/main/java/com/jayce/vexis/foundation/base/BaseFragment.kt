package com.jayce.vexis.foundation.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment <T : BaseViewModel> : Fragment(), Common {

    protected val model by lazy {
        getViewModel()
    }

    final override fun getViewModel(): T {
        return ViewModelProvider(this)[getViewModelClazz()]
    }

    open fun getViewModelClazz(): Class<T> {
        return BaseViewModel::class.java as Class<T>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    open fun registerLauncher() {}
}