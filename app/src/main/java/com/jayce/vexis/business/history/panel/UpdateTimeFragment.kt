package com.jayce.vexis.business.history.panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.jayce.vexis.business.history.api.OnViewReady
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.client.ThreadTool.runOnIO
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.HistoryPanelUpdateTimeBinding
import com.jayce.vexis.domain.viewmodel.HistoryViewModel

class UpdateTimeFragment : BaseFragment<HistoryPanelUpdateTimeBinding>() {

    private val viewModel by viewModels<HistoryViewModel>(
        ownerProducer = { requireParentFragment() }
    )

    private var onViewReady: OnViewReady? = null

    fun setOnViewReadyListener(listener: OnViewReady) { this.onViewReady = listener }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewReady?.onReady(view)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        initView()
        return binding.root
    }

    private fun initView() = binding.apply {
        startTime.isSelected = true
        endTime.isSelected = false

        val startValue = viewModel.startTime.formatString(7)
        val endValue = viewModel.endTime.formatString(7)
        startTime.text = startValue
        endTime.text = endValue
        picker.post { picker.setTime(startValue) }

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
                runOnIO { viewModel.setStartTime(picker.time()) }
            } else {
                endTime.text = timeSting
                runOnIO { viewModel.setEndTime(picker.time()) }
            }
        }
    }
}