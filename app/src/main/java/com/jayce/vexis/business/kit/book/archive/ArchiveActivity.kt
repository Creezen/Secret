package com.jayce.vexis.business.kit.book.archive

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.ActivityBookArchiveBinding
import com.jayce.vexis.domain.bean.BookArchiveEntry
import com.jayce.vexis.domain.database.ledger.BookDatabase

class ArchiveActivity : BaseActivity<ActivityBookArchiveBinding>() {

    private val scoreDao by lazy { BookDatabase.getDatabase(this).recordDao() }
    private val list = arrayListOf<BookArchiveEntry>()
    private val adapter by lazy {
        ArchiveAdapter(this, list, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
        loadData()
    }

    private fun initPage() = binding.apply {
        historyListRv.layoutManager = LinearLayoutManager(this@ArchiveActivity)
        historyListRv.adapter = adapter
    }

    private fun loadData() {
        ThreadTool.runOnMulti {
            list.clear()
            val recordList = scoreDao.getBookList()
            recordList.forEach {
                list.add(BookArchiveEntry(it.title, it.time, "this is rank!", it.id))
            }
            adapter.notifyDataSetChanged()
        }
    }
}