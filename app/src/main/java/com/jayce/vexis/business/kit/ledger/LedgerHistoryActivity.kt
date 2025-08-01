package com.jayce.vexis.business.kit.ledger

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.foundation.base.BaseActivity
import com.jayce.vexis.databinding.ActivityRecordHistoryBinding
import com.jayce.vexis.business.kit.ledger.adapter.RecordHistoryAdapter
import com.jayce.vexis.business.kit.ledger.bean.RecordListItem
import com.jayce.vexis.business.kit.ledger.database.ScoreDatabase
import com.jayce.vexis.foundation.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LedgerHistoryActivity : BaseActivity<BaseViewModel>() {

    companion object {
        const val TAG = "LedgerHistoryActivity"
    }

    private lateinit var binding: ActivityRecordHistoryBinding
    private val scoreDao by lazy {
        ScoreDatabase.getDatabase(this).recordDao()
    }
    private val list = arrayListOf<RecordListItem>()
    private val adapter by lazy {
        RecordHistoryAdapter(this, list, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                list.add(RecordListItem(it.title, it.time, "this is rank!", it.id))
            }
            adapter.notifyDataSetChanged()

        }
    }
}