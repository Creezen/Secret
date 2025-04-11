package com.jayce.vexis.gadgets.gomoku

import android.os.Bundle
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivityGomokuBinding

class GomokuActivity : BaseActivity() {
    private lateinit var binding: ActivityGomokuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGomokuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
    }

    private fun initPage() {}
}
