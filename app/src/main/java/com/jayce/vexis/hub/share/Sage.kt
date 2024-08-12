package com.jayce.vexis.hub.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.creezen.tool.AndroidTool.init
import com.creezen.tool.Constant.getPrimarySubjectList
import com.creezen.tool.Constant.getSecondSubjectList
import com.creezen.tool.Constant.getTertiarySubjectList
import com.jayce.vexis.base.BaseFragment
import com.jayce.vexis.R
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
            primary.init(
                primaryList,
                R.layout.spinner_prompt,
                R.layout.spinner_dropdown
            ){
                secondary.refreshSpinner {
                   secondaryList[primary.selectedItemPosition]
                }
                tertiary.refreshSpinner {
                    tertiaryList[primary.selectedItemPosition][0]
                }
                val textShow = "${binding.primary.selectedItem}/${binding.secondary.selectedItem}/${binding.tertiary.selectedItem}"
                binding.text.text = textShow
            }
            secondary.init(
                secondaryList[0],
                R.layout.spinner_prompt,
                R.layout.spinner_dropdown
            ){
                tertiary.refreshSpinner {
                    tertiaryList[primary.selectedItemPosition][secondary.selectedItemPosition]
                }
                val textShow = "${binding.primary.selectedItem}/${binding.secondary.selectedItem}/${binding.tertiary.selectedItem}"
                binding.text.text = textShow
            }
            tertiary.init(
                tertiaryList[0][0],
                R.layout.spinner_prompt,
                R.layout.spinner_dropdown
            ){
                val textShow = "${binding.primary.selectedItem}/${binding.secondary.selectedItem}/${binding.tertiary.selectedItem}"
                binding.text.text = textShow
            }
        }
    }

    private fun Spinner.refreshSpinner(dataSource: ()-> List<String>){
        val adapter = this.adapter as ArrayAdapter<String>
        adapter.clear()
        adapter.addAll(dataSource())
        adapter.notifyDataSetChanged()
        this.setSelection(0)
    }

//    private fun test() {
//        val a = binding.longText
//        binding.button.setOnClickListener {
//            val textValue = binding.edit.text.toString()
//            val list = textValue.split("\n")
//                .asSequence()
//                .filterNot {
//                    it.isEmpty()
//                }.map {
//                    it.trim()
//                }.take(10)
//            list.forEach{
//                Log.e("test", "the value: $it")
//            }
//            RandomAccessFile("filepath", "r").use {
//                it.seek(it.filePointer)
//                Channels.newInputStream(it.channel).bufferedReader().useLines {
//
//                }
//            }
//            val file = File("ddd")
//
//            file.useLines {
//                it.filterNot {
//                    it.isEmpty()
//                }.map {
//                    it.trim()
//                }
//            }
//            a.longText = textValue
//            binding.tv.text = "${list.count()}"
//        }
//    }
}