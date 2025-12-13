package com.jayce.vexis.dynamic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creezen.tool.AndroidTool
import com.creezen.tool.ability.api.IFragment
import com.jayce.vexis.dynamic.databinding.ToolLayoutBinding

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
        }
    }
}