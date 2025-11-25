package com.jayce.vexis.business.file.submodule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FragmentDynamicMduleBinding
import com.jayce.vexis.foundation.bean.DynamicModuleEntry

class DynamicModuleFragment : BaseFragment<FragmentDynamicMduleBinding>() {

    private val list = arrayListOf<DynamicModuleEntry>()
    private val adapter = DynamicModuleAdapter(list)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View {
        initData()
        initView()
        return binding.root
    }

    private fun initData() {
        (0 .. 4).forEach {
            list.add(DynamicModuleEntry("下载模块$it", "$it"))
        }
    }

    private fun initView() {
        binding.apply {
            moduleListRv.adapter = adapter
            moduleListRv.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            moduleListRv.addItemDecoration(DynamicModuleDecoration(20))
        }
    }
}