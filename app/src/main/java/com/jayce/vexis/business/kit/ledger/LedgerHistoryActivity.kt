package com.jayce.vexis.business.kit.ledger

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.business.kit.ledger.adapter.RecordHistoryAdapter
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityRecordHistoryBinding
import com.jayce.vexis.foundation.bean.RecordListEntry
import com.jayce.vexis.foundation.database.ledger.ScoreDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LedgerHistoryActivity : BaseActivity<ActivityRecordHistoryBinding>() {

    companion object {
        const val TAG = "LedgerHistoryActivity"
    }

    private val scoreDao by lazy {
        ScoreDatabase.getDatabase(this).recordDao()
    }
    private val list = arrayListOf<RecordListEntry>()
    private val adapter by lazy {
        RecordHistoryAdapter(this, list, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
        loadData()
    }

    private fun initPage() {
        with(binding) {
            historyListRv.layoutManager = LinearLayoutManager(this@LedgerHistoryActivity)
            historyListRv.adapter = adapter
        }
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            list.clear()
            val recordList = scoreDao.getRecordList()
            recordList.forEach {
                list.add(RecordListEntry(it.title, it.time, "this is rank!", it.id))
            }
            adapter.notifyDataSetChanged()

        }
    }
}