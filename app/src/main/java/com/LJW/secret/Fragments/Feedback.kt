package com.LJW.secret.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.LJW.secret.databinding.FeedbackBinding

class Feedback:BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding=FeedbackBinding.inflate(inflater)
        with(binding){

        }
        return binding.root
    }
}