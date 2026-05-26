package com.jayce.vexis.business.peer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.commontool.bean.PeerAdviceBean
import com.creezen.tool.DataTool
import com.creezen.tool.ThreadTool
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.SageFragmentBinding
import com.jayce.vexis.domain.bean.SubjectTableEntry
import com.jayce.vexis.domain.route.PeerService
import com.jayce.vexis.foundation.Util.request
import kotlinx.coroutines.Dispatchers

class PeerFragment : BaseFragment<SageFragmentBinding>() {

    companion object {
        const val TAG = "PeerFragment"
    }

    private var primaryNum = 0
    private var secordNum = 0
    private var tertiaryNum = 0

    private lateinit var primaryList: List<String>
    private lateinit var secondaryList: List<List<String>>
    private lateinit var tertiaryList: List<List<List<String>>>

    private val primaryItem
        get() = primaryList[primaryNum]
    private val secondItem
        get() = secondaryList[primaryNum][secordNum]
    private val tertiaryItem
        get() = tertiaryList[primaryNum][secordNum][tertiaryNum]

    private var isFirst: Boolean = true

    private val list = arrayListOf<PeerAdviceBean>()
    private val adapter by lazy {
        PeerAdapter(requireActivity(), list)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        initData()
        return binding.root
    }

    override fun onGetData(firstInit: Boolean) {
        super.onGetData(firstInit)
        ThreadTool.runOnMulti(Dispatchers.Main) { fetchAdvice() }
    }

    private fun initData() {
        ThreadTool.runOnMulti(Dispatchers.Main) {
            val subjectTableEntry = DataTool.loadDataFromYAML<SubjectTableEntry>("SubjectTable") ?: return@runOnMulti
            primaryList = subjectTableEntry.discipline
            secondaryList = subjectTableEntry.category
            tertiaryList = subjectTableEntry.major
            ui { initView() }
        }
    }

    private fun initView() = binding.apply {
        primary.init(primaryList) {
            primaryNum = it
            secordNum = 0
            tertiaryNum = 0
            if (isFirst) return@init
            secondary.refreshData(secondaryList[it])
        }
        secondary.init(secondaryList[0]) {
            secordNum = it
            tertiaryNum = 0
            if (isFirst) return@init
            tertiary.refreshData(tertiaryList[primary.selectedItemPosition][it])
        }
        tertiary.init(tertiaryList[0][0]) {
            tertiaryNum = it
            fetchAdvice()
            isFirst = false
        }
        advice.setOnClickListener {
            val intent = Intent(context, AdviceActivity::class.java)
            intent.putExtra("primary", primaryList[primaryNum])
            intent.putExtra("secord", secondaryList[primaryNum][secordNum])
            intent.putExtra("tertiary", tertiaryList[primaryNum][secordNum][tertiaryNum])
            startActivity(intent)
        }
        adviceRv.layoutManager = LinearLayoutManager(this@PeerFragment.context)
        adviceRv.adapter = adapter
    }

    private fun fetchAdvice() {
        request<PeerService, List<PeerAdviceBean>>(
            { getAdvice(primaryItem, secondItem, tertiaryItem) }
        ) {
            adapter.notifyDataChange(it)
        }
    }
}