package com.jayce.vexis.business.kit.gomoku.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.GomokuDialogThirdLayoutBinding
import com.jayce.vexis.domain.viewmodel.GomokuViewModel

class MenuThirdPageFragment : BaseFragment<GomokuDialogThirdLayoutBinding>() {

    private var searchIPText: String = ""
    private val viewModel by activityViewModels<GomokuViewModel>()

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