package com.jayce.vexis.business.history.panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creezen.tool.AndroidTool.msg
import com.jayce.vexis.business.history.OnViewReady
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.HistoryPanelUpdateTimeBinding

class UpdateTimeFragment : BaseFragment<HistoryPanelUpdateTimeBinding>() {

    private var onViewReady: OnViewReady? = null

    fun setOnViewReadyListener(listener: OnViewReady) {
        this.onViewReady = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewReady?.onReady(view)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initView()
        return binding.root
    }

    private fun initView() = binding.apply {
        startTime.isSelected = true
        endTime.isSelected = false
        startTime.setOnClickListener {
            startTime.isSelected = true
            endTime.isSelected = false
            picker.setTime(startTime.msg())
        }
        endTime.setOnClickListener {
            startTime.isSelected = false
            endTime.isSelected = true
            picker.setTime(endTime.msg())
        }
        picker.setOnTimePickerChange {
            val timeSting = picker.formatTime(true)
            if (startTime.isSelected) startTime.text = timeSting else endTime.text = timeSting
        }
    }
}