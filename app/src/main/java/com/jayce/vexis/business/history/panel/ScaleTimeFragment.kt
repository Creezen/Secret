package com.jayce.vexis.business.history.panel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.R
import com.jayce.vexis.business.history.api.OnViewReady
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.HistoryPanelScaleTimeBinding
import com.jayce.vexis.foundation.ui.TextSeekBar

class ScaleTimeFragment : BaseFragment<HistoryPanelScaleTimeBinding>(), TextSeekBar.OnTextSeekbarChangeListener {

    private var onViewReady: OnViewReady? = null
    private var hundredNum: Int = 0
    private var tenNum: Int = 0
    private var onesNum: Int = 0

    val scale: Int
        get() {
            return hundredNum * 10000 + tenNum * 100 + onesNum
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
        hundred.progress = 0
        ten.progress = 0
        ones.progress = 1

        hundredNum = hundred.progress
        tenNum = ten.progress
        onesNum = ones.progress

        hundred.max = 99
        ten.max = 99
        ones.max = 99

        hundred.setEndText("x10000")
        ten.setEndText("x100")
        ones.setEndText("x1")

        val text = "当前倍率 $scale"
        binding.scaleTv.text = text

        hundred.setOnSeekBarChangeListener(this@ScaleTimeFragment)
        ten.setOnSeekBarChangeListener(this@ScaleTimeFragment)
        ones.setOnSeekBarChangeListener(this@ScaleTimeFragment)
    }

    override fun onTextSeekbarChange(seekBar: TextSeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.hundred -> hundredNum = progress
            R.id.ten -> tenNum = progress
            R.id.ones -> onesNum = progress
        }
        val text = "当前倍率 $scale"
        binding.scaleTv.text = text
    }

    override fun onStartTextSeekbarTouch(seekBar: TextSeekBar) { /**/ }

    override fun onStopTextSeekbarTouch(seekBar: TextSeekBar) { /**/ }
}