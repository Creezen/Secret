package com.jayce.vexis.senior

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creezen.tool.DataTool
import com.jayce.vexis.base.BaseFragment
import com.jayce.vexis.databinding.SageFragmentBinding
import com.jayce.vexis.widgets.bean.SubjectTable

class Senior : BaseFragment() {

    companion object {
        const val TAG = "Senior"
    }

    private lateinit var primaryList: List<String>
    private lateinit var secondaryList: List<List<String>>
    private lateinit var tertiaryList: List<List<List<String>>>

    private lateinit var binding: SageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = SageFragmentBinding.inflate(inflater)
        initData()
        initView()
        return binding.root
    }

    private fun initData() {
        val subjectTable = DataTool.loadDataFromYAML<SubjectTable>("SubjectTable") ?: return
        primaryList = subjectTable.discipline
        secondaryList = subjectTable.category
        tertiaryList = subjectTable.major
    }

    private fun initView() {
        with(binding) {
            primary.configuration(primaryList) {
                secondary.refreshData(secondaryList[it])
                tertiary.refreshData(tertiaryList[it][0])
                val textShow = "${binding.primary.selectedItem}/${binding.secondary.selectedItem}/${binding.tertiary.selectedItem}"
                binding.text.text = textShow
            }
            secondary.configuration(secondaryList[0]) {
                tertiary.refreshData(tertiaryList[primary.selectedItemPosition][it])
                val textShow = "${binding.primary.selectedItem}/${binding.secondary.selectedItem}/${binding.tertiary.selectedItem}"
                binding.text.text = textShow
            }
            tertiary.configuration(tertiaryList[0][0]) {
                val textShow = "${binding.primary.selectedItem}/${binding.secondary.selectedItem}/${binding.tertiary.selectedItem}"
                binding.text.text = textShow
            }
        }
    }
}
