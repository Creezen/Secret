package com.jayce.vexis.business.kit.digger

import android.os.Bundle
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityDiggerBinding
import com.jayce.vexis.domain.viewmodel.DiggerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DiggerActivity : BaseActivity<ActivityDiggerBinding>() {

    private val viewModel by viewModel<DiggerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() = binding.apply {
//        val name = "com.jayce.vexis.ai.ToolFragment"
//        val fg = ModuleHelper.getFragment(name) ?: return@apply
//        replaceFragment(supportFragmentManager, R.id.container, fg, "dynamic")
    }
}