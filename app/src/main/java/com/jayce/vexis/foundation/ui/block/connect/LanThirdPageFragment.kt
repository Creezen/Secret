package com.jayce.vexis.foundation.ui.block.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.LanViewThirdLayoutBinding

class LanThirdPageFragment : BaseFragment<LanViewThirdLayoutBinding>() {

    private var searchIPText: String = ""

    override fun onCreateView(inflater: LayoutInflater, contain: ViewGroup?, instance: Bundle?): View {
        super.onCreateView(inflater, contain, instance)
        initData()
        initView()
        return binding.root
    }

    private fun initData() {
        searchIPText = arguments?.getString("ipAddress") ?: ""
    }

    private fun initView() {
        binding.apply {
            searchIP.text = "正在连接 $searchIPText"
        }
    }
}