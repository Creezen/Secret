package com.jayce.vexis.business.kit.gomoku

import android.os.Bundle
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityGomokuBinding

class GomokuActivity : BaseActivity<ActivityGomokuBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
    }

    private fun initPage() {}
}