package com.jayce.vexis.business.file.module

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FragmentDynamicMduleBinding
import com.jayce.vexis.domain.viewmodel.FileViewModel
import kotlinx.coroutines.launch

class DynamicFragment(
    private val viewModel: FileViewModel
) : BaseFragment<FragmentDynamicMduleBinding>() {

    private val adapter by lazy { DynamicAdapter(requireContext(), listOf()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View {
        initView()
        initViewModule()
        initData()
        return binding.root
    }

    fun updateData() {}

    private fun initData() {
        viewModel.getDynamicModule()
    }

    private fun initView() {
        binding.apply {
            moduleListRv.adapter = adapter
            moduleListRv.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            moduleListRv.addItemDecoration(DynamicDecoration(20))
        }
    }

    private fun initViewModule() {
        lifecycleScope.launch {
            viewModel.dynamicFlow.collect { module ->
                adapter.notifyDataChange(module)
            }
        }
    }
}