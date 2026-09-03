package com.jayce.vexis.business.kit.digger

import android.os.Bundle
import android.text.Editable
import android.widget.TextView
import com.jayce.vexis.business.article.DifferHelper
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityDiggerBinding
import com.jayce.vexis.domain.viewmodel.DiggerViewModel
import com.jayce.vexis.foundation.TextListener
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
        ed1.addTextChangedListener(object : TextListener() {
            override fun afterTextChanged(s: Editable?) = diff()
        })
        ed2.addTextChangedListener(object : TextListener() {
            override fun afterTextChanged(s: Editable?) = diff()
        })
    }

    private fun diff() {
        binding.diffView.removeAllViews()
        val msg1 = binding.ed1.msg().split("\n")
        val msg2 = binding.ed2.msg().split("\n")
        DifferHelper.setRenderCallback {
            val text = TextView(this).apply { text = it }
            binding.diffView.addView(text)
        }
        DifferHelper.diff(msg1, msg2)
    }








}