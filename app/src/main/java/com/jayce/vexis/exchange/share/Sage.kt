package com.jayce.vexis.exchange.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creezen.tool.Constant.getPrimarySubjectList
import com.creezen.tool.Constant.getSecondSubjectList
import com.creezen.tool.Constant.getTertiarySubjectList
import com.jayce.vexis.base.BaseFragment
import com.jayce.vexis.databinding.SageFragmentBinding

class Sage: BaseFragment() {

    private val primaryList = getPrimarySubjectList()
    private val secondaryList = getSecondSubjectList()
    private val tertiaryList = getTertiarySubjectList()

    private lateinit var binding: SageFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SageFragmentBinding.inflate(inflater)
        initView()
        return binding.root
    }

    private fun initView(){
        with(binding){
            primary.configuration(primaryList){
                secondary.refreshData(secondaryList[primary.selectedItemPosition])
                tertiary.refreshData(tertiaryList[primary.selectedItemPosition][0])
                val textShow = "${binding.primary.selectedItem}/${binding.secondary.selectedItem}/${binding.tertiary.selectedItem}"
                binding.text.text = textShow
            }
            secondary.configuration(secondaryList[0]){
                tertiary.refreshData(tertiaryList[primary.selectedItemPosition][it])
                val textShow = "${binding.primary.selectedItem}/${binding.secondary.selectedItem}/${binding.tertiary.selectedItem}"
                binding.text.text = textShow
            }
            tertiary.configuration(tertiaryList[0][0]){
                val textShow = "${binding.primary.selectedItem}/${binding.secondary.selectedItem}/${binding.tertiary.selectedItem}"
                binding.text.text = textShow
            }
        }
    }
}