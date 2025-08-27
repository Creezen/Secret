package com.jayce.vexis.business.kit.gomoku

import android.os.Bundle
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityGomokuBinding
import com.jayce.vexis.core.base.BaseViewModel

class GomokuActivity : BaseActivity<BaseViewModel>() {

    private lateinit var binding: ActivityGomokuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGomokuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPage()
    }

    private fun initPage() {}
}