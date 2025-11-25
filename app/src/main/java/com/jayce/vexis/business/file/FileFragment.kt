package com.jayce.vexis.business.file

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.jayce.vexis.business.file.pool.FileContentsFragment
import com.jayce.vexis.business.file.submodule.DynamicModuleFragment
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.FileFragmentLayoutBinding

class FileFragment : BaseFragment<FileFragmentLayoutBinding>() {

    private val list = arrayListOf<Fragment>()
    private var fileAdapter: FileAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedState: Bundle?): View {
        initData()
        initView()
        return binding.root
    }

    private fun initData() {
        val fileContentsFragment = FileContentsFragment()
        val dynamicModuleFragment = DynamicModuleFragment()
        list.add(fileContentsFragment)
        list.add(dynamicModuleFragment)
    }

    private fun initView() {
        if (fileAdapter == null) {
            val manager =  if (isAdded) parentFragmentManager else null
            if (manager == null) return
            fileAdapter = FileAdapter(manager, lifecycle, list)
        }

        binding.apply {
            page.adapter = fileAdapter
            TabLayoutMediator(tab, page) { tab, pos ->
                val textView = TextView(this@FileFragment.context)
                textView.gravity = Gravity.CENTER
                textView.text = when (pos) {
                    0 -> "资源共享库"
                    1 -> "随用随下"
                    else -> ""
                }
                tab.customView = textView
            }.attach()
        }
    }
}