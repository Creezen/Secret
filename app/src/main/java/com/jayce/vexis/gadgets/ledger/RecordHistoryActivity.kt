package com.jayce.vexis.gadgets.ledger

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.base.BaseActivity
import com.jayce.vexis.databinding.ActivityRecordHistoryBinding
import com.jayce.vexis.gadgets.ledger.adapter.RecordHistoryAdapter
import com.jayce.vexis.gadgets.ledger.bean.RecordListItem
import com.jayce.vexis.gadgets.ledger.database.ScoreDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordHistoryActivity : BaseActivity() {

    companion object {
        const val TAG = "RecordHistoryActivity"
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
            historyListRv.layoutManager = LinearLayoutManager(this@RecordHistoryActivity)
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