package com.jayce.vexis.core.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment <K : ViewBinding> : Fragment(), Common<K> {

    private var isUIDataInit: Boolean = false
    val binding: K by lazy {
        getBind()
    }

    final override fun getLayoutInflate(): LayoutInflater {
        return layoutInflater
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    open fun registerLauncher() {}

    override fun onResume() {
        super.onResume()
        if (!isUIDataInit) {
            onGetData(true)
            isUIDataInit = true
        } else if (isVisible) {
            onGetData(false)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) onGetData(false)
    }

    open fun onGetData(firstInit: Boolean) {}
}