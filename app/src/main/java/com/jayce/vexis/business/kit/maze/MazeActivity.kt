package com.jayce.vexis.business.kit.maze

import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.client.SoundTool.playShortSound
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityMazeBinding

class MazeActivity : BaseActivity<ActivityMazeBinding>(), MazeStatusCallback {

    private var startTime: Long = 0

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) return
        binding.maze.setRowAndLine(100f)
        binding.maze.invalidate()
        startTime = System.currentTimeMillis()
        binding.maze.registerCallback(this)
    }

    override fun onMove() {
        playShortSound(R.raw.move)
    }

    override fun onError() {
        playShortSound(R.raw.error)
    }

    override fun onFinish() {
        val costTime = System.currentTimeMillis() - startTime
        playShortSound(R.raw.win)
        "你一共使用了${costTime / 1000f}s 的时间!".toast()
    }
}