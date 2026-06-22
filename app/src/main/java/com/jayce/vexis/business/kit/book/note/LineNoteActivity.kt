package com.jayce.vexis.business.kit.book.note

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.jayce.vexis.util.toTime
import com.jayce.vexis.client.AndroidTool.addSimpleView
import com.jayce.vexis.client.AndroidTool.msg
import com.jayce.vexis.client.AndroidTool.toast
import com.jayce.vexis.client.TLog
import com.jayce.vexis.client.ThreadTool
import com.jayce.vexis.R
import com.jayce.vexis.core.base.BaseActivity
import com.jayce.vexis.databinding.BookLineNoteBinding
import com.jayce.vexis.databinding.BookNoteDialogLineBinding
import com.jayce.vexis.databinding.BookNoteDialogUserBinding
import com.jayce.vexis.domain.bean.BookLineEntry
import com.jayce.vexis.domain.bean.RecordEntry
import com.jayce.vexis.domain.bean.BookEntry
import com.jayce.vexis.domain.database.ledger.BookDatabase
import com.jayce.vexis.foundation.ui.block.FlexibleDialog

class LineNoteActivity : BaseActivity<BookLineNoteBinding>() {

    companion object {
        const val WIDTH = 180
        const val HEIGHT = 180
    }

    private lateinit var title: String
    private lateinit var createTime: String
    private val userList = ArrayList<String>()
    private val scoreList = ArrayList<RecordEntry>()
    private val totalScoreList = ArrayList<Int>()
    private val adapter by lazy { LineAdapter(scoreList) }
    private val scoreDao by lazy { BookDatabase.getDatabase(this).recordDao() }
    private lateinit var noteDialogAdapter: NoteDialogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initView()
    }

    private fun initData() {
        userList.clear()
        val list = intent.getStringArrayListExtra("userData")
        TLog.e("get data: $list")
        intent.getStringArrayListExtra("userData")?.let { userNames ->
            userList.addAll(
                userNames.filter { it.isNotEmpty() }.toSet(),
            )
        }
        TLog.e("$userList")
        repeat(userList.size) { totalScoreList.add(0) }
        noteDialogAdapter = NoteDialogAdapter(userList)
    }

    private fun initView() = binding.apply {
        title = intent.getStringExtra("title") ?: "默认标题"
        titleTv.text = title
        createTime = intent.getLongExtra("time", 0).toTime()
        timeTv.text = createTime
        userList.forEach { name ->
            playerName.addSimpleView(name, WIDTH)
            totalScore.addSimpleView("0", WIDTH)
        }
        addUser.setOnClickListener {
            FlexibleDialog
                .flexibleView<BookNoteDialogUserBinding>(this@LineNoteActivity)
                .title("添加角色")
                .negative()
                .positive("添加") {
                    val edit = findViewById<EditText>(R.id.edit)
                    val addUserName = edit.msg()
                    playerName.addSimpleView(addUserName, WIDTH)
                    userList.add(addUserName)
                    scoreList.forEach { it.scores.add(0) }
                    adapter.notifyDataSetChanged()
                    noteDialogAdapter.notifyUserAdded()
                    totalScoreList.add(0)
                    totalScore.addSimpleView("0", WIDTH)
                }.show()
        }
        addRecord.setOnClickListener {
            FlexibleDialog
                .flexibleView<BookNoteDialogLineBinding>(this@LineNoteActivity) {
                    scoreRv.layoutManager = LinearLayoutManager(this@LineNoteActivity)
                    scoreRv.adapter = noteDialogAdapter
                }
                .positive("添加") {
                    val scoreList = noteDialogAdapter.getLineList()
                    val newList = ArrayList<Int>()
                    newList.addAll(scoreList)
                    if (newList.sum() != 0) {
                        "分数设置不合法，请检查一下哦".toast()
                    }
                    newList.forEachIndexed { index, value ->
                        totalScoreList[index] += value
                        val childView = totalScore.getChildAt(index) as TextView
                        childView.text = totalScoreList[index].toString()
                    }
                    addRecord(newList)
                }.title("设置分数").show()
        }
        save.setOnClickListener {
            saveRecord()
        }
        rv.layoutManager = LinearLayoutManager(this@LineNoteActivity)
        rv.adapter = adapter
        rvSC.setOnScrollChangeListener { _, i, i2, _, _ ->
            playerNameSV.scrollTo(i, i2)
            totalScoreSV.scrollTo(i, i2)
        }
        rv.setOnScrollChangeListener { _, _, _, oldScrollX, oldScrollY ->
            val scX = roundNumRV.scrollX
            val scY = roundNumRV.scrollY
            roundNumRV.scrollTo(scX - oldScrollX, scY - oldScrollY)
        }
        playerNameSV.setOnTouchListener { _, _ -> true }
        totalScoreSV.setOnTouchListener { _, _ -> true }
        roundNumRV.setOnTouchListener { _, _ -> true }
    }

    private fun saveRecord() {
        ThreadTool.runOnMulti {
            val bookLineEntry = BookLineEntry(title, createTime)
            val recordId = scoreDao.insertRecord(bookLineEntry)
            val userStr = StringBuilder()
            val scoreStr = StringBuilder()
            val totalStr = StringBuilder()
            userList.forEachIndexed { i, v ->
                userStr.append(v)
                if (i != userList.size - 1) {
                    userStr.append("$")
                }
            }
            totalScoreList.forEachIndexed { i, v ->
                totalStr.append(v)
                if (i != totalScoreList.size - 1) {
                    totalStr.append("$")
                }
            }
            scoreList.forEachIndexed { idx, value ->
                val roundScore = value.scores
                roundScore.forEachIndexed { i, v ->
                    scoreStr.append(v)
                    if (i != roundScore.size - 1) {
                        scoreStr.append("$")
                    }
                }
                if (idx != scoreList.size - 1) {
                    scoreStr.append("&")
                }
            }
            val bookEntry = BookEntry(recordId, userStr.toString(), scoreStr.toString(), totalStr.toString())
            scoreDao.insertScore(bookEntry)
        }
        "保存成功".toast()
    }

    private fun addRecord(list: ArrayList<Int>) {
        scoreList.add(RecordEntry("${System.currentTimeMillis()}", list))
        adapter.notifyItemInserted(scoreList.size)
        binding.roundNum.addSimpleView("${scoreList.size}", HEIGHT, WIDTH)
    }
}