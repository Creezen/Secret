package com.ljw.secret.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ljw.secret.databinding.FeedbackBinding

class Feedback:BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding=FeedbackBinding.inflate(inflater)
        with(binding){

        }
        return binding.root
    }
}