package com.jayce.vexis.business.kit.gomoku

import android.os.Bundle
import com.jayce.vexis.foundation.base.BaseActivity
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
