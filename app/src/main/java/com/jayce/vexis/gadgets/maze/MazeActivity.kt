package com.jayce.vexis.gadgets.maze
import android.os.Bundle
import com.creezen.tool.AndroidTool.toast
import com.creezen.tool.SoundTool.playShortSound
import com.jayce.vexis.R
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivityMazeBinding

class MazeActivity : BaseActivity() {
    private lateinit var binding: ActivityMazeBinding
    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMazeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            binding.maze.setRowAndLine(50f)
            binding.maze.invalidate()
            startTime = System.currentTimeMillis()
            binding.maze.registerCallback(
                object : MazeStatusCallback {
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
            )
        }
    }
}
