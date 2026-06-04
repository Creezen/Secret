package com.jayce.vexis.business.history.panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creezen.tool.AndroidTool.msg
import com.creezen.tool.ThreadTool
import com.jayce.vexis.business.history.TimeManager
import com.jayce.vexis.business.history.api.OnViewReady
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.HistoryPanelUpdateTimeBinding
import com.jayce.vexis.domain.bean.TimeUnitEntry
import org.koin.android.ext.android.inject

class UpdateTimeFragment : BaseFragment<HistoryPanelUpdateTimeBinding>() {

    private var onViewReady: OnViewReady? = null
    private val manager by inject<TimeManager>()

    var updatedStartTime: TimeUnitEntry = TimeUnitEntry.zero()
    var updatedEndTime: TimeUnitEntry = TimeUnitEntry.now()

    init {
        ThreadTool.runOnMulti {
            val time = manager.getTime()
            updatedStartTime = time.first
            updatedEndTime = time.second
        }
    }

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

        val startValue = updatedStartTime.formatString(7)
        val endValue = updatedEndTime.formatString(7)
        binding.startTime.text = startValue
        binding.endTime.text = endValue
        binding.picker.post {
            binding.picker.setTime(startValue)
        }

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
            if (startTime.isSelected) {
                startTime.text = timeSting
                updatedStartTime = picker.time()
            } else {
                endTime.text = timeSting
                updatedEndTime = picker.time()
            }
        }
    }
}