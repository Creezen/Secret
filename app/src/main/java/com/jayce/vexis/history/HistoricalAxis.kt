package com.jayce.vexis.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.base.BaseFragment
import com.jayce.vexis.databinding.TimeLineBinding

class HistoricalAxis : BaseFragment() {

    private lateinit var binding: TimeLineBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = TimeLineBinding.inflate(inflater)
        initView()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        with(binding) {
            base.setOnTouchListener { _, event ->
                val param = center.layoutParams
                param.height = event.y.toInt()
                center.layoutParams = param
                return@setOnTouchListener true
            }
        }
    }

}