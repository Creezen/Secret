package com.ljw.secret.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.view.size
import com.ljw.secret.Consts
import com.ljw.secret.R
import com.ljw.secret.config
import com.ljw.secret.databinding.YouqIaBinding

class YQIA:BaseFragment() {

    private val primaryList = Consts.getPrimarySubjectList()
    private val secondaryList = Consts.getSecondSubjextList()
    private val tertiaryList = Consts.getTertiarySubjectList()

    private lateinit var binding: YouqIaBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=YouqIaBinding.inflate(inflater)
        initView()
        return binding.root
    }

    fun initView(){
        with(binding){
            primary.config(primaryList){
                secondary.refreshSpinner {
                   secondaryList[primary.selectedItemPosition]
                }
                tertiary.refreshSpinner {
                    tertiaryList[primary.selectedItemPosition][0]
                }
            }
            secondary.config(secondaryList[0]){
                tertiary.refreshSpinner { tertiaryList[primary.selectedItemPosition][secondary.selectedItemPosition] }
            }
            tertiary.config(tertiaryList[0][0]){}
        }
    }

    private fun Spinner.refreshSpinner(dataSource: ()-> List<String>){
        var adapter = this.adapter as ArrayAdapter<String>
        adapter.clear()
        adapter.addAll(dataSource())
        adapter.notifyDataSetChanged()
        this.setSelection(0)
    }
}