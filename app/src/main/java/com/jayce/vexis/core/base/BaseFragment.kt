package com.jayce.vexis.core.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment <K : ViewBinding> : Fragment(), Common<K> {

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
}