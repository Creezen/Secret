package com.jayce.vexis.foundation.ui.block.connect

import android.os.Bundle
import android.view.Choreographer
import android.view.Choreographer.FrameCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.client.NetTool.formatElapsed
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.LanViewSecordLayoutBinding
import com.jayce.vexis.foundation.ability.socket.LanManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LanSecondPageFragment : BaseFragment<LanViewSecordLayoutBinding>(), FrameCallback, KoinComponent {

    private var startTime: Long = 0L
    private val manager by inject<LanManager>()

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
            binding.countTimer.text = formatElapsed(ellipse)
        }
        Choreographer.getInstance().postFrameCallback(this)
    }
}