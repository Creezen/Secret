package com.jayce.vexis.business.peer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.creezen.tool.DataTool
import com.creezen.tool.ThreadTool
import com.creezen.tool.ThreadTool.ui
import com.jayce.vexis.core.base.BaseFragment
import com.jayce.vexis.databinding.SageFragmentBinding
import com.jayce.vexis.foundation.Util.request
import com.jayce.vexis.foundation.bean.PeerAdviceEntry
import com.jayce.vexis.foundation.bean.SubjectTableEntry
import com.jayce.vexis.foundation.route.PeerService
import kotlinx.coroutines.Dispatchers

class PeerFragment : BaseFragment<SageFragmentBinding>() {

    companion object {
        const val TAG = "PeerFragment"
    }

    private var primaryNum = 0
    private var secordNum = 0
    private var tertiaryNum = 0

    private var isFirst: Boolean = true

    private lateinit var primaryList: List<String>
    private lateinit var secondaryList: List<List<String>>
    private lateinit var tertiaryList: List<List<List<String>>>

    private val list = arrayListOf<PeerAdviceEntry>()
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            fetchAdvice()
        }
    }

    private fun initData() {
        ThreadTool.runOnMulti(Dispatchers.IO) {
            val subjectTableEntry = DataTool.loadDataFromYAML<SubjectTableEntry>("SubjectTable") ?: return@runOnMulti
            primaryList = subjectTableEntry.discipline
            secondaryList = subjectTableEntry.category
            tertiaryList = subjectTableEntry.major
            ui { initView() }
        }
    }

    private fun initView() {
        with(binding) {
            primary.configuration(primaryList) {
                primaryNum = it
                secordNum = 0
                tertiaryNum = 0
                if (isFirst) return@configuration
                secondary.refreshData(secondaryList[it])
            }
            secondary.configuration(secondaryList[0]) {
                secordNum = it
                tertiaryNum = 0
                if (isFirst) return@configuration
                tertiary.refreshData(tertiaryList[primary.selectedItemPosition][it])
            }
            tertiary.configuration(tertiaryList[0][0]) {
                tertiaryNum = it
                fetchAdvice()
                isFirst= false
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
    }

    private fun fetchAdvice() {
        Log.d(TAG, "fetchAdvice ${primary()}  ${secondary()}  ${teriary()}")
        request<PeerService, List<PeerAdviceEntry>>({
            getAdvice(primary(),secondary(), teriary())
        }) {
            val count = list.size
            list.clear()
            list.addAll(it)
            ui {
                adapter.notifyItemRangeRemoved(0, count)
                adapter.notifyItemRangeInserted(0, it.size)
            }
        }
    }

    private fun primary(): String {
        return primaryList[primaryNum]
    }

    private fun secondary(): String {
        return secondaryList[primaryNum][secordNum]
    }

    private fun teriary(): String {
        return tertiaryList[primaryNum][secordNum][tertiaryNum]
    }
}