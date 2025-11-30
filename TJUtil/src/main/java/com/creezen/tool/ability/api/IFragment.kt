package com.creezen.tool.ability.api

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment

open class IFragment : Fragment() {

    var contextWrapper: ContextWrapper? = null

    fun injectContext(contextWrapper: ContextWrapper) {
        this.contextWrapper = contextWrapper
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val layoutInflater = super.onGetLayoutInflater(savedInstanceState)
        return layoutInflater.cloneInContext(contextWrapper)
    }

    override fun getContext(): Context? {
        return contextWrapper
    }
}