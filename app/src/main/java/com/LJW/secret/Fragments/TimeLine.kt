package com.ljw.secret.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ljw.secret.databinding.TimeLineBinding

class TimeLine:BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val binding=TimeLineBinding.inflate(inflater)
            return binding.root
        }
}