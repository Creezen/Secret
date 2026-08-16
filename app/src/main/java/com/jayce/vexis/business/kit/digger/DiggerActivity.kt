package com.jayce.vexis.business.kit.digger

import android.os.Bundle
import com.jayce.vexis.R
import com.jayce.vexis.client.AndroidTool.replaceFragment
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityDiggerBinding
import com.jayce.vexis.domain.viewmodel.DiggerViewModel
import com.jayce.vexis.foundation.dynamic.ModuleHelper
import org.koin.androidx.viewmodel.ext.android.viewModel

class DiggerActivity : BaseActivity<ActivityDiggerBinding>() {

    private val viewModel by viewModel<DiggerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.test()
        initView()
    }

    private fun initView() = binding.apply {
        val fg = ModuleHelper.getFragment("com.jayce.vexis.dynamic.ToolFragment") ?: return@apply
        replaceFragment(supportFragmentManager, R.id.container, fg, "dynamic")
    }
}