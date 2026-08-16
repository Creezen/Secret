package com.jayce.vexis.ai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.client.AndroidTool
import com.jayce.vexis.client.ability.api.IFragment
import com.jayce.vexis.ai.AIUtil.aiModel
import com.jayce.vexis.ai.databinding.ToolLayoutBinding

class ToolFragment : IFragment() {

    private var binding: ToolLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ToolLayoutBinding.inflate(layoutInflater)
        initView()
        return binding?.root
    }

    private fun initView() {
        binding?.apply {
            text.setOnClickListener {
                AndroidTool.startActivity(JumpActivity::class.java)
            }
            buttonDynamic.setOnClickListener {
                context?.let { aiModel(it) }
            }
        }
    }
}