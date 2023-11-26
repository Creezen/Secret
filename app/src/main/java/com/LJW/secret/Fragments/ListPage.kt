package com.ljw.secret.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ljw.secret.databinding.MainFunctionsBinding

class ListPage:BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val binding=MainFunctionsBinding.inflate(inflater)
            return binding.root
        }
}