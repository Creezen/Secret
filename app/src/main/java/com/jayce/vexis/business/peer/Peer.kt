package com.jayce.vexis.business.peer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.creezen.tool.DataTool
import com.creezen.tool.ThreadTool
import com.jayce.vexis.foundation.base.BaseFragment
import com.jayce.vexis.databinding.SageFragmentBinding
import com.jayce.vexis.foundation.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Peer : BaseFragment<BaseViewModel>() {

    companion object {
        const val TAG = "Peer"
    }

    private var primaryNum = 0
    private var secordNum = 0
    private var tertiaryNum = 0

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
        return binding.root
    }

    private fun initData() {
        ThreadTool.runOnMulti(Dispatchers.IO) {
            val subjectTable = DataTool.loadDataFromYAML<SubjectTable>("SubjectTable") ?: return@runOnMulti
            primaryList = subjectTable.discipline
            secondaryList = subjectTable.category
            tertiaryList = subjectTable.major
            withContext(Dispatchers.Main) {
                initView()
            }
        }
    }

    private fun initView() {
        with(binding) {
            primary.configuration(primaryList) {
                secondary.refreshData(secondaryList[it])
                tertiary.refreshData(tertiaryList[it][0])
                primaryNum = it
                secordNum = 0
                tertiaryNum = 0
            }
            secondary.configuration(secondaryList[0]) {
                tertiary.refreshData(tertiaryList[primary.selectedItemPosition][it])
                secordNum = it
                tertiaryNum = 0
            }
            tertiary.configuration(tertiaryList[0][0]) {
                tertiaryNum = it
            }
            advice.setOnClickListener {
                val intent = Intent(context, AdviceActivity::class.java)
                intent.putExtra("primary", primaryList[primaryNum])
                intent.putExtra("secord", secondaryList[primaryNum][secordNum])
                intent.putExtra("tertiary", tertiaryList[primaryNum][secordNum][tertiaryNum])
                startActivity(intent)
            }
        }
    }

    private fun fetchAdvice() {
        ThreadTool.runOnMulti(Dispatchers.IO) {

        }
    }
}