package com.ljw.secret.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.ljw.secret.Constant
import com.ljw.secret.util.config
import com.ljw.secret.databinding.YouqIaBinding

class YQIA:BaseFragment() {

    private val primaryList = Constant.getPrimarySubjectList()
    private val secondaryList = Constant.getSecondSubjectList()
    private val tertiaryList = Constant.getTertiarySubjectList()

    private lateinit var binding: YouqIaBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding=YouqIaBinding.inflate(inflater)
        initView()
        return binding.root
    }

    private fun initView(){
        with(binding){
            primary.config(primaryList){
                secondary.refreshSpinner {
                   secondaryList[primary.selectedItemPosition]
                }
                tertiary.refreshSpinner {
                    tertiaryList[primary.selectedItemPosition][0]
                }
                val textShow = "${binding.primary.selectedItem}/${binding.secondary.selectedItem}/${binding.tertiary.selectedItem}"
                binding.text.text = textShow
            }
            secondary.config(secondaryList[0]){
                tertiary.refreshSpinner {
                    tertiaryList[primary.selectedItemPosition][secondary.selectedItemPosition]
                }
                val textShow = "${binding.primary.selectedItem}/${binding.secondary.selectedItem}/${binding.tertiary.selectedItem}"
                binding.text.text = textShow
            }
            tertiary.config(tertiaryList[0][0]){
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
}