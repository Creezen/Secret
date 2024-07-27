package com.jayce.vexis.chronicle

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.BaseFragment
import com.jayce.vexis.databinding.TimeLineBinding

class TimeLine : BaseFragment() {

    private lateinit var binding: TimeLineBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = TimeLineBinding.inflate(inflater)
        initView()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        with(binding) {
            base.setOnTouchListener { view, motionEvent ->
                val param = center.layoutParams
                param.height = motionEvent.y.toInt()
                center.layoutParams = param
                return@setOnTouchListener true
            }
        }
    }

}