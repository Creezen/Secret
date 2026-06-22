package com.jayce.vexis.business.peer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.util.bean.PeerAdviceBean
import com.jayce.vexis.client.DataTool
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.client.ThreadTool.ui
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.SageFragmentBinding
import com.jayce.vexis.domain.bean.SubjectTableEntry
import com.jayce.vexis.domain.route.PeerService
import com.jayce.vexis.foundation.Util.request
import kotlinx.coroutines.Dispatchers

class PeerFragment : BaseFragment<SageFragmentBinding>() {

    private var primaryNum = 0
    private var secondNum = 0
    private var tertiaryNum = 0

    private lateinit var primaryList: List<String>
    private lateinit var secondaryList: List<List<String>>
    private lateinit var tertiaryList: List<List<List<String>>>

    private val primaryItem
        get() = primaryList[primaryNum]
    private val secondItem
        get() = secondaryList[primaryNum][secondNum]
    private val tertiaryItem
        get() = tertiaryList[primaryNum][secondNum][tertiaryNum]

    private var isFirst: Boolean = true

    private val list = arrayListOf<PeerAdviceBean>()
    private val adapter by lazy {
        PeerAdapter(requireActivity(), list)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        initData()
        return binding.root
    }

    override fun onGetData(firstInit: Boolean) {
        super.onGetData(firstInit)
        ThreadTool.runOnMulti { fetchAdvice() }
    }

    private fun initData() {
        ThreadTool.runOnMulti {
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
            secondNum = 0
            tertiaryNum = 0
            if (isFirst) return@init
            secondary.refreshData(secondaryList[it])
        }
        secondary.init(secondaryList[0]) {
            secondNum = it
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
            val intent = Intent(context, PeerActivity::class.java)
            intent.putExtra("primary", primaryList[primaryNum])
            intent.putExtra("secord", secondaryList[primaryNum][secondNum])
            intent.putExtra("tertiary", tertiaryList[primaryNum][secondNum][tertiaryNum])
            startActivity(intent)
        }
        adviceRv.layoutManager = LinearLayoutManager(this@PeerFragment.context)
        adviceRv.adapter = adapter
    }

    private fun fetchAdvice() {
        request<PeerService, _>({
            getAdvice(primaryItem, secondItem, tertiaryItem)
        }) { adapter.notifyDataChange(it) }
    }
}