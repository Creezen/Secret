package com.jayce.vexis.business.kit.gomoku.dialog

import android.os.Bundle
import android.view.Choreographer
import android.view.Choreographer.FrameCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.GomokuDialogSecordLayoutBinding
import com.jayce.vexis.domain.viewmodel.GomokuViewModel

class MenuSecondPageFragment : BaseFragment<GomokuDialogSecordLayoutBinding>(), FrameCallback {

    private var startTime: Long = 0L
    private val viewModel by activityViewModels<GomokuViewModel>()

    override fun onCreateView(inflater: LayoutInflater, contain: ViewGroup?, instance: Bundle?): View {
        super.onCreateView(inflater, contain, instance)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.lottie.setMinAndMaxFrame(0, 150)
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Choreographer.getInstance().removeFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (startTime == 0L) {
            startTime = frameTimeNanos
        } else {
            val ellipse = (frameTimeNanos - startTime) / 1000000
            binding.countTimer.text = viewModel.formatElapsed(ellipse)
        }
        Choreographer.getInstance().postFrameCallback(this)
    }
}